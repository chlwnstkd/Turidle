package kopo.poly.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping(value = "trans")
@RequiredArgsConstructor
@Controller
public class TransController {

    @GetMapping(value = "")
    public String trans() throws Exception {

        log.info(this.getClass().getName() + "./trans Start!");



        log.info(this.getClass().getName() + "./trans End!");

        return "/trans/trans";
    }
}
