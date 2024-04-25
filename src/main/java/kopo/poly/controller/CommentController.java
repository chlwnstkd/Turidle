package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.PostDTO;
import kopo.poly.service.ICommentService;
import kopo.poly.service.ILikeService;
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

    // 댓글페이지 이동 및 조회 코드
    @GetMapping(value = "")
    public String getCommentList(ModelMap model,
                                 @RequestParam String postNumber,
                                 @RequestParam(name = "page", defaultValue = "1") int page) throws Exception {
        log.info(this.getClass().getName() + ".getCommentList start!");

        log.info("postNumber : " + postNumber);
        log.info("page : " + page);

        CommentDTO pDTO = CommentDTO.builder().postNumber(postNumber).build();

        List<Map<String, Object>> pList = Optional.ofNullable(commentService.getCommentList(pDTO)).orElseGet(ArrayList::new);

        List<CommentDTO> rList = new ArrayList<>();

        for (Map<String, Object> rMap : pList) {
            CommentDTO rDTO = CommentDTO.builder(
            ).commentNumber(String.valueOf(rMap.get("commentNumber"))
            ).userId(String.valueOf(rMap.get("nickname"))
            ).regDt(String.valueOf(rMap.get("regDt"))
            ).postNumber(String.valueOf(rMap.get("postNumber"))
            ).contents(String.valueOf(rMap.get("contents"))
            ).build();

            rList.add(rDTO);
        }

        /**페이징 시작 부분*/

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 5;

        // 페이지네이션을 위해 전체 아이템 개수 구하기
        int totalItems = rList.size();

        // 전체 페이지 개수 계산
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        // 현재 페이지에 해당하는 아이템들만 선택하여 rList에 할당
        int fromIndex = (page - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
        rList = rList.subList(fromIndex, toIndex);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);
        log.info("rList : " + rList.toString() );

        /**페이징 끝부분*/


        model.addAttribute("rList", rList);
        model.addAttribute("postNumber", postNumber);

        log.info(this.getClass().getName() + ".getCommentList End!");

        return "/post/comment";
    }

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
            String commentNumber = CmmUtil.nvl(request.getParameter("commentNumber"));

            log.info("userId : " + userId);
            log.info("commentNumber : " + commentNumber);

            CommentDTO pDTO = CommentDTO.builder().commentNumber(commentNumber).userId(userId).build();
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

            log.info("userId : " + userId);
            log.info("commentNumber : " + commentNumber);
            log.info("contents : " + contents);

            pDTO = CommentDTO.builder().userId(userId).commentNumber(commentNumber).contents(contents).build();

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
