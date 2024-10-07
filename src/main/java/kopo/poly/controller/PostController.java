package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.*;
import kopo.poly.service.*;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.*;

@Slf4j
// "/post" 경로에 매핑되는 컨트롤러입니다.
@RequestMapping(value = "/post")
// 롬복을 사용하여 생성자 주입을 위한 어노테이션입니다.
@RequiredArgsConstructor
// 스프링 MVC에서 컨트롤러로 사용됩니다.
@Controller
public class PostController {

    // 필요한 서비스들을 주입받습니다.
    private final IPostService postService;
    private final ILikeService likeService;
    private  final ICommentService commentService;
    private final IUserInfoService userInfoService;
    private final IHideService hideService;

    // GET 방식으로 게시글 목록을 조회하고 페이지 이동을 처리하는 메소드입니다.
    // 페이지 파라미터를 받아와서 해당 페이지의 게시글 목록을 조회하여 모델에 담아 postList.html을 반환합니다.
    @GetMapping(value = "/postList")
    public String postList(ModelMap model, @RequestParam(defaultValue = "1") int page)
            throws Exception {
        log.info(this.getClass().getName() + ".postList 시작!");

        // 페이지당 게시글 개수와 시작 인덱스를 계산합니다.
        int from = (page-1) * 10 + 1;
        int to = page * 10;

        // 조회할 게시글 범위를 지정하는 PostDTO 객체를 생성합니다.
        PostDTO pDTO = PostDTO.builder().from(from).to(to).build();

        // 게시글 목록을 조회합니다.
        List<Map<String, Object>> pList = postService.getPostList(pDTO);
        if (pList == null) pList = new ArrayList<>();

        log.info(pList.toString());

        // 조회 결과를 담을 리스트를 초기화합니다.
        List<PostDTO> rList = new ArrayList<>();

        // 조회된 맵을 PostDTO로 변환하여 리스트에 추가합니다.
        for (Map<String, Object> rMap : pList) {
            PostDTO rDTO = PostDTO.builder().postNumber(String.valueOf(rMap.get("postNumber")))
                    .readCount(String.valueOf(rMap.get("readCount")))
                    .regDt(String.valueOf(rMap.get("regDt")))
                    .regId(String.valueOf(rMap.get("nickname")))
                    .title(String.valueOf(rMap.get("title")))
                    .build();

            rList.add(rDTO);
        }

        // 페이지당 보여줄 아이템 개수를 정의합니다.
        int itemsPerPage = 10;

        // 전체 페이지 수를 계산합니다.
        int totalPages = (int) Math.ceil((double) Optional.ofNullable(postService.getPostCount()).orElse(0) / itemsPerPage);

        // 모델에 결과를 담아 post/postList.html을 반환합니다.
        model.addAttribute("rList", rList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);

        log.info(this.getClass().getName() + ".postList 끝!");

        return "post/postList";
    }

    // 게시글 등록 페이지로 이동하는 메소드입니다.
    @GetMapping(value = "/postReg")
    public String PostReg() {
        log.info(this.getClass().getName() + ".postReg 시작!");
        log.info(this.getClass().getName() + ".postReg 끝!");

        return "post/postReg";
    }

    // 게시글을 등록하는 메소드입니다.
    // HTTP 요청을 받아 게시글 등록에 성공하면 성공 메시지를 반환합니다.
    @ResponseBody
    @PostMapping(value = "/postInsert")
    public MsgDTO postInsert(HttpServletRequest request, HttpSession session) {
        log.info(this.getClass().getName() + ".postInsert 시작!");
        String msg = "";
        int res = 0;

        try {
            // 세션에서 사용자 정보를 가져옵니다.
            String regId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            // HTTP 요청 파라미터에서 제목과 내용을 가져옵니다.
            String title = CmmUtil.nvl(request.getParameter("title"));
            String contents = CmmUtil.nvl(request.getParameter("contents"));

            log.info("regId : " + regId);
            log.info("title : " + title);
            log.info("contents : " + contents);

            // 게시글 정보를 PostDTO 객체로 생성합니다.
            PostDTO pDTO = PostDTO.builder().regId(regId).title(title).contents(contents).build();

            // 게시글 등록 서비스를 호출하여 등록합니다.
            postService.insertPostInfo(pDTO);
            msg = "등록되었습니다.";
            res = 1;

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".postInsert 끝!");
        }

