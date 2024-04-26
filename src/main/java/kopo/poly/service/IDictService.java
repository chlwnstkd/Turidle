package kopo.poly.service;

import kopo.poly.dto.DictDTO;

import java.util.List;
import java.util.Map;

public interface IDictService {
    List<DictDTO> getDictList(String text) throws Exception; // 사전 검색 조회
    DictDTO getDictInfo(String text) throws Exception; // 사전 상세 조회

}
