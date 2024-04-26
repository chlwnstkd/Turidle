package kopo.poly.persistance.mapper;

import kopo.poly.dto.BasketDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IBasketMapper {
    int getBasket(BasketDTO pDTO) throws Exception; // 보관함 정보 조회
    void insertBasket(BasketDTO pDTO) throws Exception; // 보관함 저장
    void deleteBasket(BasketDTO pDTO) throws Exception; // 보관함 삭제
}
