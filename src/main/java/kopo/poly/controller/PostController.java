package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.*;
import kopo.poly.service.ICommentService;
import kopo.poly.service.ILikeService;
import kopo.poly.service.IPostService;
import kopo.poly.service.IUserInfoService;
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
@RequestMapping(value = "/post")
//특정 메소드와 매핑하기 위해 사용, 구형이다
@RequiredArgsConstructor
@Controller
public class PostController {
    private final IPostService postService;
    private final ILikeService likeService;
    private  final ICommentService commentService;
    private final IUserInfoService userInfoService;


    //GET 방식은 데이터 조회, POST 방식에서 새로운 데이터 추가.

    // 게시글 목록 조회 및 페이지 이동 코드
    // 구현완료(11/13)
    @GetMapping(value = "/postList")
    public String postList(ModelMap model, @RequestParam(defaultValue = "1") int page)
            throws Exception {
        log.info(this.getClass().getName() + ".postList Start!");

        int from = (page-1) * 10 + 1;

        int to = page * 10;

        PostDTO pDTO = PostDTO.builder().from(from).to(to).build();

        List<Map<String, Object>> pList = postService.getPostList(pDTO);
        if (pList == null) pList = new ArrayList<>();

        log.info(pList.toString());

        List<PostDTO> rList = new ArrayList<>();

        for (Map<String, Object> rMap : pList) {
            PostDTO rDTO = PostDTO.builder().postNumber(String.valueOf(rMap.get("postNumber"))
            ).readCount(String.valueOf(rMap.get("readCount"))
            ).regDt(String.valueOf(rMap.get("regDt"))
            ).regId(String.valueOf(rMap.get("nickname"))
            ).title(String.valueOf(rMap.get("title"))
            ).build();

            rList.add(rDTO);
        }

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 10;

        int totalPages = (int) Math.ceil((double) Optional.ofNullable(postService.getPostCount()).orElse(0) / itemsPerPage);

        model.addAttribute("rList", rList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);

        log.info(this.getClass().getName() + ".postList End!");

        return "post/postList";
    }

    // 게시글 등록페이지 이동코드
    // 구현완료(11/10)
    @GetMapping(value = "/postReg")
    public String PostReg() {
        log.info(this.getClass().getName() + ".postReg Start!");
        log.info(this.getClass().getName() + ".postReg End!");

        return "post/postReg";
    }

