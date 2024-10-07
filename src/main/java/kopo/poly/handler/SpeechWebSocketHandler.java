package kopo.poly.handler;

import kopo.poly.service.ISpeechService;
import kopo.poly.service.impl.SpeechService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpeechWebSocketHandler extends BinaryWebSocketHandler {

    private final ISpeechService speechService;

    private OutputStream outputStream;
    private boolean isReceivingAudio = false;

    private static final String AUDIO_FILE_PATH = "c:\\wav\\recorded_audio.wav"; // 저장할 오디오 파일 경로

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        if (message.getPayloadLength() == 0) {
            return; // 비어 있는 메시지 처리
        }

        // 바이너리 데이터 처리
        if (isReceivingAudio) {
            byte[] audioData = message.getPayload().array();
            if (outputStream != null) {
                outputStream.write(audioData);
                outputStream.flush();
            }
        } else {
            // 오디오 수신 시작
            isReceivingAudio = true;
            outputStream = new FileOutputStream("recorded_audio.wav"); // 파일 경로
            byte[] audioData = message.getPayload().array();
            outputStream.write(audioData);
            outputStream.flush();
        }
    }


    private void saveAudioData(byte[] audioData) {

        log.info("Saving audio data to " + AUDIO_FILE_PATH);

        try (FileOutputStream fos = new FileOutputStream(AUDIO_FILE_PATH, true)) {
            fos.write(audioData);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket 연결됨: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WebSocket 연결 종료: " + session.getId());
    }
}
