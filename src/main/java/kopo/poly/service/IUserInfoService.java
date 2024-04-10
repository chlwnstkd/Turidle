package kopo.poly.service;

import kopo.poly.dto.UserInfoDTO;

import java.util.Map;


public interface IUserInfoService {
    String getUserExists(UserInfoDTO pDTO) throws Exception;
    UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception;

    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

    int getUserLogin(UserInfoDTO pDTO) throws Exception;
    Map<String, Object> searchUserIdOrPasswordProc(UserInfoDTO pDTO) throws Exception;
    int newPasswordProc(UserInfoDTO pDTO) throws Exception;
}
