package kopo.poly.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record ChatDTO (
        String name, // 이름
        String msg, // 채팅 메시지
        String date, // 발송날짜
        String userId, //유저 아이디
        String roomName // 채팅방 이름
){
}
