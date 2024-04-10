package kopo.poly.service.impl;

import kopo.poly.dto.PostDTO;
import kopo.poly.persistance.mapper.IPostMapper;
import kopo.poly.service.IPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService implements IPostService {
    private final IPostMapper postMapper;

    /* 게시글 목록 조회 코드 */
    @Override
    public List<PostDTO> getPostList(String type) throws Exception {

        log.info(this.getClass().getName() + ".getPostList start!");

        return postMapper.getPostList(type);
    }

    /* 게시글 정보 조회 코드 */
    @Transactional
    @Override
    public PostDTO getPostInfo(PostDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getPostInfo start!");

            log.info("Update ReadCNT");
            postMapper.updatePostReadCnt(pDTO);

        return postMapper.getPostInfo(pDTO);
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

    /* 게시글 번호 조회 코드 */
    @Override
    public int getPostNumber() throws Exception {
        return 0;
    }

    /* 게시글 조회수 증가 코드 */
    @Override
    public void updatePostReadCnt(PostDTO pDTO) throws Exception {

    }
}
