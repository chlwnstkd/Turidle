package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.chat.ChatHandler;
import kopo.poly.util.CmmUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

@Slf4j
@Controller
@RequestMapping(value = "/chat")
public class ChatController {

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
    public String chatroom(HttpServletRequest request, ModelMap model) {

        log.info(this.getClass().getName() + ".chatroom Start!");

        String userName = CmmUtil.nvl(request.getParameter("userName"));
        String roomName = CmmUtil.nvl(request.getParameter("roomName"));

        model.addAttribute("userName", userName);
        model.addAttribute("roomName", roomName);

        log.info(this.getClass().getName() + ".chatroom End!");

        return "/chat/chatroom";
    }

    /**
     * 채팅방 목록
     */
    @RequestMapping(value = "roomList")
    @ResponseBody
    public Set<String> roomList() {

        log.info(this.getClass().getName() + ".roomList Start!");

        log.info(this.getClass().getName() + ".roomList Ends!");

        return ChatHandler.roomInfo.keySet();

    }
}