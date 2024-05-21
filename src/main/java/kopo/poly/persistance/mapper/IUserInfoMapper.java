package kopo.poly.persistance.mapper;

import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface IUserInfoMapper {


    List<Map<String, Object>> getUserList() throws Exception;
    List<Map<String, Object>> getChatList() throws Exception;


    //회원 가입하기(회원정보 등록하기)
    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

    Map<String, Object> getUserInfo(UserInfoDTO pDTO) throws Exception;


    //회원 가입 전 아이디 중복체크하기(DB조회하기)
    String getUserIdExists(UserInfoDTO pDTO) throws Exception;

    // 회원 가입 전 이메일 중복체크하기(DB조회하기)
    String getEmailExists(UserInfoDTO pDTO) throws Exception;

    // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기
    Map<String, Object> getLogin(UserInfoDTO pDTO) throws Exception;
    /*
     아이디, 비밀번호 찾기에 활용
     1. 이름과 이메일 맞다면, 아이디 알려주기
     2. 아이디, 이름과 이메일이 맞다면, 비밀번호 재설정하기
     */
    Map<String, Object> getUserId(UserInfoDTO pDTO) throws Exception;

    int updatePassword(UserInfoDTO pDTO) throws Exception;
    int deleteUser(UserInfoDTO pDTO) throws Exception;
    int updateUserInfo(UserInfoDTO pDTO) throws Exception; // 소비자 정보 수정

}