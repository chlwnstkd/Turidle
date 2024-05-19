package kopo.poly.service.impl;

import kopo.poly.dto.ChatDTO;
import kopo.poly.persistance.mapper.IChat2Mapper;
import kopo.poly.persistance.mapper.IChatMapper;
import kopo.poly.persistance.mapper.IUserInfoMapper;
import kopo.poly.service.IChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService implements IChatService {

    private final IChatMapper chatMapper;
    private final IChat2Mapper chat2Mapper;
    private final IUserInfoMapper userInfoMapper;


    @Override
    public int saveMessage(ChatDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".saveMessage Start!");

        int res1 = chatMapper.saveMessage(pDTO);

        int res2 = chat2Mapper.insertUser(pDTO.userId());

        int res = res1 * res2;

        log.info(this.getClass().getName() + ".saveMessage Start!");

        return res;
    }

    @Override
    public List<ChatDTO> getMessage(String roomName) throws Exception {
        log.info(this.getClass().getName() + ".getSingerSong Start!");

        List<ChatDTO> rList = null;

        rList = chatMapper.getMessage(roomName);

        log.info(this.getClass().getName() + ".getSingerSong End!");

        return rList;
    }
    @Override
    public List<Map<String, Object>> getChatList() throws Exception {

        log.info(this.getClass().getName() + ".getChatList start!");

        return userInfoMapper.getChatList();
    }

    @Override
    public int deleteUser(String userId) throws Exception {

        log.info(this.getClass().getName() + ".getChatList start!");

        return chat2Mapper.deleteUser(userId);
    }
}
