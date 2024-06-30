package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.LikeDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.service.ILikeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/like")
public class LikeController {

    private final ILikeService likeService;

    /**
     * 게시글 좋아요 업데이트
     *
     * @param session  HTTP 세션 객체
     * @param request  HTTP 요청 객체
     * @return MsgDTO  결과 메시지와 상태 코드
     */
    @ResponseBody
    @PostMapping(value = "/update")
    public MsgDTO postLike(HttpSession session, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".update Start!");

        String msg = "";
        int res = 0;

        try {
            // 세션에서 사용자 아이디 가져오기
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            // 요청 파라미터에서 게시글 번호 가져오기
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

            log.info("userId : " + userId);
            log.info("postNumber : " + postNumber);

            // LikeDTO 객체 생성
            LikeDTO pDTO = LikeDTO.builder().userId(userId).postNumber(postNumber).build();

            if (userId.equals("")) {
                msg = "로그인 후 이용 가능합니다";
                res = 2;
            } else {
                // 사용자가 이미 좋아요를 눌렀는지 확인
                if (likeService.getLike(pDTO) == 1) {
                    // 이미 좋아요를 눌렀으면 좋아요 취소
                    likeService.deleteLike(pDTO);
                    msg = "좋아요를 취소하였습니다";
                    res = 1;
                } else {
                    // 좋아요 추가
                    likeService.insertLike(pDTO);
                    msg = "좋아요가 추가되었습니다";
                    res = 1;
                }
            }
        } catch (Exception e) {
            // 예외 처리
            msg = "실패하였습니다: " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".update End!");
        }

        // 결과 메시지와 상태 코드를 담은 MsgDTO 반환
        return MsgDTO.builder().msg(msg).result(res).build();
    }
}
