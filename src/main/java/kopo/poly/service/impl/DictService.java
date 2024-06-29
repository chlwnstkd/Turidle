package kopo.poly.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.DictDTO;
import kopo.poly.service.IDictService;
import kopo.poly.util.NetworkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class DictService implements IDictService {
    // application.properties에서 API 키 값 주입
    @Value("${spring.api.key}")
    private String key;

    @Override
    public List<DictDTO> getDictList(String text) throws Exception {
        // API 호출을 위한 URL 구성
        String url = "https://opendict.korean.go.kr/api/search?certkey_no=6503&" +
                "key=" + key + "&target_type=search&req_type=json&part=word&q=" + text + "&sort=dict&start=1&num=100";

        Map<String, String> headers = new HashMap<>(); // 헤더 초기화
        List<DictDTO> rList = new ArrayList<>(); // 결과 리스트 초기화

        // 최대 재시도 횟수 설정
        int maxRetries = 3;
        int retryCount = 0;
        boolean success = false;
        String json = null;

        // API 호출 재시도 로직
        while (retryCount < maxRetries && !success) {
            try {
                json = NetworkUtil.get(url, headers); // API 호출
                if(json != null) {
                    success = true; // 성공 시 루프 종료
                }
            } catch (Exception e) {
                retryCount++;
                log.error("API 통신 실패: 재시도 횟수 = " + retryCount, e);
                if (retryCount >= maxRetries) {
                    throw new Exception("API 통신 실패: 최대 재시도 횟수를 초과했습니다.", e); // 최대 재시도 횟수 초과 시 예외 발생
                }
                Thread.sleep(2000); // 2초 지연 후 재시도
            }
        }

        // JSON 응답 파싱
        Map<String, Object> pMap = new ObjectMapper().readValue(json, LinkedHashMap.class);
        Map<String, Object> channelMap = (Map<String, Object>) pMap.get("channel");
        List<Map<String, Object>> itemList = (List<Map<String, Object>>) channelMap.get("item");

        // 각 아이템을 순회하며 DictDTO 객체 생성
        for (Map<String, Object> item : itemList) {
            List<Map<String, Object>> senseList = (List<Map<String, Object>>) item.get("sense");
            String word = (String) item.get("word");

            for (Map<String, Object> sense : senseList) {
                String type = (String) sense.get("type");

                if(!type.equals("방언")) { // 방언인 경우만 처리
                    continue;
                }
                String definition = (String) sense.get("definition");
                String pos = (String) sense.get("pos");
                String targetCode = (String) sense.get("target_code");

                DictDTO rDTO = DictDTO.builder(
                ).definition(definition
                ).pos(pos
                ).targetCode(targetCode
                ).word(word
                ).build();

                rList.add(rDTO); // 결과 리스트에 추가
            }
        }

        log.info(rList.toString()); // 결과 로그 출력
        return rList; // 결과 반환
    }

    @Override
    public DictDTO getDictInfo(String targetCode) throws Exception {
        // API 호출을 위한 URL 구성
        String url = "https://opendict.korean.go.kr/api/view?certkey_no=6503&key=" + key +
                "&target_type=view&req_type=json&method=target_code&q=" + targetCode;

        Map<String, String> headers = new HashMap<>(); // 헤더 초기화
        DictDTO rDTO = DictDTO.builder().build(); // 결과 객체 초기화
        String json = NetworkUtil.get(url, headers); // API 호출

        Map<String, Object> pMap = new ObjectMapper().readValue(json, LinkedHashMap.class); // JSON 응답 파싱

        try {
            // JSON 문자열을 자바 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);

            // JSON 파싱을 위한 변수 초기화
            String senseDefinition = "";
            String regionInfo = "";
            String relationWord = "";
            String exampleTranslation = "";
            String exampleExample = "";
            String pos = "";

            JsonNode channelNode = rootNode.path("channel");
            if (!channelNode.isMissingNode()) {
                JsonNode itemNode = channelNode.path("item");
                if (!itemNode.isMissingNode()) {
                    JsonNode senseInfoNode = itemNode.path("senseInfo");
                    if (!senseInfoNode.isMissingNode()) {
                        senseDefinition = senseInfoNode.path("definition").asText("");
                        regionInfo = senseInfoNode.path("region_info").path(0).asText("");
                        relationWord = senseInfoNode.path("relation_info").path(0).path("word").asText("");
                        exampleTranslation = senseInfoNode.path("example_info").path(0).path("translation").asText("");
                        exampleExample = senseInfoNode.path("example_info").path(0).path("example").asText("");
                        pos = senseInfoNode.path("pos").asText("");
                    }
                }
            }

            // 관련 단어 후처리
            if(!relationWord.equals("")) {
                int length = relationWord.length();
                relationWord = relationWord.substring(0, length - 3);
            }

            // 결과 객체 빌드
            rDTO = rDTO.toBuilder().definition(senseDefinition
            ).region(regionInfo
            ).relationWord(relationWord
            ).example(exampleExample
            ).original(exampleTranslation
            ).pos(pos
            ).targetCode(targetCode
            ).build();

        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info(rDTO.toString()); // 결과 로그 출력
        return rDTO; // 결과 반환
    }
}
