package kopo.poly.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import kopo.poly.service.IClovaApiService;
import kopo.poly.service.IClovaService;
import kopo.poly.util.ResultData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClovaService implements IClovaService {

    // Clova API 클라이언트 의존성 주입
    private final IClovaApiService clovaApiClient;

    public String prompt(String text) throws IOException {
        ObjectMapper mapper = new ObjectMapper(); // JSON 객체 생성 도구

        // 시스템 메시지 JSON 객체 생성
        ObjectNode systemMessage = mapper.createObjectNode();
        systemMessage.put("role", "system");
        systemMessage.put("content", "");

        // 사용자 메시지 JSON 객체 생성
        ObjectNode userMessage = mapper.createObjectNode();
        userMessage.put("role", "user");
        userMessage.put("content", text);

        // 메시지 JSON 배열 생성
        ArrayNode messages = mapper.createArrayNode();
        messages.add(systemMessage);
        messages.add(userMessage);

        // 메인 요청 데이터 JSON 객체 생성
        ObjectNode requestData = mapper.createObjectNode();
        requestData.set("messages", messages);
        requestData.put("topP", 0.8);
        requestData.put("topK", 0);
        requestData.put("maxTokens", 256);
        requestData.put("temperature", 0.5);
        requestData.put("repeatPenalty", 5.0);
        requestData.set("stopBefore", mapper.createArrayNode()); // 빈 배열
        requestData.put("includeAiFilters", true);
        requestData.put("seed", 0);

        // Feign 클라이언트 메서드 호출
        List<String> resultList = clovaApiClient.prompt(requestData);

        // 결과 로그 출력
        resultList.forEach(str -> log.info("resultList : " + str));

        // resultList가 응답을 순서대로 포함한다고 가정
        // 필요한 대로 응답 처리
        if (!resultList.isEmpty()) {
            // 예를 들어, 마지막 응답 추출
            String lastResponse = resultList.get(resultList.size() - 6); // -1이 아닌 -6으로 변경된 이유는 확인 필요
            Gson gson = new Gson();

            // JSON 문자열을 ResultData 객체로 변환
            ResultData resultData = gson.fromJson(lastResponse.substring(lastResponse.indexOf("{")), ResultData.class);

            // 응답 메시지 내용 반환
            return resultData.getMessage().getContent();
        } else {
            throw new IOException("API로부터 빈 응답을 받았습니다.");
        }
    }
}
