package kopo.poly.service;

import kopo.poly.dto.BasketDTO;

public interface IBasketService {
    int getBasket(BasketDTO pDTO) throws Exception; // 좋아요 정보 조회
    void insertBasket(BasketDTO pDTO) throws Exception; // 좋아요 정보 등록
    void deleteBasket(BasketDTO pDTO) throws Exception; // 좋아요 정보 삭제
}
