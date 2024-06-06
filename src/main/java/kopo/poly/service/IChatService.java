package kopo.poly.service;

import kopo.poly.dto.ChatDTO;
import kopo.poly.dto.UserInfoDTO;

import java.util.List;
import java.util.Map;

public interface IChatService {

    int saveMessage(ChatDTO pDTO) throws Exception;
    List<ChatDTO> getMessage(String roomName) throws Exception;

    List<Map<String, Object>> getChatList(UserInfoDTO pDTO) throws Exception;

    int getChatCount() throws Exception;
    int deleteUser(String roomName, String userId) throws Exception;
}
