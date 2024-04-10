package kopo.poly.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "")
@Controller
public class MainController {
    @GetMapping(value = "/home")
    public String home() {

        log.info(this.getClass().getName() + ".home Start!");

        return "home";
    }
    @GetMapping(value = "/index")
    public String index() {

        log.info(this.getClass().getName() + ".index Start!");

        return "index";
    }
}
