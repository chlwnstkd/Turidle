package kopo.poly;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import org.json.JSONArray;
import org.json.JSONObject;

public class CompletionExecutor {
    private String host;
    private String apiKey;
    private String apiKeyPrimaryVal;
    private String requestId;

    public CompletionExecutor(String host, String apiKey, String apiKeyPrimaryVal, String requestId) {
        this.host = host;
        this.apiKey = apiKey;
        this.apiKeyPrimaryVal = apiKeyPrimaryVal;
        this.requestId = requestId;
    }

    public void execute(JSONObject completionRequest) throws Exception {
        String url = host + "/testapp/v1/chat-completions/HCX-DASH-001";
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
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(completionRequest.toString());
        wr.flush();
        wr.close();

        // Get the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            if (inputLine.isEmpty()) {
                // Process the event when empty line is encountered
                processEvent(response.toString());
                // Reset the StringBuilder for the next event
                response.setLength(0);
            }
        }
        in.close();
    }

    private void processEvent(String eventData) {
        // Decode and process the eventData as needed
        System.out.println("Received event data: " + eventData);
    }

    public static void main(String[] args) throws Exception {
        CompletionExecutor completionExecutor = new CompletionExecutor(
                "https://clovastudio.stream.ntruss.com",
                "NTA0MjU2MWZlZTcxNDJiY/D3ANE0yyIa4y+8GjxZesNfys+xjSizQmZ8+ZwUkBMx",
                "IOEUMfN5uT3pyp0ABWij2LwkqsIvL1NP77sMLe8H",
                "dfbbc624-30be-4896-848d-3b515b7d2874"
        );

        JSONArray presetText = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "안녕");
        presetText.put(systemMessage);
        presetText.put(new JSONObject().put("role", "user").put("content", ""));

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

        System.out.println("start");
        System.out.println(presetText.toString());
        System.out.println("end");
        completionExecutor.execute(requestData);
    }
}
