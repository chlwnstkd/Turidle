package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.service.IClovaService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/clova")
@RequiredArgsConstructor
public class ClovaController {

    private final IClovaService clovaService;

    /**
     * 클로바 서비스에 프롬프트를 전달하고 결과를 반환하는 메서드
     * @param request HTTP 요청 객체
     * @return 클로바 서비스의 응답 결과
     * @throws Exception 예외 처리
     */
    @PostMapping("/prompt")
    public String prompt(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".prompt Start!");

        // 요청 파라미터에서 내용 가져오기
        String content = CmmUtil.nvl(request.getParameter("contents"));
        String before = CmmUtil.nvl(request.getParameter("before"));
        String after = CmmUtil.nvl(request.getParameter("after"));

        // 프롬프트 형식 설정
        String text = before + " : " + content + "\n" + after + ":";

        log.info("text : " + text);

        // 클로바 서비스 호출
        String result = clovaService.prompt(text);
        log.info("result : " + result);

        // 응답 결과에서 첫 번째 줄 추출
        int newlineIndex = result.indexOf('\n');
        String extractedSubstring;
        if (newlineIndex != -1) {
            extractedSubstring = result.substring(0, newlineIndex);
        } else {
            extractedSubstring = result;
        }

        log.info(extractedSubstring);

        // 추출된 결과에서 따옴표 제거
        result = extractedSubstring.replace("\"", "");

        log.info("result : " + result);

        log.info(this.getClass().getName() + ".prompt End!");

        // 결과 반환
        return result;
    }
}
