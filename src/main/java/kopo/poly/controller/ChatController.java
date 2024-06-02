package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kopo.poly.chat.ChatHandler;
import kopo.poly.dto.ChatDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IChatService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));

        try {
            UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

            log.info("userId : " + userId);

            Map<String, Object> rMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO)).orElseGet(HashMap::new);

            String email = String.valueOf(rMap.get("email"));

            if (email.equals("null")) {
                email = "";
            }else {
                email = EncryptUtil.decAES128CBC(email);
            }

            UserInfoDTO rDTO = UserInfoDTO.builder(
            ).userId(String.valueOf(rMap.get("userId"))
            ).nickname(String.valueOf(rMap.get("nickname"))
            ).email(email
            ).regDt(String.valueOf(rMap.get("regDt"))
            ).build();

            log.info("rDTO : " + rDTO);

            model.addAttribute("roomName", userId);
            model.addAttribute("rDTO", rDTO);
        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();
        }
        log.info(this.getClass().getName() + ".chatroom End!");

        return "chat/chatroom";
    }

    @RequestMapping(value = "saveMessage")
    @ResponseBody
    public int saveMessage(HttpServletRequest request) throws Exception{

        log.info(this.getClass().getName() + ".saveMessage Start!");

        int res = 0;

        String name = CmmUtil.nvl(request.getParameter("name"));
        String msg = CmmUtil.nvl(request.getParameter("msg"));
        String userId = CmmUtil.nvl(request.getParameter("userId"));
        String roomName = CmmUtil.nvl(request.getParameter("roomName"));

        log.info("userId : " + userId);

        ChatDTO pDTO = ChatDTO.builder(
        ).userId(userId
        ).name(name
        ).roomName(roomName
        ).date(DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss")
        ).msg(msg).build();

        log.info("pDTO : " + pDTO);
        if (userId.equals("admin")) {
            int res1 = chatService.saveMessage(pDTO);
            int res2 = chatService.deleteUser(roomName, userId);
            res = res1 * res2;
        } else {
            res = chatService.saveMessage(pDTO);
        }
        log.info(this.getClass().getName() + ".saveMessage Ends!");

        return res;

    }
}