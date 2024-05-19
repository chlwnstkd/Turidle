package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.PostDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IChatService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping(value = "/admin")
@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {
    private final IUserInfoService userInfoService;
    private final IChatService chatService;

    @GetMapping(value = "/main")
    public String main(ModelMap model, @RequestParam(defaultValue = "1") int page)
            throws Exception {

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

    @GetMapping(value = "/chatList")
    public String chatList(ModelMap model, @RequestParam(defaultValue = "1") int page)
            throws Exception {

        log.info(this.getClass().getName() + ".chatList Start!");

        List<Map<String, Object>> pList = chatService.getChatList();
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

        return "admin/chatList";
    }

    @GetMapping(value = "/chatroom")
    public String chatroom(HttpServletRequest request, HttpSession session, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".chatroom Start!");

        String roomName = CmmUtil.nvl(request.getParameter("userId"));
        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        log.info("userId : " + userId);

        Map<String, Object> rMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO)).orElseGet(HashMap::new);

        UserInfoDTO rDTO = UserInfoDTO.builder(
        ).userId(String.valueOf(rMap.get("userId"))
        ).nickname(String.valueOf(rMap.get("nickname"))
        ).email(EncryptUtil.decAES128CBC(String.valueOf(rMap.get("email")))
        ).regDt(String.valueOf(rMap.get("regDt"))
        ).build();

        log.info("rDTO : " + rDTO);

        model.addAttribute("roomName", roomName);
        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".chatroom End!");
        return "admin/chatroom";
    }

    @GetMapping(value = "/userInfo")
    public String userInfo(HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".userInfo Start!");

        String userId = CmmUtil.nvl((String) request.getParameter("userId"));

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();
        Map<String, Object> rMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO))
                .orElseGet(HashMap::new);

        UserInfoDTO rDTO = UserInfoDTO.builder(
        ).userId(String.valueOf(rMap.get("userId"))
        ).nickname(String.valueOf(rMap.get("nickname"))
        ).email(EncryptUtil.decAES128CBC(String.valueOf(rMap.get("email")))
        ).regDt(String.valueOf(rMap.get("regDt"))
        ).build();

        log.info(rDTO.userId() + "/" + rDTO.nickname() + "/" + rDTO.email() + "/" + rDTO.regDt());

        model.addAttribute("rDTO", rDTO);

        return "admin/userInfo";
    }
    @ResponseBody
    @PostMapping(value = "searchUser")
    public MsgDTO searchUser(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".searchUser Start!");

        String userId = CmmUtil.nvl((String) request.getParameter("userId"));
        int res = 0;

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();
        Map<String, Object> rMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO))
                .orElseGet(HashMap::new);

        if(String.valueOf(rMap.get("userId")).equals("null")) {
            res = 1;
        }

        MsgDTO dto = MsgDTO.builder().result(res).build();

        return dto;

    }

    @ResponseBody
    @PostMapping(value = "deleteUser")
    public MsgDTO deleteUser(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".deleteUser Start!");

        String userId = CmmUtil.nvl(request.getParameter("userId"));

        log.info("userId : " + userId);

        int res = 0;

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        int success = Optional.ofNullable(userInfoService.deleteUser(pDTO))
                .orElse(0);

        String msg ="";

        if(success != 0) {

            msg = "탈퇴시켰습니다";

            res = 1;

        }

        log.info(this.getClass().getName() + ".deleteUser End!");

        MsgDTO dto = MsgDTO.builder().msg(msg).result(res).build();

        return dto;

    }
}
