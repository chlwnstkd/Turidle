package kopo.poly.handler;

import kopo.poly.service.ISpeechService;
import kopo.poly.service.impl.SpeechService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpeechWebSocketHandler extends BinaryWebSocketHandler {

    private final SpeechService speechService;
    private FileOutputStream fileOutputStream;
    private int totalAudioLength = 0;
    private final int sampleRate = 48000; // 샘플 레이트
    private final int channels = 1; // 채널 수 (모노)
    private final int bitDepth = 16; // 비트 깊이
    private byte[] receivedDataBuffer = new byte[0];
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private WebSocketSession currentSession; // 현재 WebSocketSession 저장

    private Runnable timeoutTask = () -> {
        try {
            if (receivedDataBuffer.length > 0) {
                // 일정 시간동안 새로운 데이터가 없을 경우 파일을 완성하고 처리
                completeAndProcessWavFile();
            }
        } catch (Exception e) {
            log.error("WAV 파일 처리 중 오류 발생", e);
        }
    };

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.currentSession = session; // 현재 세션 저장

        log.info("WebSocket 연결됨, 오디오 파일 생성");

        // 1초 동안 데이터가 없을 경우 처리할 타이머 설정
        scheduler.scheduleAtFixedRate(timeoutTask, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        byte[] audioData = message.getPayload().array();
        log.info("받은 오디오 데이터 크기: " + audioData.length);

        File wavFile = new File("audio.wav");
        fileOutputStream = new FileOutputStream(wavFile);

        fileOutputStream.write(audioData);
        fileOutputStream.flush();
        totalAudioLength += audioData.length;

        byte[] newBuffer = new byte[receivedDataBuffer.length + audioData.length];
        System.arraycopy(receivedDataBuffer, 0, newBuffer, 0, receivedDataBuffer.length);
        System.arraycopy(audioData, 0, newBuffer, receivedDataBuffer.length, audioData.length);
        receivedDataBuffer = newBuffer;

        log.info("청크 데이터 수신 및 파일에 기록됨");

    }

    private void completeAndProcessWavFile() throws Exception {
        if (fileOutputStream != null) {
            fileOutputStream.close(); // 파일 출력 스트림 닫기
            log.info("WAV 파일 작성 완료");

            // PCM 형식으로 변환하기 위해 FFmpeg를 호출
            String pcmFilePath = "audio_pcm.wav"; // 저장할 PCM 파일 경로
            convertToPCM("audio.wav", pcmFilePath);

            log.info("PCM 변환 시작");

            // PCM 형식의 오디오 데이터를 읽고 전송
            byte[] pcmData = readPCMFile(pcmFilePath);
            log.info("pcm data 성공");
            String transcription = speechService.transcribeAudio(pcmData);
            log.info("transcription : " + transcription);

            // 전사된 텍스트를 클라이언트로 전송
            sendTranscription(transcription);

            receivedDataBuffer = new byte[0]; // 버퍼 초기화
            totalAudioLength = 0; // 길이 초기화

            deleteFiles("audio.wav", pcmFilePath);
            deleteFiles("audio_pcm.wav", pcmFilePath);

        }
    }

    private void sendTranscription(String transcription) {
        if (currentSession != null && currentSession.isOpen()) {
            try {
                currentSession.sendMessage(new TextMessage(transcription));
                log.info("전사 결과를 클라이언트로 전송: " + transcription);
            } catch (IOException e) {
                log.error("클라이언트로 전사 결과 전송 중 오류 발생", e);
            }
        } else {
            log.warn("현재 WebSocketSession이 닫혀 있거나 유효하지 않습니다.");
        }
    }

    private void convertToPCM(String inputFilePath, String outputFilePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-i", inputFilePath,
                    "-acodec", "pcm_s16le", // PCM 16-bit little-endian
                    "-ac", String.valueOf(channels), // 채널 수
                    "-ar", String.valueOf(sampleRate), // 샘플 레이트
                    outputFilePath
            );
            log.info("PCM 변환 설정 성공");

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("PCM 변환 완료: " + outputFilePath);
            } else {
                log.error("PCM 변환 실패. 종료 코드: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            log.error("FFmpeg 실행 중 오류 발생", e);
        }
    }

    private byte[] readPCMFile(String filePath) throws IOException {
        File pcmFile = new File(filePath);
        byte[] pcmData = new byte[(int) pcmFile.length()];

        try (FileInputStream fis = new FileInputStream(pcmFile)) {
            fis.read(pcmData);
        }

        log.info("PCM 파일 읽기 완료: " + filePath);
        return pcmData;
    }

    private void deleteFiles(String... filePaths) {
        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (file.exists() && file.delete()) {
                log.info("파일 삭제 성공: " + filePath);
            } else {
                log.warn("파일 삭제 실패 또는 파일이 존재하지 않음: " + filePath);
            }
        }
    }
}
