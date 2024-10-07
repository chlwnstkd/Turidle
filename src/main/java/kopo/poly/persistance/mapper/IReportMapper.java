package kopo.poly.persistance.mapper;

import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.ReportDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IReportMapper {
    List<Map<String, Object>> getReportList(ReportDTO pDTO) throws Exception; // 신고 목록 조회
    int getReportCount() throws Exception;
    int getReport(ReportDTO pDTO) throws Exception;
    void insertReport(ReportDTO pDTO) throws Exception; // 신고 정보 등록
}
