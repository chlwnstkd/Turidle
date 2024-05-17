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
     * 채팅창 입장 전
     */
    @GetMapping(value = "intro")
    public String intro() {

        log.info(this.getClass().getName() + ".intro Start!");

        log.info(this.getClass().getName() + ".intro Ends!");

        return "/chat/intro";
    }

    /**
     * 채팅창 접속
     */
    @GetMapping(value = "chatroom")
    public String chatroom(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".chatroom Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        Map<String, Object> rMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO)).orElseGet(HashMap::new);

        UserInfoDTO rDTO = UserInfoDTO.builder(
        ).userId(String.valueOf(rMap.get("userId"))
        ).nickname(String.valueOf(rMap.get("nickname"))
        ).email(EncryptUtil.decAES128CBC(String.valueOf(rMap.get("email")))
        ).regDt(String.valueOf(rMap.get("regDt"))
        ).build();

        String roomName = EncryptUtil.encHashSHA256(userId);

        model.addAttribute("roomName", roomName);
        model.addAttribute("pDTO", rDTO);

        log.info(this.getClass().getName() + ".chatroom End!");

        return "/chat/chatroom";
    }

    /**
     * 채팅방 목록
     */
    @RequestMapping(value = "saveMessage")
    @ResponseBody
    public int saveMessage(@Valid @RequestBody ChatDTO pDTO) throws Exception{

        log.info(this.getClass().getName() + ".saveMessage Start!");

        log.info("pDTO : " + pDTO);

        pDTO = pDTO.toBuilder().date(DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss")).build();

        int res = chatService.saveMessage(pDTO);

        log.info(this.getClass().getName() + ".saveMessage Ends!");

        return res;

    }
}