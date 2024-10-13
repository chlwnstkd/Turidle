package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.PostDTO;
import kopo.poly.dto.ReportDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IChatService;
import kopo.poly.service.IReportService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping(value = "/admin")
@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {
    // IUserInfoService와 IChatService 인터페이스를 주입 받음
    private final IUserInfoService userInfoService;
    private final IChatService chatService;
    private final IReportService reportService;

    // "/admin/main" URL에 대한 GET 요청을 처리하는 메서드
    @GetMapping(value = "/main")
    public String main(ModelMap model, @RequestParam(defaultValue = "1") int page)
            throws Exception {

        log.info(this.getClass().getName() + ".main Start!");

        // 페이지 계산을 위한 변수 설정
        int from = (page - 1) * 10 + 1;
        int to = page * 10;

        // UserInfoDTO 객체 생성
        UserInfoDTO pDTO = UserInfoDTO.builder().from(from).to(to).build();

        // 사용자 목록을 가져옴
        List<Map<String, Object>> pList = userInfoService.getUserList(pDTO);
        if (pList == null) pList = new ArrayList<>();

        log.info(pList.toString());

        // 사용자 정보를 담을 리스트 생성
        List<UserInfoDTO> rList = new ArrayList<>();

        // 사용자 목록을 순회하며 DTO로 변환
        for (Map<String, Object> rMap : pList) {
            if (!String.valueOf(rMap.get("userId")).equals("admin")) {
                UserInfoDTO rDTO = UserInfoDTO.builder()
                        .userId(String.valueOf(rMap.get("userId")))
                        .email(EncryptUtil.decAES128CBC(String.valueOf(rMap.get("email"))))
                        .nickname(String.valueOf(rMap.get("nickname")))
                        .regDt(String.valueOf(rMap.get("regDt")))
                        .build();

                rList.add(rDTO);
            }
        }

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 10;

        // 전체 페이지 개수 계산
        int totalPages = (int) Math.ceil((double) Optional.ofNullable(userInfoService.getUserCount()).orElse(0) / itemsPerPage);

        // 모델에 필요한 데이터를 추가
        model.addAttribute("rList", rList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);

        // 뷰 이름 반환
        return "admin/main";
    }

    // "/admin/chatList" URL에 대한 GET 요청을 처리하는 메서드
    @GetMapping(value = "/chatList")
    public String chatList(ModelMap model, @RequestParam(defaultValue = "1") int page)
            throws Exception {

        log.info(this.getClass().getName() + ".chatList Start!");

        // 페이지 계산을 위한 변수 설정
        int from = (page - 1) * 10 + 1;
        int to = page * 10;

        // UserInfoDTO 객체 생성
        UserInfoDTO pDTO = UserInfoDTO.builder().from(from).to(to).build();

        // 채팅 목록을 가져옴
        List<Map<String, Object>> pList = chatService.getChatList(pDTO);
        if (pList == null) pList = new ArrayList<>();

        log.info(pList.toString());

        // 채팅 정보를 담을 리스트 생성
        List<UserInfoDTO> rList = new ArrayList<>();

        // 채팅 목록을 순회하며 DTO로 변환
        for (Map<String, Object> rMap : pList) {
            UserInfoDTO rDTO = UserInfoDTO.builder()
                    .userId(String.valueOf(rMap.get("userId")))
                    .email(EncryptUtil.decAES128CBC(String.valueOf(rMap.get("email"))))
                    .nickname(String.valueOf(rMap.get("nickname")))
                    .regDt(String.valueOf(rMap.get("regDt")))
                    .build();

            rList.add(rDTO);
        }

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 10;

        // 전체 페이지 개수 계산
        int totalPages = (int) Math.ceil((double) Optional.ofNullable(chatService.getChatCount()).orElse(0) / itemsPerPage);

        // 모델에 필요한 데이터를 추가
        model.addAttribute("rList", rList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);

        // 뷰 이름 반환
        return "admin/chatList";
    }



    // "/admin/chatroom" URL에 대한 GET 요청을 처리하는 메서드
    @GetMapping(value = "/chatroom")
    public String chatroom(HttpServletRequest request, HttpSession session, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".chatroom Start!");

        // 채팅방 이름과 사용자 ID를 가져옴
        String roomName = CmmUtil.nvl(request.getParameter("userId"));
        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        log.info("userId : " + userId);

        // 사용자 정보를 가져옴
        Map<String, Object> rMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO)).orElseGet(HashMap::new);

        UserInfoDTO rDTO = UserInfoDTO.builder()
                .userId(String.valueOf(rMap.get("userId")))
                .nickname(String.valueOf(rMap.get("nickname")))
                .email(EncryptUtil.decAES128CBC(String.valueOf(rMap.get("email"))))
                .regDt(String.valueOf(rMap.get("regDt")))
                .build();

        log.info("rDTO : " + rDTO);

        // 모델에 데이터를 추가
        model.addAttribute("roomName", roomName);
        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".chatroom End!");
        return "admin/chatroom";
    }

    // "/admin/userInfo" URL에 대한 GET 요청을 처리하는 메서드
    @GetMapping(value = "/userInfo")
    public String userInfo(HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".userInfo Start!");

        // 사용자 ID를 가져옴
        String userId = CmmUtil.nvl((String) request.getParameter("userId"));

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();
        Map<String, Object> rMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO))
                .orElseGet(HashMap::new);

        UserInfoDTO rDTO = UserInfoDTO.builder()
                .userId(String.valueOf(rMap.get("userId")))
                .nickname(String.valueOf(rMap.get("nickname")))
                .email(EncryptUtil.decAES128CBC(String.valueOf(rMap.get("email"))))
                .regDt(String.valueOf(rMap.get("regDt")))
                .build();

        log.info(rDTO.userId() + "/" + rDTO.nickname() + "/" + rDTO.email() + "/" + rDTO.regDt());

        // 모델에 데이터를 추가
        model.addAttribute("rDTO", rDTO);

        return "admin/userInfo";
    }

    // "/admin/userChange" URL에 대한 GET 요청을 처리하는 메서드
    @GetMapping(value = "/userChange")
    public String userChange(HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".userChange Start!");

        // 사용자 ID를 가져옴
        String userId = CmmUtil.nvl((String) request.getParameter("userId"));

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();
        Map<String, Object> rMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO))
                .orElseGet(HashMap::new);

        UserInfoDTO rDTO = UserInfoDTO.builder()
                .userId(String.valueOf(rMap.get("userId")))
                .nickname(String.valueOf(rMap.get("nickname")))
                .email(EncryptUtil.decAES128CBC(String.valueOf(rMap.get("email"))))
                .regDt(String.valueOf(rMap.get("regDt")))
                .build();

        log.info(rDTO.userId() + "/" + rDTO.nickname() + "/" + rDTO.email() + "/" + rDTO.regDt());

        // 모델에 데이터를 추가
        model.addAttribute("rDTO", rDTO);

        return "admin/userChange";
    }

    // "/admin/searchUser" URL에 대한 POST 요청을 처리하는 메서드
    @ResponseBody
    @PostMapping(value = "searchUser")
    public MsgDTO searchUser(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".searchUser Start!");

        // 사용자 ID를 가져옴
        String userId = CmmUtil.nvl((String) request.getParameter("userId"));
        int res = 0;

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();
        Map<String, Object> rMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO))
                .orElseGet(HashMap::new);

        // 사용자가 존재하지 않는 경우
        if(String.valueOf(rMap.get("userId")).equals("null")) {
            res = 1;
        }

        MsgDTO dto = MsgDTO.builder().result(res).build();

        return dto;
    }

    // "/admin/deleteUser" URL에 대한 POST 요청을 처리하는 메서드
    @ResponseBody
    @PostMapping(value = "deleteUser")
    public MsgDTO deleteUser(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".deleteUser Start!");

        // 사용자 ID를 가져옴
        String userId = CmmUtil.nvl(request.getParameter("userId"));

        log.info("userId : " + userId);

        int res = 0;

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        int success = Optional.ofNullable(userInfoService.deleteUser(pDTO))
                .orElse(0);

        String msg ="";

        // 삭제 성공 시
        if(success != 0) {
            msg = "탈퇴시켰습니다";
            res = 1;
        }

        MsgDTO dto = MsgDTO.builder().msg(msg).result(res).build();
        log.info(dto.toString());

        log.info(this.getClass().getName() + ".deleteUser End!");

        return dto;
    }

    // "/admin/changeUser" URL에 대한 POST 요청을 처리하는 메서드
    @ResponseBody
    @PostMapping(value = "changeUser")
    public MsgDTO changeUser(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".changeUser Start!");

        // 사용자 정보를 가져옴
        String userId = CmmUtil.nvl(request.getParameter("userId"));
        String email = CmmUtil.nvl(request.getParameter("email"));
        String nickname = CmmUtil.nvl(request.getParameter("nickname"));

        log.info("userId : " + userId);
        log.info("email : " + email);
        log.info("nickname : " + nickname);

        // 암호화된 이메일과 사용자 정보를 설정
        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId)
                .email(EncryptUtil.encAES128CBC(email))
                .nickname(nickname)
                .build();

        int res = 0;

        int success = Optional.ofNullable(userInfoService.updateUserInfo(pDTO))
                .orElse(0);

        String msg ="";

        // 변경 성공 시
        if(success != 0) {
            msg = "변경시켰습니다";
            res = 1;
        }

        MsgDTO dto = MsgDTO.builder().msg(msg).result(res).build();
        log.info(dto.toString());

        log.info(this.getClass().getName() + ".changeUser End!");

        return dto;
    }

    @GetMapping(value = "/reportList")
    public String reportList(ModelMap model, @RequestParam(defaultValue = "1") int page)
            throws Exception {

        log.info(this.getClass().getName() + ".reportList Start!");

        // 페이지 계산을 위한 변수 설정
        int from = (page - 1) * 10 + 1;
        int to = page * 10;

        // UserInfoDTO 객체 생성
        ReportDTO pDTO = ReportDTO.builder().from(from).to(to).build();

        // 사용자 목록을 가져옴
        List<Map<String, Object>> pList = reportService.getReportList(pDTO);
        if (pList == null) pList = new ArrayList<>();

        log.info(pList.toString());

        // 사용자 정보를 담을 리스트 생성
        List<ReportDTO> rList = new ArrayList<>();

        // 사용자 목록을 순회하며 DTO로 변환
        for (Map<String, Object> rMap : pList) {
            if (!String.valueOf(rMap.get("userId")).equals("admin")) {
                ReportDTO rDTO = ReportDTO.builder()
                        .userId(String.valueOf(rMap.get("userId")))
                        .reportId(String.valueOf(rMap.get("reportId")))
                        .reason(String.valueOf(rMap.get("reason")))
                        .regDt(String.valueOf(rMap.get("regDt")))
                        .build();

                rList.add(rDTO);
            }
        }

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 10;

        // 전체 페이지 개수 계산
        int totalPages = (int) Math.ceil((double) Optional.ofNullable(reportService.getReportCount()).orElse(0) / itemsPerPage);


        // 모델에 필요한 데이터를 추가
        model.addAttribute("rList", rList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        log.info(this.getClass().getName() + ".reportList");

        // 뷰 이름 반환
        return "admin/reportList";
    }
}
