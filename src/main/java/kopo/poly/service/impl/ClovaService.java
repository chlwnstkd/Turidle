package kopo.poly.service.impl;

import com.google.gson.Gson;
import kopo.poly.service.IClovaService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ClovaService implements IClovaService {
    @Value("${host}")
    private String host;
    @Value("${apikey}")
    private String apiKey;

    @Value("${apiKeyPrimaryVal}")
    private String apiKeyPrimaryVal;

    @Value("${requestId}")
    private String requestId;

    public List<String> execute(JSONObject completionRequest) throws IOException {
        String url = host + "/testapp/v1/chat-completions/HCX-DASH-001";
        List<String> list = new ArrayList<>();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request headers
        con.setRequestMethod("POST");
        con.setRequestProperty("X-NCP-CLOVASTUDIO-API-KEY", apiKey);
        con.setRequestProperty("X-NCP-APIGW-API-KEY", apiKeyPrimaryVal);
        con.setRequestProperty("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId);
        con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        con.setRequestProperty("Accept", "text/event-stream");

        // Send post request
        con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(completionRequest.toString().getBytes());
            wr.flush();
        }

        // Check response code
        int responseCode = con.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + responseCode);
        }

        // Get the response
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                if (inputLine.isEmpty()) {
                    // Process the event when an empty line is encountered
                    list.add(response.toString());
                    // Reset the StringBuilder for the next event
                    response.setLength(0);
                }
            }
        }
        return list;
    }

    public String prompt(String text) throws IOException {
        JSONArray presetText = new JSONArray();
        presetText.put(new JSONObject().put("role", "system").put("content", ""));
        presetText.put(new JSONObject().put("role", "user").put("content", text));

        JSONObject requestData = new JSONObject();
        requestData.put("messages", presetText);
        requestData.put("topP", 0.8);
        requestData.put("topK", 0);
        requestData.put("maxTokens", 256);
        requestData.put("temperature", 0.5);
        requestData.put("repeatPenalty", 5.0);
        requestData.put("stopBefore", new JSONArray());
        requestData.put("includeAiFilters", true);
        requestData.put("seed", 0);

        List<String> list = execute(requestData);

        String resultString = list.get(list.size() - 2);

        Gson gson = new Gson();
        ResultData resultData = gson.fromJson(resultString.substring(resultString.indexOf("{")), ResultData.class);

        return resultData.getMessage().getContent();
    }

    static class ResultData {
        private MessageData message;

        public MessageData getMessage() {
            return message;
        }

        public void setMessage(MessageData message) {
            this.message = message;
        }
    }

    static class MessageData {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
