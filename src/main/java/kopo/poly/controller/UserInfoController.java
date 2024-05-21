package kopo.poly.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.MailDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IMailService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Controller
public class UserInfoController {
    private final IUserInfoService userInfoService;
    private final IMailService mailService;

    @GetMapping(value = "login")
    public String userRegForm() {

        log.info(this.getClass().getName() + ".login Start!");

        return "/user/login";
    }
    @GetMapping(value = "searchId")
    public String searchId() {

        log.info(this.getClass().getName() + ".login Start!");

        return "/user/searchId";
    }

    @GetMapping(value = "newPassword")
    public String newPassword() {

        log.info(this.getClass().getName() + ".newPassword Start!");

        return "/user/newPassword";
    }

    @GetMapping(value = "userInfo")
    public String userInfo(HttpSession session, ModelMap model) throws Exception {

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));

        log.info(userId);

        String url = "/user/userInfo";
        if(!userId.equals("")) {
            UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();
            Map<String, Object> rMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO))
                    .orElseGet(HashMap::new);

            UserInfoDTO rDTO = UserInfoDTO.builder(
            ).userId(String.valueOf(rMap.get("userId"))
            ).nickname(String.valueOf(rMap.get("nickname"))
            ).email(EncryptUtil.decAES128CBC(String.valueOf(rMap.get("email")))
            ).regDt(String.valueOf(rMap.get("regDt"))
            ).build();

            log.info(rDTO.userId() + "/" + rDTO.nickname() + "/" + rDTO.email());

            model.addAttribute("rDTO", rDTO);
        }else{
            url = "/index";
        }
        return url;
    }



    @ResponseBody
    @PostMapping(value = "getUserIdExists")
    public UserInfoDTO getUserIdExists(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        String userId = CmmUtil.nvl(request.getParameter("userId"));

        log.info("userId : " + userId);

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        String yn = Optional.ofNullable(userInfoService.getUserExists(pDTO))
                .orElseGet(String::new);

        log.info(yn);

        UserInfoDTO rDTO = UserInfoDTO.builder().existsYn(yn).build();

        log.info(this.getClass().getName() + ".getUserIdExists End!");

        return rDTO;

    }
    @ResponseBody
    @PostMapping(value = "getEmailExists")
    public UserInfoDTO getEmailExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getEmailExists Start!");

        String find = CmmUtil.nvl(request.getParameter("find"));
        String email = CmmUtil.nvl(request.getParameter("email")); // 회원아이디

        log.info("email : " + email);

        UserInfoDTO pDTO = UserInfoDTO.builder().nickname(find).email(EncryptUtil.encAES128CBC(email)).build();

        // 입력된 이메일이 중복된 이메일인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getEmailExists(pDTO)).orElseGet(() -> UserInfoDTO.builder().build());

        log.info(this.getClass().getName() + ".getEmailExists End!");

        return rDTO;
    }

    @ResponseBody
    @PostMapping(value = "insertUserInfo")
    public MsgDTO insertUserInfo(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".insertUserInfo Start!");

        String msg;

        String userId = CmmUtil.nvl(request.getParameter("userId"));
        String nickname = CmmUtil.nvl(request.getParameter("nickname"));
        String password = CmmUtil.nvl(request.getParameter("pw"));
        String email = CmmUtil.nvl(request.getParameter("email"));

        log.info("userId : " + userId);
        log.info("userName : " + nickname);
        log.info("password : " + password);
        log.info("email : " + email);

        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId)
                .nickname(nickname)
                .password(EncryptUtil.encHashSHA256(password))
                .email(EncryptUtil.encAES128CBC(email))
                .build();

        int res = userInfoService.insertUserInfo(pDTO);

        log.info("회원가입 결과(res) : " + res);

        if (res == 1) {
            msg = "회원가입되었습니다";

        } else if(res == 2) {
            msg = "이미 가입된 아이디 입니다";
        } else {
            msg = "오류로 인해 회원가입에 실패하였습니다";
        }

        MsgDTO dto = MsgDTO.builder().result(res).msg(msg).build();

        log.info(this.getClass().getName() + ".insertUserInfo End!");

        return dto;

    }

    @ResponseBody
    @PostMapping(value = "loginProc")
    public MsgDTO loginProc(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".loginProc Start!");

        String msg;

        String userId = CmmUtil.nvl(request.getParameter("id"));
        String password = CmmUtil.nvl(request.getParameter("password"));

        log.info("userId : " + userId);
        log.info("password : " + password);

        MsgDTO dto;


        if (userId.equals("admin") && password.equals("1234")) {
            dto = MsgDTO.builder().result(2).msg("관리자 페이지에 로그인합니다").build();
            session.setAttribute("SS_USER", userId);
        }else {
            UserInfoDTO pDTO = UserInfoDTO.builder()
                    .userId(userId)
                    .password(EncryptUtil.encHashSHA256(password))
                    .build();

            int res = userInfoService.getUserLogin(pDTO);

            log.info("회원가입 결과(res) : " + res);

            if (res == 1) {
                msg = "로그인이 성공했습니다";
                session.setAttribute("SS_USER", userId);
            } else {
                msg = "아이디와 비밀번호가 올바르지 않습니다";
            }

            dto = MsgDTO.builder().result(res).msg(msg).build();
        }
        log.info(this.getClass().getName() + ".loginProc End!");

        return dto;

    }
    /**
     * 아아디 찾기 로직 수행
     */
    @ResponseBody
    @PostMapping(value = "searchUserIdProc")
    public MsgDTO searchUserIdProc(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".user/searchUserIdProc Start!");

        String nickname = CmmUtil.nvl(request.getParameter("nickname1")); // 이름
        String email = CmmUtil.nvl(request.getParameter("email1")); // 이메일

        int res = 0;

        log.info("nickname : " + nickname);
        log.info("email : " + email);

        UserInfoDTO pDTO = UserInfoDTO.builder().nickname(nickname).email(EncryptUtil.encAES128CBC(email)).build();


        Map<String, Object> rMap = Optional.ofNullable(userInfoService.searchUserIdOrPasswordProc(pDTO))
                .orElseGet(HashMap::new);

        if(rMap.get("userId") != "") {

            String title = "아이디 확인 이메일";
            String contents = rMap.get("nickname") + "님의 아이디는 " + rMap.get("userId") + "입니다";
            String toMail = email;

            MailDTO dto = MailDTO.builder().title(title).contents(contents).toMail(toMail).build();

            mailService.doSendMail(dto);
            res = 1;
            dto = null;

        }

        log.info(this.getClass().getName() + ".user/searchUserIdProc End!");

        MsgDTO dto = MsgDTO.builder().msg("/index").result(res).build();

        return dto;

    }

    /**
     * 비밀번호 찾기 로직 수행
     * <p>
     * 아이디, 이름, 이메일 일치하면, 비밀번호 재발급 화면 이동
     */
    @ResponseBody
    @PostMapping(value = "searchPasswordProc")
    public MsgDTO searchPasswordProc(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".user/searchPasswordProc Start!");

        String userId = CmmUtil.nvl(request.getParameter("id")); // 아이디
        String nickname = CmmUtil.nvl(request.getParameter("nickname2")); // 이름
        String email = CmmUtil.nvl(request.getParameter("email")); // 이메일

        int res = 0;

        log.info("userId : " + userId);
        log.info("nickname : " + nickname);
        log.info("email : " + email);

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).nickname(nickname).email(EncryptUtil.encAES128CBC(email)).build();

        // 비밀번호 찾기 가능한지 확인하기
        Map<String, Object> rMap = Optional.ofNullable(userInfoService.searchUserIdOrPasswordProc(pDTO)).orElseGet(HashMap::new);

        // 비밀번호 재생성하는 화면은 보안을 위해 반드시 NEW_PASSWORD 세션이 존재해야 접속 가능하도록 구현
        // userId 값을 넣은 이유는 비밀번호 재설정하는 newPasswordProc 함수에서 사용하기 위함
        session.setAttribute("NEW_PASSWORD", userId);

        if(rMap.get("userId") != "") {
            res = 1;
        }

        MsgDTO dto = MsgDTO.builder().msg("/user/newPassword").result(res).build();

        log.info(this.getClass().getName() + ".user/searchPasswordProc End!");

        return dto;
    }
    /**
     * 비밀번호 찾기 로직 수행
     * <p>
     * 아이디, 이름, 이메일 일치하면, 비밀번호 재발급 화면 이동
     */
    @ResponseBody
    @PostMapping(value = "newPasswordProc")
    public MsgDTO newPasswordProc(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".user/newPasswordProc Start!");

        String msg = ""; // 웹에 보여줄 메시지
        int res = 0;

        // 정상적인 접근인지 체크
        String newPassword = CmmUtil.nvl((String) session.getAttribute("NEW_PASSWORD"));

        if (newPassword.length() > 0) { //정상 접근

            String password = CmmUtil.nvl(request.getParameter("password")); // 신규 비밀번호

            log.info("password : " + password);

            UserInfoDTO pDTO = UserInfoDTO.builder().userId(newPassword).password(EncryptUtil.encHashSHA256(password)).build();

            userInfoService.newPasswordProc(pDTO);

            // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_PASSWORD 세션 삭제
            session.setAttribute("NEW_PASSWORD", "");
            session.removeAttribute("NEW_PASSWORD");

            session.setAttribute("SS_USER", "");
            session.removeAttribute("SS_USER");

            res = 1;
            msg = "비밀번호가 재설정되었습니다.";

        } else { // 비정상 접근
            msg = "비정상 접근입니다.";
        }


        log.info(this.getClass().getName() + ".user/newPasswordProc End!");

        return MsgDTO.builder().msg(msg).result(res).build();

    }


    @ResponseBody
    @PostMapping(value = "deleteUser")
    public MsgDTO deleteUser(HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".user/deleteUser Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));

        int res = 0;

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();


        int success = Optional.ofNullable(userInfoService.deleteUser(pDTO))
                .orElse(0);

        String msg ="";

        if(success != 0) {

            msg = "탈퇴되었습니다";

            res = 1;

            session.setAttribute("SS_USER", "");
            session.removeAttribute("SS_USER");

        }

        log.info(this.getClass().getName() + ".user/deleteUser End!");

        MsgDTO dto = MsgDTO.builder().msg(msg).result(res).build();

        return dto;

    }

    @GetMapping(value = "/userInfoUpdate")
    public String userInfoUpdate(HttpSession session, ModelMap model) throws Exception{
        log.info(this.getClass().getName() + ".userInfoUpdate start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));

        log.info(userId);

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        Map<String, Object> rMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO))
                .orElseGet(HashMap::new);

        UserInfoDTO rDTO = UserInfoDTO.builder(
        ).userId(String.valueOf(rMap.get("userId"))
        ).nickname(String.valueOf(rMap.get("nickname"))
        ).email(EncryptUtil.decAES128CBC(String.valueOf(rMap.get("email")))
        ).build();

        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".userInfoUpdate start!");
        return "/user/userInfoUpdate";
    }
    @ResponseBody
    @PostMapping(value = "loginOut")
    public MsgDTO loginProc(HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".loginOut Start!");

        String msg;

        session.setAttribute("SS_USER", "");
        session.removeAttribute("SS_USER");

        MsgDTO dto = MsgDTO.builder().result(1).msg("로그아웃하였습니다").build();

        log.info(this.getClass().getName() + ".loginOut End!");

        return dto;
    }



    @ResponseBody
    @PostMapping(value = "updateInfo")
    public MsgDTO updateInfo(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".updateInfo Start!");

        // 성공이면 1, 실패면 0
        int res = 0;
        String msg = "";

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String email = CmmUtil.nvl(request.getParameter("email"));
            String nickname = CmmUtil.nvl(request.getParameter("nickname"));


            log.info("userId : " + userId);
            log.info("email : " + email);
            log.info("nickname : " + nickname);

            UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).email(EncryptUtil.encAES128CBC(email)).nickname(nickname).build();

            res = userInfoService.updateUserInfo(pDTO);

            log.info("res : " + res);

            if (res == 1) {
                msg = "수정되었습니다";
            } else {
                msg = "오류로 인해 수정에 실패하였습니다";
            }
        } catch (Exception e) {
            msg = "실패하였습니다 : " + e;
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".updateInfo End!");
        }
        return MsgDTO.builder().msg(msg).result(res).build();
    }
}
