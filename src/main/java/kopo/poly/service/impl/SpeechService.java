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

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
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
        AtomicBoolean isCompleted = new AtomicBoolean(false);

        ResponseObserver<StreamingRecognizeResponse> responseObserver = new ResponseObserver<>() {
            public void onStart(StreamController controller) {
                log.info("Streaming recognition started");
            }

            public void onResponse(StreamingRecognizeResponse response) {
                responses.add(response);
                log.info("Received response: {}", response);
            }

            public void onComplete() {
                if (!isCompleted.getAndSet(true)) {
                    log.info("onComplete called");
                    for (StreamingRecognizeResponse response : responses) {
                        StreamingRecognitionResult result = response.getResultsList().get(0);
                        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                        String transcript = alternative.getTranscript();
                        transcriptBuilder.append(transcript).append(" ");
                    }
                    log.info("Final transcript: {}", transcriptBuilder.toString());
                    latch.countDown();
                }
            }

            public void onError(Throwable t) {
                log.error("Error in response: {}", t.toString());
                isCompleted.set(true);
                latch.countDown();
            }
        };

        try (SpeechClient client = SpeechClient.create()) {
            log.info("Creating SpeechClient");
            ClientStream<StreamingRecognizeRequest> clientStream = client.streamingRecognizeCallable().splitCall(responseObserver);

            RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode("ko-KR")
                    .setSampleRateHertz(48000)
                    .build();

            StreamingRecognitionConfig streamingRecognitionConfig = StreamingRecognitionConfig.newBuilder()
                    .setConfig(recognitionConfig)
                    .build();

            StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
                    .setStreamingConfig(streamingRecognitionConfig)
                    .build();

            clientStream.send(request);
            log.info("Streaming request sent");

            AudioFormat audioFormat = new AudioFormat(Float.parseFloat(format[0]), Integer.parseInt(format[1]), Integer.parseInt(format[2]), true, false);
            DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

            if (!AudioSystem.isLineSupported(targetInfo)) {
                log.error("Microphone is not supported.");
                return "";
            }

            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            log.info("Microphone is started. Please speak.");

            long startTime = System.currentTimeMillis();
            AudioInputStream audio = new AudioInputStream(targetDataLine);
            byte[] data = new byte[6400];

            while (true) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                int bytesRead = targetDataLine.read(data, 0, data.length);

                if (bytesRead == -1 || elapsedTime > 3000 || isCompleted.get()) {
                    log.info("Stopping audio input.");
                    break;
                }

                if (bytesRead > 0) {
                    StreamingRecognizeRequest audioRequest = StreamingRecognizeRequest.newBuilder()
                            .setAudioContent(ByteString.copyFrom(data, 0, bytesRead))
                            .build();
                    clientStream.send(audioRequest);
                    log.info("Sent audio data of size: {}", bytesRead);
                }
            }

            targetDataLine.stop();
            targetDataLine.close();
            log.info("Microphone is stopped");

            clientStream.closeSend();
            log.info("Streaming ended, waiting for responses...");

            latch.await();

        } catch (Exception e) {
            log.error("Error during speech recognition: {}", e.toString());
        }

        return transcriptBuilder.toString().trim();
    }

    public String transcribeAudio(byte[] audioData) throws Exception {
        log.info("Starting transcription of audio data");
        try (SpeechClient speechClient = SpeechClient.create()) {
            ByteString audioBytes = ByteString.copyFrom(audioData);

            RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(48000)
                    .setLanguageCode("ko-KR")
                    .build();

            RecognizeResponse response = speechClient.recognize(recognitionConfig, recognitionAudio);

            log.info("API response: {}", response);

            StringBuilder transcription = new StringBuilder();

            for (SpeechRecognitionResult result : response.getResultsList()) {
                String decodedTranscript = decodeTranscript(result.getAlternativesList().get(0).getTranscript());
                log.info("result : " + result.getAlternativesList());
                transcription.append(decodedTranscript).append("\n");
            }

            log.info("Transcription completed. Result: {}", transcription);
            return transcription.toString().trim(); // 최종 전사 텍스트 반환
        }
    }
    public void testTranscribeWavFile() {
        try {
            File audioFile = new File("audio_pcm.wav");
            long fileSize = audioFile.length();
            log.info("WAV file size: {} bytes", fileSize);

            // WAV 파일로 전사 수행
            try (FileInputStream fis = new FileInputStream(audioFile)) {
                byte[] buffer = new byte[1048576]; // 10MB 버퍼
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    // 읽은 만큼의 데이터를 가지고 전사 수행
                    byte[] audioDataChunk = Arrays.copyOf(buffer, bytesRead);
                    String transcription = transcribeAudio(audioDataChunk);
                    log.info("Transcription Result for chunk: {}", transcription);
                }
            }
        } catch (IOException e) {
            log.error("Error reading WAV file: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error during transcription: {}", e.getMessage());
        }
    }
    public String decodeTranscript(String transcript) {
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < transcript.length(); i++) {
            char c = transcript.charAt(i);
            if (c == '\\' && i + 1 < transcript.length() && transcript.charAt(i + 1) == 'u') {
                // Unicode escape sequence 발견
                String hex = transcript.substring(i + 2, i + 6);
                decoded.append((char) Integer.parseInt(hex, 16));
                i += 5; // 6자 이동
            } else {
                decoded.append(c);
            }
        }
        return decoded.toString();
    }

}
