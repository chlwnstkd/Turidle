package kopo.poly.service.impl;

import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.LikeDTO;
import kopo.poly.persistance.mapper.ICommentMapper;
import kopo.poly.service.ICommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final ICommentMapper commentMapper;

    /*  좋아요 개수 등록 코드 */
    @Override
    public int getCommentCount(CommentDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getCommentCount start!");
        return commentMapper.getCommentCount(pDTO);
    }

    /* 댓글 목록 조회 코드 */
    @Override
    public List<Map<String, Object>> getCommentList(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getCommentList Start!");

        log.info("값은 : " + commentMapper.getCommentList(pDTO).toString());

        return commentMapper.getCommentList(pDTO);
    }

    /* 댓글 조회 코드 */
    @Override
    public Map<String, Object> getComment(String commentNumber) throws Exception {

        log.info(this.getClass().getName() + ".getComment Start!");

        return commentMapper.getComment(commentNumber);
    }
    /* 댓글 삭제 코드 */
    @Override
    public void deleteComment(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteComment Start!");

        commentMapper.deleteComment(pDTO);
    }
    @Transactional
    @Override
    public void insertComment(CommentDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".InsertComment start!");
        commentMapper.insertComment(pDTO);
    }
    @Transactional
    @Override
    public void updateComment(CommentDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".updateComment start!");
        commentMapper.updateComment(pDTO);
    }
}