    // 게시글 등록 로직코드
    @ResponseBody
    @PostMapping(value = "/postInsert")
    public MsgDTO postInsert(HttpServletRequest request, HttpSession session) {
        log.info(this.getClass().getName() + ".postInsert Start!");
        String msg = "";
        int res = 0;

        try {
            String regId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String title = CmmUtil.nvl(request.getParameter("title"));
            String contents = CmmUtil.nvl(request.getParameter("contents"));

            log.info("regId : " + regId);
            log.info("title : " + title);
            log.info("contents : " + contents);

            PostDTO pDTO = PostDTO.builder().regId(regId).title(title).contents(contents).build();

            postService.insertPostInfo(pDTO);
            msg = "등록되었습니다.";
            res = 1;


        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {

            log.info(this.getClass().getName() + ".postInsert End!");
        }

        return  MsgDTO.builder().msg(msg).result(res).build();
    }

    // 게시글 상세보기 코드                                                                                                                                                                                                              내 이름은 준 상 초이! 탐정이죠
    // 구현완료(11/13)
    @GetMapping(value = "/postInfo")
    public String postInfo(HttpServletRequest request, ModelMap model,  @RequestParam(name = "page", defaultValue = "1") int page) throws Exception {
        log.info(this.getClass().getName() + ".postInfo Start!");
        String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

        log.info("postNumber : " + postNumber);

        PostDTO pDTO = PostDTO.builder().postNumber(postNumber).build();

        LikeDTO lDTO = LikeDTO.builder().postNumber(postNumber).build();

        CommentDTO cDTO = CommentDTO.builder().postNumber(postNumber).build();

        int likeCnt = likeService.getLikeCount(lDTO);
        int commentCnt = commentService.getCommentCount(cDTO);

        PostDTO rDTO = Optional.ofNullable(postService.getPostInfo(pDTO))
                .orElseGet(() -> PostDTO.builder().build());

        log.info(rDTO.toString());

        model.addAttribute("rDTO", rDTO);
        model.addAttribute("likeCnt", likeCnt);
        model.addAttribute("commentCnt", commentCnt);

        log.info("postNumber : " + postNumber);
        log.info("page : " + page);

        CommentDTO pDTO2 = CommentDTO.builder().postNumber(postNumber).build();

        List<Map<String, Object>> pList = Optional.ofNullable(commentService.getCommentList(pDTO2)).orElseGet(ArrayList::new);

        List<CommentDTO> rList = new ArrayList<>();

        for (Map<String, Object> rMap : pList) {

            UserInfoDTO pDTO1 = UserInfoDTO.builder().userId(String.valueOf(rMap.get("userId"))).build();

            Map<String, Object> pMap = Optional.ofNullable(userInfoService.getUserInfo(pDTO1))
                    .orElseGet(HashMap::new);

            CommentDTO DTO = CommentDTO.builder(
            ).commentNumber(String.valueOf(rMap.get("commentNumber"))
            ).nickname(String.valueOf(pMap.get("nickname"))
            ).userId(String.valueOf(rMap.get("userId"))
            ).regDt(String.valueOf(rMap.get("regDt"))
            ).postNumber(String.valueOf(rMap.get("postNumber"))
            ).contents(String.valueOf(rMap.get("contents"))
            ).build();

            rList.add(DTO);
        }

        /**페이징 시작 부분*/

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 4;

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

        log.info(this.getClass().getName() + ".postInfo End!");

        return "post/postInfo";
    }

    // 게시글 수정페이지 이동코드
    // 구현완료(11/13)
    @GetMapping(value = "/postEditInfo")
    public String postEditInfo(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".postEditInfo Start!");

        String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

        log.info("postNumber : " + postNumber);

        PostDTO pDTO = PostDTO.builder().postNumber(postNumber).build();

        PostDTO rDTO = Optional.ofNullable(postService.getPostInfo(pDTO)).orElseGet(() -> PostDTO.builder().build());

        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".postEditInfo End!");

        return "post/postEditInfo";
    }

    // 게시글 수정로직 코드
    // 구현완료(11/13)
    @ResponseBody
    @PostMapping(value = "/postUpdate")
    public MsgDTO postUpdate(HttpSession session, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".postUpdate Start!");

        String msg = "";
        int res = 0;

        try {
            String regId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));
            String title = CmmUtil.nvl(request.getParameter("title"));
            String contents = CmmUtil.nvl(request.getParameter("contents"));

            log.info("regId : " + regId);
            log.info("postNumber : " + postNumber);
            log.info("title : " + title);
            log.info("contents : " + contents);

            PostDTO pDTO = PostDTO.builder().regId(regId).postNumber(postNumber).title(title).contents(contents).build();

            postService.updatePostInfo(pDTO);

            msg = "수정되었습니다.";
            res = 1;
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".postInsert End!");
        }

        return MsgDTO.builder().msg(msg).result(res).build();
    }

    // 게시글 삭제로직 코드
    // 구현완료(11/13)
    @ResponseBody
    @PostMapping(value = "/postDelete")
    public MsgDTO postDelete(HttpSession session, ModelMap model, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".postDelete Start!");

        String msg = "";
        int res = 0;

        try {
            String regId = CmmUtil.nvl((String) session.getAttribute("SS_USER"));
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

            log.info("regId : " + regId);
            log.info("postNumber : " + postNumber);

            PostDTO pDTO = PostDTO.builder().regId(regId).postNumber(postNumber).build();

            postService.deletePostInfo(pDTO);

            msg = "삭제되었습니다.";
            res = 1;
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".postInsert End!");
        }

        return MsgDTO.builder().msg(msg).result(res).build();
    }
}

