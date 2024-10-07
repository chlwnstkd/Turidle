package kopo.poly.service.impl;

import kopo.poly.dto.HideDTO;
import kopo.poly.persistance.mapper.IHideMapper;
import kopo.poly.service.IHideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class HideService implements IHideService {
    private final IHideMapper hideMapper;
    

    /*  숨김 정보 등록 코드 */
    @Transactional
    @Override
    public int getHide(HideDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getHide start!");
        return hideMapper.getHide(pDTO);
    }

    /*  숨김 정보 등록 코드 */
    @Transactional
    @Override
    public void insertHide(HideDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".insertHide start!");
        hideMapper.insertHide(pDTO);
    }

    /* 숨김 정보 삭제 코드 */
    @Override
    public void deleteHide(HideDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".deleteHide start!");
        hideMapper.deleteHide(pDTO);
    }

}
