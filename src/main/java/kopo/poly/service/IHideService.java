package kopo.poly.service;

import kopo.poly.dto.HideDTO;

public interface IHideService {
    int getHide(HideDTO pDTO) throws Exception; // 숨김 정보 조회
    void insertHide(HideDTO pDTO) throws Exception; // 숨김 정보 등록
    void deleteHide(HideDTO pDTO) throws Exception; // 숨김 정보 삭제
}
