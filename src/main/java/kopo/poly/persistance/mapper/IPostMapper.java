package kopo.poly.persistance.mapper;

import kopo.poly.dto.LikeDTO;
import kopo.poly.dto.PostDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IPostMapper {
    List<Map<String, Object>> getPostList(PostDTO pDTO) throws Exception; // 게시판 목록 조회
    int getPostCount() throws Exception; //게시판 개수 조회
    void insertPostInfo(PostDTO pDTO) throws Exception; // 게시글 등록
    Map<String, Object> getPostInfo(PostDTO pDTO) throws Exception; // 게시글 정보 조회
    void updatePostReadCnt(PostDTO pDTO) throws Exception; // 게시글 조회수 증가
    void updatePostInfo(PostDTO pDTO) throws Exception; // 게시글 수정
    void deletePostInfo(PostDTO pDTO) throws Exception; // 게시글 정보 삭제

}
