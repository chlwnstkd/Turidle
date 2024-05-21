package kopo.poly.service;

import kopo.poly.dto.UserInfoDTO;

import java.util.List;
import java.util.Map;


public interface IUserInfoService {
    String getUserExists(UserInfoDTO pDTO) throws Exception;
    UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception;

    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

    List<Map<String, Object>> getUserList() throws Exception;

    int getUserLogin(UserInfoDTO pDTO) throws Exception;
    Map<String, Object> getUserInfo(UserInfoDTO pDTO) throws Exception;
    Map<String, Object> searchUserIdOrPasswordProc(UserInfoDTO pDTO) throws Exception;
    int newPasswordProc(UserInfoDTO pDTO) throws Exception;
    int updateUserInfo(UserInfoDTO pDTO) throws Exception;
    int deleteUser(UserInfoDTO pDTO) throws Exception;

}
