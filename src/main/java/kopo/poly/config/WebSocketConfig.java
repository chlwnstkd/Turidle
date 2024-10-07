package kopo.poly.config;

import kopo.poly.handler.ChatHandler;
import kopo.poly.handler.SpeechWebSocketHandler;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Slf4j // 로그 사용을 위한 어노테이션
@EnableWebSocket // 웹소켓을 활성화하는 어노테이션
@RequiredArgsConstructor // final 필드나 @NonNull 필드에 대한 생성자를 자동으로 생성해주는 롬복 어노테이션
@Configuration // 스프링 설정 클래스임을 나타내는 어노테이션
public class WebSocketConfig  implements WebSocketConfigurer {

    private final ChatHandler chatHandler; // ChatHandler를 주입받음
    private final SpeechWebSocketHandler speechWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("WebSocket Execute!!!");

        registry.addHandler(chatHandler, "/ws/*/*")
                .setAllowedOrigins("*") // 모든 도메인에서의 접근을 허용
                .addInterceptors(
                        new HttpSessionHandshakeInterceptor() {

                            @Override
                            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                           WebSocketHandler wsHandler, Map<String, Object> attributes)
                                    throws Exception {

                                String path = CmmUtil.nvl(request.getURI().getPath());
                                log.info("path : " + path);

                                String[] urlInfo = path.split("/");

                                String roomName = CmmUtil.nvl(urlInfo[2]); // URI Path를 통해 채팅방 이름 가져오기
                                String userName = CmmUtil.nvl(urlInfo[3]); // URI Path를 통해 사용자 이름 가져오기

                                // 채팅룸 이름이 한글 및 특수문자로 입력될 수 있기에
                                // 채팅룸 이름은 데이터 처리에 문제없는 영문이나 숫자로 변환해야 함
                                // 채팅룸 이름은 해시 함수를 이용하여 영문명으로 변경함
                                String roomNameHash = EncryptUtil.encHashSHA256(roomName);

                                log.info("roomName : " + roomName + " / userName : " + userName);

                                attributes.put("roomName", roomName);
                                attributes.put("userName", userName);
                                attributes.put("roomNameHash", roomNameHash);

                                return super.beforeHandshake(request, response, wsHandler, attributes);
                            }
                        }
                );
        registry.addHandler(speechWebSocketHandler, "/audio").setAllowedOrigins("*");

    }
}