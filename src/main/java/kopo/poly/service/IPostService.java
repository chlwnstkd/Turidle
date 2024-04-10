package kopo.poly.service;

import kopo.poly.dto.PostDTO;

import java.util.List;

public interface IPostService {
    List<PostDTO> getPostList(String type) throws Exception; // 게시판 목록 조회
    void insertPostInfo(PostDTO pDTO) throws Exception; // 게시글 등록
    PostDTO getPostInfo(PostDTO pDTO) throws Exception; // 게시글 정보 조회
    int getPostNumber() throws Exception; // 게시글 번호 조회
    void updatePostReadCnt(PostDTO pDTO) throws Exception; // 게시글 조회수 증가
    void updatePostInfo(PostDTO pDTO) throws Exception; // 게시글 수정
    void deletePostInfo(PostDTO pDTO) throws Exception; // 게시글 정보 삭제
}
