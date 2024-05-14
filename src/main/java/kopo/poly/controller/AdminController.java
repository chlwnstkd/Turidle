package kopo.poly.controller;

import kopo.poly.dto.PostDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/admin")
@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {
    private final IUserInfoService userInfoService;

    @GetMapping(value = "/main")
    public String main(ModelMap model, @RequestParam(defaultValue = "1") int page)
        throws Exception{

        log.info(this.getClass().getName() + ".main Start!");

        List<Map<String, Object>> pList = userInfoService.getUserList();
        if (pList == null) pList = new ArrayList<>();

        log.info(pList.toString());

        List<UserInfoDTO> rList = new ArrayList<>();

        for (Map<String, Object> rMap : pList) {
            UserInfoDTO rDTO = UserInfoDTO.builder(
            ).userId(String.valueOf(rMap.get("userId"))
            ).email(EncryptUtil.decAES128CBC(String.valueOf(rMap.get("email")))
            ).nickname(String.valueOf(rMap.get("nickname"))
            ).regDt(String.valueOf(rMap.get("regDt"))
            ).build();

            rList.add(rDTO);
        }

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 10;

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

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);

        return "admin/main";
    }
    @GetMapping(value = "/chat")
    public String chat() {

        log.info(this.getClass().getName() + ".userInfo Start!");

        return "admin/chat";
    }
    @GetMapping(value = "/userInfo")
    public String userInfo() {

        log.info(this.getClass().getName() + ".userInfo Start!");

        return "admin/userInfo";
    }
}
