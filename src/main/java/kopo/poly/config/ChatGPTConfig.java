package kopo.poly.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration // 스프링 설정 클래스임을 나타내는 어노테이션
public class ChatGPTConfig {

    @Value("${openai.secret-key}") // application.properties 파일에서 openai.secret-key 값을 주입 받음
    private String secretKey;

    @Bean // RestTemplate 빈을 생성하는 메소드
    public RestTemplate restTemplate() {
        return new RestTemplate(); // RestTemplate 객체를 반환
    }

    @Bean // HttpHeaders 빈을 생성하는 메소드
    public HttpHeaders httpHeaders() {
        HttpHeaders headers = new HttpHeaders(); // HttpHeaders 객체 생성
        headers.setBearerAuth(secretKey); // Bearer 인증 방식으로 secretKey 설정
        headers.setContentType(MediaType.APPLICATION_JSON); // Content-Type을 JSON으로 설정
        return headers; // 설정된 HttpHeaders 객체를 반환
    }
}