        // 결과를 담은 MsgDTO를 반환합니다.
        return  MsgDTO.builder().msg(msg).result(res).build();
    }

    // 게시글 상세보기 메소드입니다.
    // 게시글 번호와 페이지를 받아와 상세 정보와 댓글을 조회하여 postInfo.html을 반환합니다.
    @GetMapping(value = "/postInfo")
    public String postInfo(HttpServletRequest request, ModelMap model, HttpSession session,  @RequestParam(name = "page", defaultValue = "1") int page) throws Exception {
        log.info(this.getClass().getName() + ".postInfo 시작!");
        String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

        log.info("postNumber : " + postNumber);

        // 조회할 게시글 번호를 가지고 있는 PostDTO 객체를 생성합니다.
        PostDTO pDTO = PostDTO.builder().postNumber(postNumber).build();

        // 조회할 좋아요 수를 가지고 있는 LikeDTO 객체를 생성합니다.
        LikeDTO lDTO = LikeDTO.builder().postNumber(postNumber).build();

        // 조회할 댓글 수를 가지고 있는 CommentDTO 객체를 생성합니다.
        CommentDTO cDTO = CommentDTO.builder().postNumber(postNumber).build();

        // 좋아요 수와 댓글 수를 조회합니다.
        int likeCnt = likeService.getLikeCount(lDTO);
        int commentCnt = commentService.getCommentCount(cDTO);

        // 조회할 게시글 정보를 조회하여 PostDTO 객체로 받습니다.
        PostDTO rDTO = Optional.ofNullable(postService.getPostInfo(pDTO))
                .orElseGet(() -> PostDTO.builder().build());

        log.info(rDTO.toString());

        // 모델에 조회 결과와 좋아요 수, 댓글 수를 담습니다.
        model.addAttribute("rDTO", rDTO);
        model.addAttribute("likeCnt", likeCnt);
        model.addAttribute("commentCnt", commentCnt);

        log.info("postNumber : " + postNumber);
        log.info("page : " + page);

        // 조회할 댓글 목록을 가져오기 위한 CommentDTO 객체를 생성합니다.
        CommentDTO pDTO2 = CommentDTO.builder().postNumber(postNumber).build();

        // 댓글 목록을 조회합니다.
        List<Map<String, Object>> pList = Optional.ofNullable(commentService.getCommentList(pDTO2)).orElseGet(ArrayList::new);

        // 조회된 댓글 정보를 담을 리스트를 생성합니다.
        List<CommentDTO> rList = new ArrayList<>();

        // 조회된 댓글 정보를 CommentDTO 객체로 변환하여 리스트에 추가합니다.
        for (Map<String, Object> rMap : pList) {

            // 댓글 작성자 정보를 가져오기 위한 UserInfoDTO 객체를 생성합니다.
            UserInfoDTO pDTO1 = UserInfoDTO.builder().userId(String.valueOf(rMap.get("userId"))).build();

            // 댓글 작성자 정보를 조회합니다.
            Map<String, Object> pMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO1))
                    .orElseGet(HashMap::new);

            String hideId = CmmUtil.nvl((String) rMap.get("userId"));
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));

            log.info("hideId : " + hideId);
            log.info("userId : " + userId);

            // 댓글 삭제를 위한 DTO 생성
            HideDTO pDTO3 = HideDTO.builder().userId(userId).hideId(hideId).build();


            int res = hideService.getHide(pDTO3);
            String contents = CmmUtil.nvl(String.valueOf(rMap.get("contents")));
            String nickname = CmmUtil.nvl((String) pMap.get("nickname"));

            log.info("res : " + res);

            if (res != 0) {
                nickname = "숨긴 유저";
                contents = "숨긴 유저의 댓글입니다";
            }

            // 댓글 정보를 CommentDTO 객체로 생성합니다.
            CommentDTO DTO = CommentDTO.builder(
                    ).commentNumber(String.valueOf(rMap.get("commentNumber")))
                    .nickname(nickname)
                    .userId(hideId)
                    .regDt(String.valueOf(rMap.get("regDt")))
                    .postNumber(String.valueOf(rMap.get("postNumber")))
                    .contents(contents)
                    .build();

            rList.add(DTO);
        }

        /**페이징 시작 부분*/

        // 페이지당 보여줄 아이템 개수를 정의합니다.
        int itemsPerPage = 4;

        // 전체 아이템 개수를 구합니다.
        int totalItems = rList.size();

        // 전체 페이지 수를 계산합니다.
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        // 현재 페이지에 해당하는 아이템만 선택하여 rList에 할당합니다.
        int fromIndex = (page - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
        rList = rList.subList(fromIndex, toIndex);

        // 모델에 페이지 정보와 댓글 목록을 담습니다.
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);
        log.info("rList : " + rList.toString() );

        /**페이징 끝부분*/

        // 모델에 댓글 목록과 게시글 번호를 담아 post/postInfo.html을 반환합니다.
        model.addAttribute("rList", rList);
        model.addAttribute("postNumber", postNumber);

        log.info(this.getClass().getName() + ".postInfo 끝!");

        return "post/postInfo";
    }

    // 게시글 수정 페이지로 이동하는 메소드입니다.
    @GetMapping(value = "/postEditInfo")
    public String postEditInfo(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".postEditInfo 시작!");

        // 수정할 게시글 번호를 파라미터에서 가져옵니다.
        String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

        log.info("postNumber : " + postNumber);

        // 조회할 게시글 번호를 가지고 있는 PostDTO 객체를 생성합니다.
        PostDTO pDTO = PostDTO.builder().postNumber(postNumber).build();

        // 게시글 정보를 조회하여 모델에 담습니다.
        PostDTO rDTO = Optional.ofNullable(postService.getPostInfo(pDTO)).orElseGet(() -> PostDTO.builder().build());

        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".postEditInfo 끝!");

        return "post/postEditInfo";
    }

    // 게시글 수정 로직을 처리하는 메소드입니다.
    // HTTP 요청을 받아 게시글 수정에 성공하면 성공 메시지를 반환합니다.
    @ResponseBody
    @PostMapping(value = "/postUpdate")
    public MsgDTO postUpdate(HttpSession session, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".postUpdate 시작!");

        String msg = "";
        int res = 0;

        try {
            // 세션에서 사용자 정보를 가져옵니다.
            String regId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            // HTTP 요청 파라미터에서 게시글 번호, 제목, 내용을 가져옵니다.
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));
            String title = CmmUtil.nvl(request.getParameter("title"));
            String contents = CmmUtil.nvl(request.getParameter("contents"));

            log.info("regId : " + regId);
            log.info("postNumber : " + postNumber);
            log.info("title : " + title);
            log.info("contents : " + contents);

            // 수정할 게시글 정보를 PostDTO 객체로 생성합니다.
            PostDTO pDTO = PostDTO.builder().regId(regId).postNumber(postNumber).title(title).contents(contents).build();

            // 게시글 수정 서비스를 호출하여 수정합니다.
            postService.updatePostInfo(pDTO);

            msg = "수정되었습니다.";
            res = 1;
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".postInsert 끝!");
        }

        // 결과를 담은 MsgDTO를 반환합니다.
        return MsgDTO.builder().msg(msg).result(res).build();
    }

    // 게시글 삭제 로직을 처리하는 메소드입니다.
    // HTTP 요청을 받아 게시글 삭제에 성공하면 성공 메시지를 반환합니다.
    @ResponseBody
    @PostMapping(value = "/postDelete")
    public MsgDTO postDelete(HttpSession session, ModelMap model, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".postDelete 시작!");

        String msg = "";
        int res = 0;

        try {
            // 세션에서 사용자 정보를 가져옵니다.
            String regId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            // HTTP 요청 파라미터에서 게시글 번호를 가져옵니다.
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

            log.info("regId : " + regId);
            log.info("postNumber : " + postNumber);

            // 삭제할 게시글 정보를 PostDTO 객체로 생성합니다.
            PostDTO pDTO = PostDTO.builder().regId(regId).postNumber(postNumber).build();

            // 게시글 삭제 서비스를 호출하여 삭제합니다.
            postService.deletePostInfo(pDTO);

            msg = "삭제되었습니다.";
            res = 1;
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".postInsert 끝!");
        }

        // 결과를 담은 MsgDTO를 반환합니다.
        return MsgDTO.builder().msg(msg).result(res).build();
    }
}
