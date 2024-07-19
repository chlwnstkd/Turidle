package kopo.poly.controller;

import kopo.poly.service.ISpeechService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping(value = "/trans")
@RequiredArgsConstructor
@Controller
public class TransController {

    // SpeechService 주입
    private ISpeechService speechService;

    @GetMapping(value = "")
    public String trans() {

        log.info(this.getClass().getName() + ".trans Start!");

        return "trans/trans";
    }


    @GetMapping("/recognize") // HTTP GET 엔드포인트
    public String recognizeSpeech() throws Exception{
        // 음성 인식을 수행하고 결과를 반환
        return speechService.recognizeSpeech();
    }

}
