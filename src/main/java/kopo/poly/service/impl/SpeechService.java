package kopo.poly.service.impl;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import kopo.poly.service.ISpeechService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import com.google.cloud.speech.v1.RecognitionConfig;


import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


@Service
@Slf4j
@RequiredArgsConstructor
public class SpeechService implements ISpeechService {

    @Override
    public String streamingMicRecognize(String[] format) throws Exception {
        final StringBuilder transcriptBuilder = new StringBuilder();
        final CountDownLatch latch = new CountDownLatch(1);
        ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();
        AtomicBoolean isCompleted = new AtomicBoolean(false); // 추가된 부분

        ResponseObserver<StreamingRecognizeResponse> responseObserver = new ResponseObserver<>() {
            public void onStart(StreamController controller) {}

            public void onResponse(StreamingRecognizeResponse response) {
                responses.add(response);
            }

            public void onComplete() {
                if (!isCompleted.getAndSet(true)) { // 완료 상태를 확인
                    log.info("onComplete called");
                    for (StreamingRecognizeResponse response : responses) {
                        StreamingRecognitionResult result = response.getResultsList().get(0);
                        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                        String transcript = alternative.getTranscript();
                        transcriptBuilder.append(transcript).append(" ");
                    }
                    log.info("transcriptBuilder : " + transcriptBuilder.toString());
                    latch.countDown(); // onComplete가 끝나면 latch를 카운트다운함
                }
            }

            public void onError(Throwable t) {
                log.error("Error in response: " + t.toString());
                isCompleted.set(true); // 오류 발생 시에도 완료 상태를 설정
                latch.countDown(); // 오류 발생 시에도 latch를 카운트다운함
            }
        };

        try (SpeechClient client = SpeechClient.create()) {
            ClientStream<StreamingRecognizeRequest> clientStream = client.streamingRecognizeCallable().splitCall(responseObserver);

            RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode("ko-KR")
                    .setSampleRateHertz(Integer.parseInt(format[0]))
                    .build();

            StreamingRecognitionConfig streamingRecognitionConfig = StreamingRecognitionConfig.newBuilder()
                    .setConfig(recognitionConfig)
                    .build();

            StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
                    .setStreamingConfig(streamingRecognitionConfig)
                    .build();

            clientStream.send(request);

            AudioFormat audioFormat = new AudioFormat(Float.parseFloat(format[0]), Integer.parseInt(format[1]), Integer.parseInt(format[2]), true, false);
            DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

            if (!AudioSystem.isLineSupported(targetInfo)) {
                log.info("마이크로폰이 지원되지 않습니다.");
                return "";
            }

            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            log.info("말씀하세요.");

            long startTime = System.currentTimeMillis();
            AudioInputStream audio = new AudioInputStream(targetDataLine);
            byte[] data = new byte[6400];

            while (true) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                int bytesRead = targetDataLine.read(data, 0, data.length); // 수정된 부분

                if (bytesRead == -1 || elapsedTime > 3000 || isCompleted.get()) { // 3초 이상 지나면 멈추기
                    log.info("말씀을 멈추세요.");
                    break;
                }

                if (bytesRead > 0) {
                    StreamingRecognizeRequest audioRequest = StreamingRecognizeRequest.newBuilder()
                            .setAudioContent(ByteString.copyFrom(data, 0, bytesRead))
                            .build();
                    clientStream.send(audioRequest);
                }
            }

            targetDataLine.stop();
            targetDataLine.close();

            clientStream.closeSend(); // 스트리밍 종료

            // onComplete가 호출될 때까지 대기
            latch.await();

        } catch (Exception e) {
            log.error(e.toString());
        }

        return transcriptBuilder.toString().trim();
    }
}