package kopo.poly.service.impl;

import kopo.poly.dto.MailDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.IUserInfoMapper;
import kopo.poly.service.IMailService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoService implements IUserInfoService {
    private final IUserInfoMapper userInfoMapper;

    private final IMailService mailService;

    @Override
    public String getUserExists(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        String userId = CmmUtil.nvl(pDTO.userId());

        log.info("userId : " + userId);

        String rDTO = userInfoMapper.getUserIdExists(pDTO);

        log.info(this.getClass().getName() + ".getUserIdExists End!");

        return rDTO;
    }

    @Override
    public UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".emailAuth Start!");

        String existsYn = userInfoMapper.getEmailExists(pDTO);



        int authNumber = 0;

        log.info("existsYn : " + existsYn);

        log.info(pDTO.nickname());

        if (existsYn.equals("N") && pDTO.nickname().equals("") || existsYn.equals("Y") && !pDTO.nickname().equals("")) {
            authNumber = ThreadLocalRandom.current().nextInt(100000,1000000);

            String title = "이메일 중복 확인 인증번호 발송 메일";
            String contents = "인증번호는 " + authNumber + " 입니다.";
            String toMail = CmmUtil.nvl(EncryptUtil.decAES128CBC(pDTO.email()));

            MailDTO dto = MailDTO.builder().title(title).contents(contents).toMail(toMail).build();

            mailService.doSendMail(dto);

            dto = null;

            log.info("authNumber : " + authNumber);
        }

        UserInfoDTO rDTO = UserInfoDTO.builder().existsYn(existsYn).authNumber(authNumber).build();

        log.info(this.getClass().getName() + ".emailAuth End!");

        return rDTO;
    }


    @Override
    public int updateUserInfo(UserInfoDTO pDTO) throws Exception {

        int res = 0;

        int success = userInfoMapper.updateUserInfo(pDTO);

        if(success > 0) {
            res = 1;
        }

        return res;
    }

    @Override
    public int deleteUser(UserInfoDTO pDTO) throws Exception {

        int res = 0;

        int success = userInfoMapper.deleteUser(pDTO);

        if(success > 0) {
            res = 1;
        }

        return res;
    }

    @Override
    public int insertUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertUserInfo Start!");

        int res = 0;

        String userId = CmmUtil.nvl(pDTO.userId());
        String nickname = CmmUtil.nvl(pDTO.nickname());
        String password = CmmUtil.nvl(pDTO.password());
        String email = CmmUtil.nvl(pDTO.email());

        log.info("pDTO : " + pDTO);

        String existsYn = userInfoMapper.getUserIdExists(pDTO);

        if(existsYn == "Y") {
            res = 2;
        }else {
            UserInfoDTO rDTO = UserInfoDTO.builder()
                    .userId(userId).nickname(nickname)
                    .password(password)
                    .email(email)
                    .regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .build();

            res = userInfoMapper.insertUserInfo(rDTO);

        }

        log.info(this.getClass().getName() + ".insertUserInfo End!");

        return res;
    }

    @Override
    public int getUserLogin(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getUserLogin Start!");

        int res = 0;

        Map<String, Object> rMap = userInfoMapper.getLogin(pDTO);

        if(rMap != null) {
            res = 1;
        }

        log.info(this.getClass().getName() + ".getUserLogin End!");


        return res;
    }
    @Override
    public Map<String, Object> searchUserIdOrPasswordProc(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".searchUserIdOrPasswordProc Start!");

        Map<String, Object> rMap = userInfoMapper.getUserId(pDTO);

        log.info(this.getClass().getName() + ".searchUserIdOrPasswordProc End!");

        return rMap;
    }
    @Override
    public Map<String, Object> getUserInfo(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getUserInfo Start!");


        Map<String, Object> rMap = userInfoMapper.getUserInfo(pDTO);

        log.info(this.getClass().getName() + ".getUserInfo Start!");

        return rMap;
    }


    @Override
    public int newPasswordProc(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".newPasswordProc Start!");

        int success = userInfoMapper.updatePassword(pDTO);

        log.info(this.getClass().getName() + ".newPasswordProc End!");

        return success;
    }
}
