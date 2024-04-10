package kopo.poly.controller;

import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.PostDTO;
import kopo.poly.service.IFileService;
import kopo.poly.service.IPostService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@RequestMapping(value = "/post")
//특정 메소드와 매핑하기 위해 사용, 구형이다
@RequiredArgsConstructor
@Controller
public class PostController {
    private final IPostService postService;
    private final IFileService fileService;

    //GET 방식은 데이터 조회, POST 방식에서 새로운 데이터 추가.

    // 게시글 목록 조회 및 페이지 이동 코드
    // 구현완료(11/13)
    @GetMapping(value = "/postList")
    public String postList(ModelMap model, @RequestParam(defaultValue = "1") int page, HttpServletRequest request)
            throws Exception {
        log.info(this.getClass().getName() + ".postList Start!");


        String type = CmmUtil.nvl(request.getParameter("type"));

        List<PostDTO> rList = postService.getPostList(type);
        if (rList == null) rList = new ArrayList<>();

        log.info(rList.toString());
        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 3;

        // 페이지네이션을 위해 전체 아이템 개수 구하기
        int totalItems = rList.size();

        // 전체 페이지 개수 계산
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        // 현재 페이지에 해당하는 아이템들만 선택하여 rList에 할당
        int fromIndex = (page - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
        rList = rList.subList(fromIndex, toIndex);

        model.addAttribute("rList", rList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);

        log.info(this.getClass().getName() + ".postList End!");
        if(type.equals("notice")) {
            return "/post/noticeList";
        }
        if(type.equals("verification")) {
            return "/post/verificationPostList";
        }
        return "/post/postList";
    }

    // 게시글 등록페이지 이동코드
    // 구현완료(11/10)
    @GetMapping(value = "/postReg")
    public String PostReg() {
        log.info(this.getClass().getName() + ".postReg Start!");
        log.info(this.getClass().getName() + ".postReg End!");

        return "/post/postReg";
    }

    // 게시글 등록 로직코드
    // 구현완료(11/10)
    @ResponseBody
    @PostMapping(value = "/postInsert")
    public MsgDTO postInsert(HttpServletRequest request, HttpSession session, @RequestParam(value = "fileUpload") MultipartFile mf) {
        log.info(this.getClass().getName() + ".postInsert Start!");
        String msg = "";
        int res = 0;
        MsgDTO dto = null;

        try {
            String customer_id = CmmUtil.nvl((String) session.getAttribute("SS_ID"));
            String title = CmmUtil.nvl(request.getParameter("title"));
            String contents = CmmUtil.nvl(request.getParameter("contents"));
            String type = "Post";
            log.info("session customer_id : " + customer_id);
            log.info("title : " + title);
            log.info("contents : " + contents);

            PostDTO pDTO = new PostDTO();
            pDTO.setCustomerId(customer_id);
            pDTO.setTitle(title);
            pDTO.setContents(contents);
            if (!mf.isEmpty()) {
                String image = mf.getOriginalFilename();
                String fileName = image;
                String folderName = type + "/" + LocalDate.now() + "/";
                fileService.upload(fileName, folderName, mf);
                pDTO.setImage(fileService.getFileURL(folderName, fileName));
                log.info(pDTO.getImage());
            }
            postService.insertPostInfo(pDTO);
            msg = "등록되었습니다.";
            res = 1;


        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            dto = new MsgDTO();
            dto.setMsg(msg);
            dto.setResult(res);
            log.info(dto.toString());
            log.info(this.getClass().getName() + ".postInsert End!");
        }

        return dto;
    }

    // 게시글 상세보기 코드
    // 구현완료(11/13)
    @GetMapping(value = "/postInfo")
    public String postInfo(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".postInfo Start!");
        String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

        log.info("postNumber : " + postNumber);

        PostDTO pDTO = new PostDTO();
        pDTO.setPostNumber(postNumber);

        PostDTO rDTO = Optional.ofNullable(postService.getPostInfo(pDTO))
                .orElseGet(PostDTO::new);

        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".postInfo End!");

        return "/post/postInfo";
    }

    // 게시글 수정페이지 이동코드
    // 구현완료(11/13)
    @GetMapping(value = "/postEditInfo")
    public String postEditInfo(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".postEditInfo Start!");

        String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

        log.info("postNumber : " + postNumber);

        PostDTO pDTO = new PostDTO();
        pDTO.setPostNumber(postNumber);

        PostDTO rDTO = Optional.ofNullable(postService.getPostInfo(pDTO)).orElseGet(PostDTO::new);

        log.info(rDTO.toString());
        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".postEditInfo End!");

        return "/post/postEditInfo";
    }

    // 게시글 수정로직 코드
    // 구현완료(11/13)
    @ResponseBody
    @PostMapping(value = "/postUpdate")
    public MsgDTO postUpdate(HttpSession session, HttpServletRequest request, @RequestParam(value = "fileUpload") MultipartFile mf) {

        log.info(this.getClass().getName() + ".postUpdate Start!");

        String msg = "";
        int res = 1;
        MsgDTO dto = null;

        try {
            String customer_id = CmmUtil.nvl((String) session.getAttribute("SS_CUSTOMER_ID"));
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));
            String title = CmmUtil.nvl(request.getParameter("title"));
            String contents = CmmUtil.nvl(request.getParameter("contents"));

            log.info("customer_id : " + customer_id);
            log.info("postNumber : " + postNumber);
            log.info("title : " + title);
            log.info("contents : " + contents);

            PostDTO pDTO = new PostDTO();
            pDTO.setCustomerId(customer_id);
            pDTO.setPostNumber(postNumber);
            pDTO.setTitle(title);
            pDTO.setContents(contents);
            pDTO.setImage("");
            if (!mf.isEmpty()) {
                String image = mf.getOriginalFilename();
                String fileName = image;
                String folderName = "Post" + "/" + pDTO.getPostNumber() + "/";
                fileService.upload(fileName, folderName, mf);
                pDTO.setImage(fileService.getFileURL(folderName, fileName));
                log.info(pDTO.getImage());
            }

            postService.updatePostInfo(pDTO);

            msg = "수정되었습니다.";
            res = 1;
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            dto = new MsgDTO();
            dto.setMsg(msg);
            dto.setResult(res);
            log.info(this.getClass().getName() + ".postInsert End!");
        }

        return dto;
    }

    // 게시글 삭제로직 코드
    // 구현완료(11/13)
    @ResponseBody
    @PostMapping(value = "/postDelete")
    public MsgDTO postDelete(HttpSession session, ModelMap model, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".postDelete Start!");

        String msg = "";
        int res = 0;
        MsgDTO dto = null;

        try {
            String customer_id = CmmUtil.nvl((String) session.getAttribute("SS_ID"));
            String postNumber = CmmUtil.nvl(request.getParameter("postNumber"));

            log.info("customer_id : " + customer_id);
            log.info("postNumber : " + postNumber);

            PostDTO pDTO = new PostDTO();
            pDTO.setCustomerId(customer_id);
            pDTO.setPostNumber(postNumber);
            postService.deletePostInfo(pDTO);

            msg = "삭제되었습니다.";
            res = 1;
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);
            log.info(this.getClass().getName() + ".postInsert End!");
        }

        return dto;
    }
}

