package kopo.poly.service;

import kopo.poly.dto.BasketDTO;
import kopo.poly.dto.CommentDTO;

import java.util.List;
import java.util.Map;

public interface IBasketService {
    int getBasket(BasketDTO pDTO) throws Exception; // 보관함 정보 조회
    List<Map<String, Object>> getBasketList(BasketDTO pDTO) throws Exception; //  보관함 목록 조회

    void insertBasket(BasketDTO pDTO) throws Exception; // 보관함 정보 등록
    void deleteBasket(BasketDTO pDTO) throws Exception; // 보관함 정보 삭제
    int getBasketCount(BasketDTO pDTO) throws Exception; // 보관함 아이템 개수 조회
}
