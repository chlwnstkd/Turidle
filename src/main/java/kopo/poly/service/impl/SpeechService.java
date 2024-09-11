package kopo.poly.service.impl;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.protobuf.ByteString;
import kopo.poly.service.ISpeechService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

@Service
@Slf4j
public class SpeechService implements ISpeechService {

    /**
     * 5초 동안 마이크로폰 스트리밍 음성 인식을 수행합니다.
     */
    @Override
    public String streamingMicRecognize() throws Exception {
        final StringBuilder transcriptBuilder = new StringBuilder();
        final CountDownLatch latch = new CountDownLatch(1);
        ArrayList<com.google.cloud.speech.v1.StreamingRecognizeResponse> responses = new ArrayList<>();

        ResponseObserver<com.google.cloud.speech.v1.StreamingRecognizeResponse> responseObserver = new ResponseObserver<com.google.cloud.speech.v1.StreamingRecognizeResponse>() {
            public void onStart(StreamController controller) {}

            public void onResponse(com.google.cloud.speech.v1.StreamingRecognizeResponse response) {
                responses.add(response);
            }

            public void onComplete() {
                log.info("onComplete called");
                for (com.google.cloud.speech.v1.StreamingRecognizeResponse response : responses) {
                    com.google.cloud.speech.v1.StreamingRecognitionResult result = response.getResultsList().get(0);
                    com.google.cloud.speech.v1.SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                    String transcript = alternative.getTranscript();
                    transcriptBuilder.append(transcript).append(" ");
                }
                log.info("transcriptBuilder : " +transcriptBuilder.toString());
                latch.countDown(); // onComplete가 끝나면 latch를 카운트다운함
            }

            public void onError(Throwable t) {
                log.error("Error in response: " + t.toString());
                latch.countDown(); // 오류 발생 시에도 latch를 카운트다운함
            }
        };

        try (com.google.cloud.speech.v1.SpeechClient client = com.google.cloud.speech.v1.SpeechClient.create()) {
            ClientStream<com.google.cloud.speech.v1.StreamingRecognizeRequest> clientStream = client.streamingRecognizeCallable().splitCall(responseObserver);

            com.google.cloud.speech.v1.RecognitionConfig recognitionConfig = com.google.cloud.speech.v1.RecognitionConfig.newBuilder()
                    .setEncoding(com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode("ko-KR")
                    .setSampleRateHertz(16000)
                    .build();

            com.google.cloud.speech.v1.StreamingRecognitionConfig streamingRecognitionConfig = com.google.cloud.speech.v1.StreamingRecognitionConfig.newBuilder()
                    .setConfig(recognitionConfig)
                    .build();

            com.google.cloud.speech.v1.StreamingRecognizeRequest request = com.google.cloud.speech.v1.StreamingRecognizeRequest.newBuilder()
                    .setStreamingConfig(streamingRecognitionConfig)
                    .build();

            clientStream.send(request);

            AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, true);
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
                int bytesRead = audio.read(data);
                if (bytesRead == -1 || elapsedTime > 5000) {
                    log.info("말씀을 멈추세요.");
                    break;
                }

                com.google.cloud.speech.v1.StreamingRecognizeRequest audioRequest = com.google.cloud.speech.v1.StreamingRecognizeRequest.newBuilder()
                        .setAudioContent(ByteString.copyFrom(data, 0, bytesRead))
                        .build();
                clientStream.send(audioRequest);
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