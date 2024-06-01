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

    @PostMapping("/prompt")
    public String prompt(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".prompt Start!");

        String content = CmmUtil.nvl(request.getParameter("contents"));
        String before = CmmUtil.nvl(request.getParameter("before"));
        String after = CmmUtil.nvl(request.getParameter("after"));

        String text = before +  " : " +  content + "\n" + after + ":" ;

        log.info("text : " + text);

        String result = clovaService.prompt(text);
        log.info("result : " + result);


        int newlineIndex = result.indexOf('\n');

        // '\n' 이전까지의 부분 문자열을 추출합니다.
        String extractedSubstring;
        if (newlineIndex != -1) {
            extractedSubstring = result.substring(0, newlineIndex);
        } else {
            // '\n'이 문자열에 없을 경우 전체 문자열을 추출합니다.
            extractedSubstring = result;
        }

        log.info(extractedSubstring);
//        Pattern pattern = Pattern.compile("\"(.*?)\"");
//        Matcher matcher = pattern.matcher(result);

        // 패턴에 맞는 부분이 있다면 추출하여 반환
//        if (matcher.find()) {
//            log.info(matcher.group());
//            result = matcher.group(1);
//        } else {
//            result =  "다시 시도해 주세요"; // 패턴에 맞는 부분이 없을 경우 빈 문자열 반환
//        }

        result =  extractedSubstring.replace("\"", "");


        log.info("result : " + result);

        log.info(this.getClass().getName() + ".prompt Start!");

        return result;
    }
}
