package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.dto.ChatCompletionDTO;
import kopo.poly.dto.ChatRequestMsgDTO;
import kopo.poly.service.impl.ChatGPTService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ChatGPT API
 */
@Slf4j
@RestController
@RequestMapping(value = "/chatGpt")
@RequiredArgsConstructor
public class ChatGPTController {

    private final ChatGPTService chatGPTService;

    @PostMapping("/prompt")
    public String prompt(HttpServletRequest request) {

        log.info(this.getClass().getName() + ".prompt Start!");

        String content = CmmUtil.nvl(request.getParameter("content"));


        ChatRequestMsgDTO pDTO = ChatRequestMsgDTO.builder().role("system").content(content).build();


        List<ChatRequestMsgDTO> rList = new ArrayList<>();
        rList.add(pDTO);

        ChatCompletionDTO rDTO = ChatCompletionDTO.builder().model("gpt-4-turbo").messages(rList).build();

        Map<String, Object> json = chatGPTService.prompt(rDTO);

        log.info(json.toString());

        JSONObject jsonObject = new JSONObject(json);
        JSONObject message = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message");
        String result = message.getString("content");


        log.info(this.getClass().getName() + ".prompt Start!");

        return result;
    }
}
