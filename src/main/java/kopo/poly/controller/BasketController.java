package kopo.poly.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.BasketDTO;
import kopo.poly.dto.DictDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IBasketService;
import kopo.poly.service.IDictService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/basket")
@Slf4j
@Controller
@RequiredArgsConstructor
public class BasketController {
    
    private final IBasketService basketService;
    private final IDictService dictService;

    /**
     * @param model
     * @param page
     * @param session
     * @param request
     * @return basket 페이지
     * @throws Exception
     */
    @GetMapping("")
    public String basket(ModelMap model, @RequestParam(defaultValue = "1") int page, HttpSession session, HttpServletRequest request)
            throws Exception {
        log.info(this.getClass().getName() + ".basket Start!");

        log.info("page : " + page);

        String word = CmmUtil.nvl(request.getParameter("text"));

        if ((word == null  || word.equals("null")) || word.equals("")) {
            word = "";
        }else {
            word = EncryptUtil.decodeString(word);
        }
        log.info(word);

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));

        int from = (page-1) * 5 + 1;

        int to = page * 5;

        BasketDTO pDTO = BasketDTO.builder().word(word).userId(userId).from(from).to(to).build();

        List<Map<String, Object>> pList = basketService.getBasketList(pDTO);
        if (pList == null) pList = new ArrayList<>();

        List<BasketDTO> rList = new ArrayList<>();

        for (Map<String, Object> rMap : pList) {
            BasketDTO rDTO = BasketDTO.builder(
            ).userId(String.valueOf(rMap.get("userId"))
            ).word(String.valueOf(rMap.get("word"))
            ).targetCode(String.valueOf(rMap.get("targetCode"))
            ).definition(String.valueOf(rMap.get("definition"))
            ).pos(String.valueOf(rMap.get("pos"))
            ).build();

            rList.add(rDTO);
        }

        log.info(rList.toString());

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 5;

        // 페이지네이션을 위해 전체 아이템 개수 구하기
        int totalItems = basketService.getBasketCount(pDTO);

        // 전체 페이지 개수 계산
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        model.addAttribute("rList", rList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("word", word);

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);

        log.info(this.getClass().getName() + ".basket End!");

        return "basket/basket";
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
            String word = CmmUtil.nvl(request.getParameter("word"));
            String definition = CmmUtil.nvl(request.getParameter("definition"));
            String pos = CmmUtil.nvl(request.getParameter("pos"));

            log.info("userId : " + userId);
            log.info("targetCode : " + targetCode);
            log.info("word : " + word);
            log.info("definition : " + definition);
            log.info("pos : " + pos);

            BasketDTO pDTO = BasketDTO.builder(
            ).userId(userId
            ).targetCode(targetCode
            ).word(word
            ).definition(definition
            ).pos(pos
            ).build();

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

    @GetMapping("/basketInfo")
    public String basketInfo(ModelMap model, HttpServletRequest request)
            throws Exception {
        log.info(this.getClass().getName() + ".basketInfo Start!");

        String targetCode = CmmUtil.nvl(request.getParameter("targetCode"));
        String word = CmmUtil.nvl(request.getParameter("word"));


        word = EncryptUtil.decodeString(word);

        log.info(word);

        log.info("targetCode : " + targetCode);

        DictDTO rDTO = dictService.getDictInfo(targetCode);

        rDTO = rDTO.toBuilder().word(word).build();

        log.info(rDTO.toString());

        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".basketInfo End!");

        return "basket/basketInfo";
    }
}
