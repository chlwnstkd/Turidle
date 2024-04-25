package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.LikeDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.PostDTO;
import kopo.poly.service.ILikeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping(value = "/like")
@Slf4j
@Controller
@RequiredArgsConstructor
public class LikeController {

    private final ILikeService likeService;

    // 게시글 삭제로직 코드
    // 구현완료(11/13)
    @ResponseBody
    @PostMapping(value = "/update")
    public MsgDTO postLike(HttpSession session, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".update Start!");

        String msg = "";
        int res = 0;

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

            log.info("userId : " + userId);
            log.info("postNumber : " + postNumber);

            LikeDTO pDTO = LikeDTO.builder().userId(userId).postNumber(postNumber).build();

            if(likeService.getLike(pDTO) == 1) {
                likeService.deleteLike(pDTO);
                msg = "좋아요를 취소히였습니다";
                res = 1;
            } else {
                likeService.insertLike(pDTO);
                msg = "좋아요를 추가되었습니다";
                res = 1;
            }

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".update End!");
        }

        return MsgDTO.builder().msg(msg).result(res).build();
    }
}