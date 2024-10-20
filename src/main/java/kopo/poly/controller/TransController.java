package kopo.poly.controller;

import kopo.poly.service.ISpeechService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Slf4j
@RequestMapping(value = "/trans")
@RequiredArgsConstructor
@Controller
public class TransController {

    @GetMapping(value = "")
    public String trans() {

        log.info(this.getClass().getName() + ".trans Start!");

        return "trans/trans";
    }

}
