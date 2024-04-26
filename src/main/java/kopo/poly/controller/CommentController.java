package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.PostDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.ICommentService;
import kopo.poly.service.ILikeService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "comment")
@Controller
public class CommentController {
    private final ICommentService  commentService;

    // 댓글 삭제로직 코드
    @ResponseBody
    @PostMapping(value = "/deleteComment")
    public MsgDTO deleteComment(HttpSession session, HttpServletRequest request) {
        log.info(this.getClass().getName() + ".commentDelete Start!");

        String msg = "";
        int res = 0;
        MsgDTO dto = null;

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));
            String commentNumber = CmmUtil.nvl(request.getParameter("commentNumber"));

            log.info("userId : " + userId);
            log.info("commentNumber : " + commentNumber);

            CommentDTO pDTO = CommentDTO.builder().commentNumber(commentNumber).userId(userId).postNumber(postNumber).build();
            commentService.deleteComment(pDTO);

            msg = "삭제되었습니다.";
            res = 1;
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".commentDelete End!");
        }

        return MsgDTO.builder().result(res).msg(msg).build();
    }

    @ResponseBody
    @PostMapping(value = "/insertComment")
    public MsgDTO insertComment(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".insertShopInfo Start!");

        // 성공이면 1, 실패면 0
        int res = 0;
        String msg = "";

        CommentDTO pDTO = null;

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String contents = CmmUtil.nvl(request.getParameter("comment"));
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

            log.info("userId : " + userId);
            log.info("postNumber : " + postNumber);
            log.info("contents : " + contents);
            
            pDTO = CommentDTO.builder().userId(userId).postNumber(postNumber).contents(contents).build();

            commentService.insertComment(pDTO);

            msg = "등록되었습니다";
            res = 1;

        } catch (Exception e) {
            msg = "실패하였습니다 : " + e;
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".insertComment End!");
        }
        return MsgDTO.builder().msg(msg).result(res).build();
    }


    @ResponseBody
    @PostMapping(value = "/updateComment")
    public MsgDTO updateComment(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".updateComment Start!");

        // 성공이면 1, 실패면 0
        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        CommentDTO pDTO = null;

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String contents = CmmUtil.nvl(request.getParameter("contents"));
            String commentNumber = CmmUtil.nvl(request.getParameter("commentNumber"));
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));


            log.info("userId : " + userId);
            log.info("commentNumber : " + commentNumber);
            log.info("contents : " + contents);

            pDTO = CommentDTO.builder().userId(userId).commentNumber(commentNumber).contents(contents).postNumber(postNumber).build();

            commentService.updateComment(pDTO);

            msg = "수정되었습니다";
            res = 1;

        } catch (Exception e) {
            msg = "실패하였습니다 : " + e;
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".updateComment End!");
        }
        return MsgDTO.builder().msg(msg).result(res).build();
    }
}
