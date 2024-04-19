package kopo.poly.service;

import kopo.poly.dto.LikeDTO;
import kopo.poly.dto.PostDTO;

import java.util.List;
import java.util.Map;

public interface IPostService {
    List<Map<String, Object>> getPostList() throws Exception; // 게시판 목록 조회
    void insertPostInfo(PostDTO pDTO) throws Exception; // 게시글 등록
    PostDTO getPostInfo(PostDTO pDTO) throws Exception; // 게시글 정보 조회
    void updatePostInfo(PostDTO pDTO) throws Exception; // 게시글 수정
    void deletePostInfo(PostDTO pDTO) throws Exception; // 게시글 정보 삭제
}
