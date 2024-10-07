package kopo.poly.service;

import kopo.poly.dto.ReportDTO;

import java.util.List;
import java.util.Map;

public interface IReportService {
    List<Map<String, Object>> getReportList(ReportDTO pDTO) throws Exception; // 신고 목록 조회
    int getReportCount() throws Exception; //게시판 개수 조회
    int getReport(ReportDTO pDTO) throws Exception; // 신고 정보 조회
    void insertReport(ReportDTO pDTO) throws Exception; // 신고 정보 등록
}
