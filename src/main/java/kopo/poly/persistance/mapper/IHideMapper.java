package kopo.poly.persistance.mapper;

import kopo.poly.dto.HideDTO;
import kopo.poly.dto.LikeDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IHideMapper {
    int getHide(HideDTO pDTO) throws Exception; // 숨김 정보 조회
    void insertHide(HideDTO pDTO) throws Exception; // 숨김 정보 등록
    void deleteHide(HideDTO pDTO) throws Exception; // 숨김 정보 삭제
}
