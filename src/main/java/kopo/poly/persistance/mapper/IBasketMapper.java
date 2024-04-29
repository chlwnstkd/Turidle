package kopo.poly.persistance.mapper;

import kopo.poly.dto.BasketDTO;
import kopo.poly.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IBasketMapper {
    int getBasket(BasketDTO pDTO) throws Exception; // 보관함 정보 조회
    List<Map<String, Object>> getBasketList(BasketDTO pDTO) throws Exception; // 보관함 목록 조회

    void insertBasket(BasketDTO pDTO) throws Exception; // 보관함 저장
    void deleteBasket(BasketDTO pDTO) throws Exception; // 보관함 삭제
}
