package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.dto.DictDTO;
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

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/dict")
@Controller
public class DictController {

    private final IDictService dictService;

    /**
     * 사전 검색 페이지로 이동
     * @return 사전 검색 페이지 경로
     */
    @GetMapping(value = "/dictSearch")
    public String search() {
        log.info(this.getClass().getName() + ".search Start!");
        return "dict/dictSearch";
    }

    /**
     * 사전 목록 조회
     * @param model 모델 객체
     * @param page 현재 페이지 번호 (기본값: 1)
     * @param request HTTP 요청 객체
     * @return 사전 목록 페이지 경로
     * @throws Exception 예외 처리
     */
    @GetMapping("/dictList")
    public String dictList(ModelMap model, @RequestParam(defaultValue = "1") int page, HttpServletRequest request)
            throws Exception {
        log.info(this.getClass().getName() + ".dictList Start!");

        // 검색어를 요청 파라미터에서 가져와 복호화
        String text = CmmUtil.nvl(request.getParameter("text"));
        text = EncryptUtil.decodeString(text);

        log.info(text);

        try {
            // 사전 목록 조회
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

            // 모델에 필요한 데이터 추가
            model.addAttribute("rList", rList);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("text", text);
        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();
        }

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);
        log.info(this.getClass().getName() + ".dictList End!");

        return "dict/dictList";
    }

    /**
     * 사전 상세 정보 조회
     * @param model 모델 객체
     * @param request HTTP 요청 객체
     * @return 사전 상세 정보 페이지 경로
     * @throws Exception 예외 처리
     */
    @GetMapping("/dictInfo")
    public String dictInfo(ModelMap model, HttpServletRequest request)
            throws Exception {
        log.info(this.getClass().getName() + ".dictInfo Start!");

        // 요청 파라미터에서 targetCode와 단어를 가져와 복호화
        String targetCode = CmmUtil.nvl(request.getParameter("targetCode"));
        String word = CmmUtil.nvl(request.getParameter("word"));
        word = EncryptUtil.decodeString(word);

        log.info(word);
        log.info("targetCode : " + targetCode);

        // 사전 상세 정보 조회
        DictDTO rDTO = dictService.getDictInfo(targetCode);
        rDTO = rDTO.toBuilder().word(word).build();

        log.info(rDTO.toString());

        // 모델에 사전 상세 정보 추가
        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".dictList End!");

        return "dict/dictInfo";
    }
}
