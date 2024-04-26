package kopo.poly.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.BasketDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.PostDTO;
import kopo.poly.service.IBasketService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping(value = "/basket")
@Slf4j
@Controller
@RequiredArgsConstructor
public class BasketController {
    
    private final IBasketService basketService;
    
    @GetMapping(value = "")
    public String basket() {

        log.info(this.getClass().getName() + ".basket Start!");

        return "/basket";
    }
    @ResponseBody
    @PostMapping(value = "/update")
    public MsgDTO update(HttpSession session, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".update Start!");

        String msg = "";
        int res = 0;

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String targetCode = CmmUtil.nvl(request.getParameter("targetCode"));

            log.info("userId : " + userId);
            log.info("targetCode : " + targetCode);

            BasketDTO pDTO = BasketDTO.builder().userId(userId).targetCode(targetCode).build();

            if(basketService.getBasket(pDTO) == 1) {
                basketService.deleteBasket(pDTO);
                msg = "보관함에서 취소히였습니다";
                res = 1;
            } else {
                basketService.insertBasket(pDTO);
                msg = " 보관함에 추가되었습니다";
                res = 1;
            }

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".update End!");
        }

        return MsgDTO.builder().msg(msg).result(res).build();
    }
}
