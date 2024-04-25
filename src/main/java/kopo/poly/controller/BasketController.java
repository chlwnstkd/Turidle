package kopo.poly.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/basket")
@Slf4j
@Controller
@RequiredArgsConstructor
public class BasketController {
    @GetMapping(value = "")
    public String basket() {

        log.info(this.getClass().getName() + ".basket Start!");

        return "/basket";
    }
}
