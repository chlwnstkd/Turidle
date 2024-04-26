package kopo.poly.service.impl;

import kopo.poly.dto.BasketDTO;
import kopo.poly.persistance.mapper.IBasketMapper;
import kopo.poly.service.IBasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasketService implements IBasketService {
    private final IBasketMapper BasketMapper;


    /*  좋아요 정보 등록 코드 */
    @Transactional
    @Override
    public int getBasket(BasketDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getBasket start!");
        return BasketMapper.getBasket(pDTO);
    }

    /*  좋아요 정보 등록 코드 */
    @Transactional
    @Override
    public void insertBasket(BasketDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".insertBasket start!");
        BasketMapper.insertBasket(pDTO);
    }

    /* 좋아요 정보 삭제 코드 */
    @Override
    public void deleteBasket(BasketDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".deleteBasket start!");
        BasketMapper.deleteBasket(pDTO);
    }

}
