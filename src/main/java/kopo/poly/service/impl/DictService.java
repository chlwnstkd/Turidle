package kopo.poly.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.DictDTO;
import kopo.poly.service.IDictService;
import kopo.poly.util.NetworkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
}
