package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.DictDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.PostDTO;
import kopo.poly.service.IDictService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/dict")
@Controller
public class DictController {

    private final IDictService dictService;

    @GetMapping(value = "/dictSearch")
    public String search() {

        log.info(this.getClass().getName() + ".search Start!");

        return "dict/dictSearch";
    }

    @GetMapping("/dictList")
    public String dictList(ModelMap model, @RequestParam(defaultValue = "1") int page, HttpServletRequest request)
            throws Exception {
        log.info(this.getClass().getName() + ".dictList Start!");

        String text = CmmUtil.nvl(request.getParameter("text"));

        List<DictDTO> rList = dictService.getDictList(text);
        if (rList == null) rList = new ArrayList<>();

        log.info(rList.toString());

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 5;

        // 페이지네이션을 위해 전체 아이템 개수 구하기
        int totalItems = rList.size();

        // 전체 페이지 개수 계산
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        // 현재 페이지에 해당하는 아이템들만 선택하여 rList에 할당
        int fromIndex = (page - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);

        log.info(fromIndex + "");
        log.info(toIndex + "");
        log.info(itemsPerPage + "");

        rList = rList.subList(fromIndex, toIndex);

        model.addAttribute("rList", rList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("text", text);

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);

        log.info(this.getClass().getName() + ".dictList End!");

        return "dict/dictList";
    }
    @GetMapping("/dictInfo")
    public String dictInfo(ModelMap model, HttpServletRequest request)
            throws Exception {
        log.info(this.getClass().getName() + ".dictInfo Start!");

        String targetCode = CmmUtil.nvl(request.getParameter("targetCode"));
        String word = CmmUtil.nvl(request.getParameter("word"));

        log.info("targetCode : " + targetCode);

        DictDTO rDTO = dictService.getDictInfo(targetCode);

        rDTO = rDTO.toBuilder().word(word).build();

        log.info(rDTO.toString());

        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".dictList End!");

        return "dict/dictInfo";
    }
}
