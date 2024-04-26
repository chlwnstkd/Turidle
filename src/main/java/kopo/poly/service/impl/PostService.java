package kopo.poly.service.impl;

import kopo.poly.dto.LikeDTO;
import kopo.poly.dto.PostDTO;
import kopo.poly.persistance.mapper.IPostMapper;
import kopo.poly.service.IPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService implements IPostService {
    private final IPostMapper postMapper;

    /* 게시글 목록 조회 코드 */
    @Override
    public List<Map<String, Object>> getPostList() throws Exception {

        log.info(this.getClass().getName() + ".getPostList start!");

        return postMapper.getPostList();
    }

    /* 게시글 정보 조회 코드 */
    @Transactional
    @Override
    public PostDTO getPostInfo(PostDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getPostInfo start!");

        log.info("Update ReadCNT");
        postMapper.updatePostReadCnt(pDTO);

        Map<String, Object> rMap = postMapper.getPostInfo(pDTO);

        log.info(String.valueOf(rMap.get("contents")));

        PostDTO rDTO = PostDTO.builder().postNumber(String.valueOf(rMap.get("postNumber"))
        ).contents(String.valueOf(rMap.get("contents"))
        ).readCount(String.valueOf(rMap.get("readCount"))
        ).regDt(String.valueOf(rMap.get("regDt"))
        ).regId(String.valueOf(rMap.get("nickname"))
        ).title(String.valueOf(rMap.get("title"))
        ).build();

        return rDTO;
    }

    /* 게시글 정보 등록 코드 */
    @Transactional
    @Override
    public void insertPostInfo(PostDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".InsertPostInfo start!");
        postMapper.insertPostInfo(pDTO);
    }

    /* 게시글 정보 수정 코드 */
    @Transactional
    @Override
    public void updatePostInfo(PostDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".updatePostInfo start!");
        postMapper.updatePostInfo(pDTO);
    }

    /* 게시글 정보 삭제 코드 */
    @Override
    public void deletePostInfo(PostDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".deletePostInfo start!");
        postMapper.deletePostInfo(pDTO);
    }
}
