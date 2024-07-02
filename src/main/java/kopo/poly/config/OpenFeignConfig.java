package kopo.poly.config;

import feign.Contract;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Response;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import feign.codec.Decoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Configuration // 스프링 설정 클래스임을 나타내는 어노테이션
public class OpenFeignConfig {

    @Value("${apikey}") // application.properties 파일에서 apikey 값을 주입 받음
    private String apiKey;

    @Value("${apiKeyPrimaryVal}") // application.properties 파일에서 apiKeyPrimaryVal 값을 주입 받음
    private String apiKeyPrimaryVal;

    @Value("${requestId}") // application.properties 파일에서 requestId 값을 주입 받음
    private String requestId;

    // API 접속을 위해 접속 방법은 기본 값으로 설정함(반드시 설정되어야 함)
    @Bean
    public Contract feignContract() {
        return new Contract.Default();
    }

    /**
     * API 호출에 사용되는 헤더 설정
     * OpenFeign 통해 호출되는 모든 API 헤더에 적용됨
     */
    @Bean
    public RequestInterceptor requestInterceptor() {

        return requestTemplate -> {
            requestTemplate.header("X-NCP-CLOVASTUDIO-API-KEY", apiKey);
            requestTemplate.header("X-NCP-APIGW-API-KEY", apiKeyPrimaryVal);
            requestTemplate.header("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId);
            requestTemplate.header("Content-Type", "application/json; charset=utf-8");
            requestTemplate.header("Accept", "text/event-stream");
        };
    }

    @Bean
    Logger.Level feignLoggerLevel() {

        /*
        OpenFeign 통해 전송 및 전달받는 모든 과정에 대해 로그 찍기 설정

        NONE: 로깅하지 않음(기본값)
        BASIC: 요청 메소드와 URI와 응답 상태와 실행시간 로깅함
        HEADERS: 요청과 응답 헤더와 함께 기본 정보들을 남김
        FULL: 요청과 응답에 대한 헤더와 바디, 메타 데이터를 남김
        */
        return Logger.Level.FULL;
    }

    @Bean
    public Decoder feignDecoder() {
        return new Decoder() {
            final Decoder delegate = new ResponseEntityDecoder(new SpringDecoder(() -> new HttpMessageConverters(new MappingJackson2HttpMessageConverter())));

            @Override
            public Object decode(Response response, Type type) throws IOException {
                if (type.getTypeName().equals("java.util.List<java.lang.String>")) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().asInputStream()));
                    List<String> lines = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                    return lines;
                }
                // Delegate to existing ResponseEntityDecoder for other types
                return delegate.decode(response, type);
            }
        };
    }
}
