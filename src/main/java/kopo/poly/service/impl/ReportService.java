package kopo.poly.service.impl;

import kopo.poly.dto.ReportDTO;
import kopo.poly.persistance.mapper.IReportMapper;
import kopo.poly.service.IReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportService implements IReportService {
    private final IReportMapper reportMapper;

    /* 댓글 목록 조회 코드 */
    @Override
    public List<Map<String, Object>> getReportList(ReportDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getReportList Start!");

        log.info("값은 : " + reportMapper.getReportList(pDTO).toString());

        return reportMapper.getReportList(pDTO);
    }
    @Override
    public int getReportCount() throws Exception {

        log.info(this.getClass().getName() + ".getReportCount start!");

        return reportMapper.getReportCount();
    }
    
    /*  좋아요 정보 등록 코드 */
    @Transactional
    @Override
    public int getReport(ReportDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getReport start!");
        return reportMapper.getReport(pDTO);
    }

    /*  좋아요 정보 등록 코드 */
    @Transactional
    @Override
    public void insertReport(ReportDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".insertReport start!");
        reportMapper.insertReport(pDTO);
    }
    

}
