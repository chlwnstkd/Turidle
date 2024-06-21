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

    private final IClovaApiService clovaApiClient;

    public String prompt(String text) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Create the system message JSON object
        ObjectNode systemMessage = mapper.createObjectNode();
        systemMessage.put("role", "system");
        systemMessage.put("content", "");

        // Create the user message JSON object
        ObjectNode userMessage = mapper.createObjectNode();
        userMessage.put("role", "user");
        userMessage.put("content", text);

        // Create the messages JSON array
        ArrayNode messages = mapper.createArrayNode();
        messages.add(systemMessage);
        messages.add(userMessage);

        // Create the main request data JSON object
        ObjectNode requestData = mapper.createObjectNode();
        requestData.set("messages", messages);
        requestData.put("topP", 0.8);
        requestData.put("topK", 0);
        requestData.put("maxTokens", 256);
        requestData.put("temperature", 0.5);
        requestData.put("repeatPenalty", 5.0);
        requestData.set("stopBefore", mapper.createArrayNode()); // Empty array
        requestData.put("includeAiFilters", true);
        requestData.put("seed", 0);

        // Call the Feign client method
        List<String> resultList = clovaApiClient.prompt(requestData);

        resultList.forEach(str -> log.info("resultList : " + str));

        // Assuming resultList contains the responses in order
        // Handle the responses as needed
        if (!resultList.isEmpty()) {
            // For example, extract the last response
            String lastResponse = resultList.get(resultList.size() - 6);
            Gson gson = new Gson();

            ResultData resultData = gson.fromJson(lastResponse.substring(lastResponse.indexOf("{")), ResultData.class);

            return resultData.getMessage().getContent();
        } else {
            throw new IOException("Empty response from API");
        }
    }
}
