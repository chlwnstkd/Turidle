package kopo.poly.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.ChatDTO;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatHandler extends TextWebSocketHandler {


    // 웹소켓에 접속되는 사용자들을 저장하며, 중복을 제거하기 위해 Set 데이터 구조 사용함
    private static Set<WebSocketSession> clients = Collections.synchronizedSet(new LinkedHashSet<>());

    // 채팅룸 조회하기 위해 사용
    public static Map<String, String> roomInfo = Collections.synchronizedMap(new LinkedHashMap<>());

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info(this.getClass().getName() + ".handleTextMessage Start!");

        String roomName = CmmUtil.nvl((String) session.getAttributes().get("roomName"));
        String userName = CmmUtil.nvl((String) session.getAttributes().get("userName"));
        String roomNameHash = CmmUtil.nvl((String) session.getAttributes().get("roomNameHash"));

        log.info("roomName : " + roomName);
        log.info("userName : " + userName);
        log.info("roomNameHash : " + roomNameHash);

        // 채팅 메시지
        String msg = CmmUtil.nvl(message.getPayload());
        log.info("msg : " + msg);

        // 발송시간 추가를 위해 JSON 문자열을 DTO로 변환
        ChatDTO cDTO = new ObjectMapper().readValue(msg, ChatDTO.class);

        // 메시지 발송시간 서버 시간으로 설정하여 추가하기
        cDTO = cDTO.toBuilder().date(DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss")).build();

        // ChatDTO을 JSON으로 다시 변환하기
        String json = new ObjectMapper().writeValueAsString(cDTO);

        log.info("json : " + json);

        // 웹소켓에 접속된 모든 사용자 검색
        clients.forEach(s -> {

            // 내가 접속한 채팅방에 있는 세션만 메시지 보내기
            if (roomNameHash.equals(s.getAttributes().get("roomNameHash"))) {
                try {

                    TextMessage chatMsg = new TextMessage(json);
                    s.sendMessage(chatMsg);

                } catch (IOException e) {
                    log.info("Error : " + e);
                }

            }
        });

        log.info(this.getClass().getName() + ".handleTextMessage End!");

    }
}