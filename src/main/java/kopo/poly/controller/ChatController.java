package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.ChatDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IChatService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/chat")
public class ChatController {

    private final IUserInfoService userInfoService;
    private final IChatService chatService;

    /**
     * 채팅창 접속
     */
    @GetMapping(value = "chatroom")
    public String chatroom(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".chatroom Start!");

        // 세션에서 사용자 ID 가져오기
        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));

        try {
            // 사용자 정보 DTO 생성
            UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

            log.info("userId : " + userId);

            // 사용자 정보 조회
            Map<String, Object> rMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO)).orElseGet(HashMap::new);

            // 이메일 복호화
            String email = String.valueOf(rMap.get("email"));

            if (email.equals("null")) {
                email = "";
            } else {
                email = EncryptUtil.decAES128CBC(email);
            }

            // 조회된 사용자 정보를 DTO에 설정
            UserInfoDTO rDTO = UserInfoDTO.builder()
                    .userId(String.valueOf(rMap.get("userId")))
                    .nickname(String.valueOf(rMap.get("nickname")))
                    .email(email)
                    .regDt(String.valueOf(rMap.get("regDt")))
                    .build();

            log.info("rDTO : " + rDTO);

            // 모델에 필요한 데이터 추가
            model.addAttribute("roomName", userId);
            model.addAttribute("rDTO", rDTO);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        log.info(this.getClass().getName() + ".chatroom End!");

        // 채팅방 뷰 반환
        return "chat/chatroom";
    }

    /**
     * 메시지 저장
     */
    @PostMapping(value = "saveMessage")
    @ResponseBody
    public int saveMessage(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".saveMessage Start!");

        int res = 0;

        // 요청 파라미터에서 데이터 가져오기
        String name = CmmUtil.nvl(request.getParameter("name"));
        String msg = CmmUtil.nvl(request.getParameter("msg"));
        String userId = CmmUtil.nvl(request.getParameter("userId"));
        String roomName = CmmUtil.nvl(request.getParameter("roomName"));

        log.info("userId : " + userId);

        // 채팅 메시지 DTO 생성
        ChatDTO pDTO = ChatDTO.builder()
                .userId(userId)
                .name(name)
                .roomName(roomName)
                .date(DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss"))
                .msg(msg)
                .build();

        log.info("pDTO : " + pDTO);

        // 관리자인 경우, 메시지 저장 후 사용자 삭제
        if ("admin".equals(userId)) {
            int res1 = chatService.saveMessage(pDTO);
            int res2 = chatService.deleteUser(roomName, userId);
            res = res1 * res2;
        } else {
            // 일반 사용자는 메시지 저장
            res = chatService.saveMessage(pDTO);
        }

        log.info(this.getClass().getName() + ".saveMessage Ends!");

        return res;
    }
}
