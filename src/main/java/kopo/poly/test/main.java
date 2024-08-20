package kopo.poly.test;

import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

import javax.sound.sampled.*;
import java.util.List;

public class main {

    public static void main(String[] args) {
        try {
            realTimeSpeechRecognition();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void realTimeSpeechRecognition() throws Exception {
        // 클라이언트를 생성합니다
        try (SpeechClient speechClient = SpeechClient.create()) {

            // 오디오 포맷을 설정합니다 (16kHz, 16비트, 모노)
            AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            // 스트리밍 인식 구성 설정
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build();

            StreamingRecognitionConfig streamingRecognitionConfig = StreamingRecognitionConfig.newBuilder()
                    .setConfig(config)
                    .setInterimResults(true)
                    .build();

            // gRPC 스트림을 생성하여 실시간 오디오 데이터를 전송
            ApiStreamObserver<StreamingRecognizeRequest> requestObserver =
                    speechClient.streamingRecognizeCallable().bidiStreamingCall(new ResponseObserver());

            // 스트리밍 요청 초기화
            StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
                    .setStreamingConfig(streamingRecognitionConfig)
                    .build();
            requestObserver.onNext(request);

            // 실시간 오디오 스트림을 전송
            byte[] data = new byte[6400];
            while (true) {
                int bytesRead = targetDataLine.read(data, 0, data.length);
                if (bytesRead <= 0) {
                    break;
                }

                request = StreamingRecognizeRequest.newBuilder()
                        .setAudioContent(ByteString.copyFrom(data, 0, bytesRead))
                        .build();
                requestObserver.onNext(request);
            }

            // 스트리밍이 끝났음을 서버에 알림
            requestObserver.onCompleted();
        }
    }

    // 응답을 처리하기 위한 ApiStreamObserver 클래스
    private static class ResponseObserver implements ApiStreamObserver<StreamingRecognizeResponse> {
        @Override
        public void onNext(StreamingRecognizeResponse response) {
            for (StreamingRecognitionResult result : response.getResultsList()) {
                // 가장 가능성 높은 결과 출력
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
            }
        }

        @Override
        public void onError(Throwable t) {
            System.err.println("Error during streaming: " + t.getMessage());
        }

        @Override
        public void onCompleted() {
            System.out.println("Streaming completed.");
        }
    }
}
