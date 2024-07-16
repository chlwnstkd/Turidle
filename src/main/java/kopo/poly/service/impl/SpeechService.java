package kopo.poly.service.impl;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

import javax.sound.sampled.*;
import java.util.ArrayList;

import org.springframework.stereotype.Service;


@Service // 서비스 클래스
public class SpeechService {

    // 음성 인식을 수행하는 메서드
    public String recognizeSpeech() {
        ResponseObserver<StreamingRecognizeResponse> responseObserver = null; // 응답 옵저버
        StringBuilder transcript = new StringBuilder(); // 인식된 텍스트 저장소

        try (SpeechClient client = SpeechClient.create()) { // SpeechClient 생성
            responseObserver = new ResponseObserver<StreamingRecognizeResponse>() {
                ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>(); // 응답 리스트

                public void onStart(StreamController controller) {
                    // 스트림 시작 시 호출되는 메서드
                }

                public void onResponse(StreamingRecognizeResponse response) {
                    // 응답 수신 시 호출되는 메서드
                    responses.add(response); // 응답 리스트에 추가
                }

                public void onComplete() {
                    // 스트림 완료 시 호출되는 메서드
                    for (StreamingRecognizeResponse response : responses) {
                        StreamingRecognitionResult result = response.getResultsList().get(0); // 첫 번째 결과
                        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0); // 첫 번째 대안
                        transcript.append(alternative.getTranscript()); // 인식된 텍스트 추가
                        System.out.printf("Transcript : %s\n", alternative.getTranscript()); // 인식된 텍스트 출력
                    }
                }

                public void onError(Throwable t) {
                    // 오류 발생 시 호출되는 메서드
                    System.out.println(t); // 오류 메시지 출력
                }
            };

            ClientStream<StreamingRecognizeRequest> clientStream = client.streamingRecognizeCallable()
                    .splitCall(responseObserver); // 스트리밍 인식 클라이언트 스트림

            RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16) // 오디오 인코딩
                    .setLanguageCode("ko-KR") // 언어 코드 (한국어)
                    .setSampleRateHertz(16000) // 샘플 속도
                    .build();

            StreamingRecognitionConfig streamingRecognitionConfig = StreamingRecognitionConfig.newBuilder()
                    .setConfig(recognitionConfig) // 인식 구성
                    .build();

            StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
                    .setStreamingConfig(streamingRecognitionConfig)
                    .build(); // 첫 번째 요청

            clientStream.send(request); // 첫 번째 요청 전송

            AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false); // 오디오 형식
            DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat); // 마이크 오디오 스트림

            if (!AudioSystem.isLineSupported(targetInfo)) {
                // 마이크 지원 여부 확인
                System.out.println("Microphone not supported"); // 지원되지 않음을 알리는 메시지
                System.exit(0); // 프로그램 종료
            }

            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo); // 타겟 데이터 라인
            targetDataLine.open(audioFormat); // 데이터 라인 열기
            targetDataLine.start(); // 데이터 라인 시작
            System.out.println("Start speaking"); // 마이크 시작 메시지

            long startTime = System.currentTimeMillis(); // 시작 시간
            AudioInputStream audio = new AudioInputStream(targetDataLine); // 오디오 입력 스트림

            while (true) {
                // 무한 루프
                long estimatedTime = System.currentTimeMillis() - startTime; // 경과 시간
                byte[] data = new byte[6400]; // 오디오 데이터 배열
                audio.read(data); // 오디오 데이터 읽기
                if (estimatedTime > 6000) { // 60초 경과 시
                    System.out.println("Stop speaking."); // 마이크 종료 메시지
                    targetDataLine.stop(); // 데이터 라인 중지
                    targetDataLine.close(); // 데이터 라인 닫기
                    break; // 루프 종료
                }
                request = StreamingRecognizeRequest.newBuilder()
                        .setAudioContent(ByteString.copyFrom(data))
                        .build(); // 오디오 콘텐츠 요청
                clientStream.send(request); // 요청 전송
            }
        } catch (Exception e) {
            // 예외 처리
            System.out.println(e); // 예외 메시지 출력
        }

        responseObserver.onComplete(); // 완료 이벤트
        return transcript.toString(); // 인식된 텍스트 반환
    }
}