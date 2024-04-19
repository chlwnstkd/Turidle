package kopo.poly.service;

import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.LikeDTO;

import java.util.List;
import java.util.Map;

public interface ICommentService {
    int getCommentCount(CommentDTO pDTO) throws Exception; // 댓글 정보 조회
    List<Map<String, Object>> getCommentList(CommentDTO pDTO) throws Exception; // 댓글 목록 조회

    Map<String, Object> getComment(String commentNumber) throws Exception; // 댓글 조회

    void deleteComment(CommentDTO pDTO) throws Exception; // 댓글 삭제
    void insertComment(CommentDTO pDTO) throws Exception; // 댓글 등록

    void updateComment(CommentDTO pDTO) throws Exception; // 댓글 수정

}
