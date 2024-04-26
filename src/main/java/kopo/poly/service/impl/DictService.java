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
    @Value("${spring.api.key}")
    private String key;

    @Override
    public List<DictDTO> getDictList(String text) throws Exception {

        String url = "https://opendict.korean.go.kr/api/search?certkey_no=6503&" +
                "key=" + key + "&target_type=search&req_type=json&part=word&q=" + text + "&sort=dict&start=1&num=100";

        Map<String, String> headers = new HashMap<>();

        List<DictDTO> rList = new ArrayList<>();

        String json = NetworkUtil.get(url, headers);

        Map<String, Object> pMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

        Map<String, Object> channelMap = (Map<String, Object>) pMap.get("channel");

        List<Map<String, Object>> itemList = (List<Map<String, Object>>) channelMap.get("item");

        for (Map<String, Object> item : itemList) {
            List<Map<String, Object>> senseList = (List<Map<String, Object>>) item.get("sense");
            String word = (String) item.get("word");

            for (Map<String, Object> sense : senseList) {
                String type = (String) sense.get("type");

                if(!type.equals("방언")) {
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

                rList.add(rDTO);

            }
        }

        log.info(rList.toString());

        return rList;
    }
    @Override
    public DictDTO getDictInfo(String targetCode) throws Exception {

        String url = "https://opendict.korean.go.kr/api/view?certkey_no=6503&key=" + key +
                "&target_type=view&req_type=json&method=target_code&q=" + targetCode;

        Map<String, String> headers = new HashMap<>();

        DictDTO rDTO = DictDTO.builder().build();

        String json = NetworkUtil.get(url, headers);

        Map<String, Object> pMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

        try {
            // JSON 문자열을 자바 객체로 변환합니다.
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);

            String senseDefinition = "";
            String regionInfo = "";
            String relationWord = "";
            String exampleTranslation = "";
            String exampleExample = "";
            String  pos = "";

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

            if(!relationWord.equals("")) {
                int length = relationWord.length();
                relationWord = relationWord.substring(0, length - 3);

            }
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

        log.info(rDTO.toString());

        return rDTO;
    }
}
