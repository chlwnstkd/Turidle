package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.CommentDTO;
import kopo.poly.service.ICommentService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "comment")
@Controller
public class CommentController {

    private final ICommentService commentService;

    /**
     * 댓글 삭제 로직
     * @param session HTTP 세션 객체
     * @param request HTTP 요청 객체
     * @return 삭제 결과 메시지를 포함한 MsgDTO 객체
     */
    @ResponseBody
    @PostMapping(value = "/deleteComment")
    public MsgDTO deleteComment(HttpSession session, HttpServletRequest request) {
        log.info(this.getClass().getName() + ".commentDelete Start!");

        String msg = "";
        int res = 0;

        try {
            // 세션에서 사용자 아이디와 요청 파라미터에서 게시글 번호, 댓글 번호를 가져옴
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));
            String commentNumber = CmmUtil.nvl(request.getParameter("commentNumber"));

            log.info("userId : " + userId);
            log.info("commentNumber : " + commentNumber);

            // 댓글 삭제를 위한 DTO 생성
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

        // 삭제 결과 메시지와 상태를 반환
        return MsgDTO.builder().result(res).msg(msg).build();
    }

    /**
     * 댓글 등록 로직
     * @param request HTTP 요청 객체
     * @param session HTTP 세션 객체
     * @return 등록 결과 메시지를 포함한 MsgDTO 객체
     * @throws Exception 예외 처리
     */
    @ResponseBody
    @PostMapping(value = "/insertComment")
    public MsgDTO insertComment(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".insertComment Start!");

        int res = 0;
        String msg = "";

        CommentDTO pDTO = null;

        try {
            // 세션에서 사용자 아이디와 요청 파라미터에서 댓글 내용, 게시글 번호를 가져옴
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String contents = CmmUtil.nvl(request.getParameter("comment"));
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

            log.info("userId : " + userId);
            log.info("postNumber : " + postNumber);
            log.info("contents : " + contents);

            // 댓글 등록을 위한 DTO 생성
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

        // 등록 결과 메시지와 상태를 반환
        return MsgDTO.builder().msg(msg).result(res).build();
    }

    /**
     * 댓글 수정 로직
     * @param request HTTP 요청 객체
     * @param session HTTP 세션 객체
     * @return 수정 결과 메시지를 포함한 MsgDTO 객체
     * @throws Exception 예외 처리
     */
    @ResponseBody
    @PostMapping(value = "/updateComment")
    public MsgDTO updateComment(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".updateComment Start!");

        int res = 0;
        String msg = "";

        CommentDTO pDTO = null;

        try {
            // 세션에서 사용자 아이디와 요청 파라미터에서 댓글 내용, 댓글 번호, 게시글 번호를 가져옴
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String contents = CmmUtil.nvl(request.getParameter("contents"));
            String commentNumber = CmmUtil.nvl(request.getParameter("commentNumber"));
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

            log.info("userId : " + userId);
            log.info("commentNumber : " + commentNumber);
            log.info("contents : " + contents);

            // 댓글 수정을 위한 DTO 생성
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

        // 수정 결과 메시지와 상태를 반환
        return MsgDTO.builder().msg(msg).result(res).build();
    }

    /**
     * 유저 신고 로직
     * @param session HTTP 세션 객체
     * @param request HTTP 요청 객체
     * @return 삭제 결과 메시지를 포함한 MsgDTO 객체
     */
    @ResponseBody
    @PostMapping(value = "/reportUser")
    public MsgDTO reportUser(HttpSession session, HttpServletRequest request) {
        log.info(this.getClass().getName() + ".reportUser Start!");

        String msg = "";
        int res = 0;

        try {
            // 세션에서 사용자 아이디와 요청 파라미터에서 게시글 번호, 댓글 번호를 가져옴
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));
            String commentNumber = CmmUtil.nvl(request.getParameter("commentNumber"));

            log.info("userId : " + userId);
            log.info("commentNumber : " + commentNumber);

            // 댓글 삭제를 위한 DTO 생성
            CommentDTO pDTO = CommentDTO.builder().commentNumber(commentNumber).userId(userId).postNumber(postNumber).build();
            commentService.deleteComment(pDTO);

            msg = "신고하였습니다.";
            res = 1;
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".reportUser End!");
        }

        // 삭제 결과 메시지와 상태를 반환
        return MsgDTO.builder().result(res).msg(msg).build();
    }

    /**
     * 유저 숨김 로직
     * @param session HTTP 세션 객체
     * @param request HTTP 요청 객체
     * @return 삭제 결과 메시지를 포함한 MsgDTO 객체
     */
    @ResponseBody
    @PostMapping(value = "/hideUser")
    public MsgDTO hideUser(HttpSession session, HttpServletRequest request) {
        log.info(this.getClass().getName() + ".hideUser Start!");

        String msg = "";
        int res = 0;

        try {
            // 세션에서 사용자 아이디와 요청 파라미터에서 게시글 번호, 댓글 번호를 가져옴
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));
            String commentNumber = CmmUtil.nvl(request.getParameter("commentNumber"));

            log.info("userId : " + userId);
            log.info("commentNumber : " + commentNumber);

            // 댓글 삭제를 위한 DTO 생성
            CommentDTO pDTO = CommentDTO.builder().commentNumber(commentNumber).userId(userId).postNumber(postNumber).build();
            commentService.deleteComment(pDTO);

            msg = "숨김/해제 되었습니다.";
            res = 1;
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".hideUser End!");
        }

        // 삭제 결과 메시지와 상태를 반환
        return MsgDTO.builder().result(res).msg(msg).build();
    }
}
