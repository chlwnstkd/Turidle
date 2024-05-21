package kopo.poly.persistance.mapper;

import kopo.poly.dto.ChatDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IChatMapper {
    int saveMessage(ChatDTO pDTO) throws Exception; // 채팅 정보 조회

    List<ChatDTO> getMessage(String roomName) throws Exception;
}
