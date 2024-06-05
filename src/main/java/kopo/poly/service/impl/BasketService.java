package kopo.poly.service.impl;

import kopo.poly.dto.BasketDTO;
import kopo.poly.dto.CommentDTO;
import kopo.poly.persistance.mapper.IBasketMapper;
import kopo.poly.service.IBasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasketService implements IBasketService {
    private final IBasketMapper basketMapper;


    /*  보관함 정보 등록 코드 */
    @Transactional
    @Override
    public int getBasket(BasketDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getBasket start!");
        return basketMapper.getBasket(pDTO);
    }

    /*   보관함 아이템 조회 코드   */
    @Override
    public int getBasketCount(BasketDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getBasketCount start!");
        return basketMapper.getBasketCount(pDTO);
    }

    /* 보관함 목록 조회 코드 */
    @Override
    public List<Map<String, Object>> getBasketList(BasketDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getBasketList Start!");

        return basketMapper.getBasketList(pDTO);
    }

    /*  좋아요 정보 등록 코드 */
    @Transactional
    @Override
    public void insertBasket(BasketDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".insertBasket start!");
        basketMapper.insertBasket(pDTO);
    }

    /* 좋아요 정보 삭제 코드 */
    @Override
    public void deleteBasket(BasketDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".deleteBasket start!");
        basketMapper.deleteBasket(pDTO);
    }

}
