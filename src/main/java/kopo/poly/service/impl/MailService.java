package kopo.poly.service.impl;

import jakarta.mail.internet.MimeMessage;
import kopo.poly.dto.MailDTO;
import kopo.poly.service.IMailService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class MailService implements IMailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}") //application.properties에 있는 값들을 가져올려고
    private String fromMail;
    @Override
    public int doSendMail(MailDTO pDTO) {

        log.info(this.getClass().getName() + ".doSendMail start!");

        int res = 1;

        String toMail = CmmUtil.nvl(pDTO.toMail());
        String title = CmmUtil.nvl(pDTO.title());
        String contents = CmmUtil.nvl(pDTO.contents());

        log.info("toMail : " + toMail);
        log.info("title : " + title);
        log.info("contents : " + contents);

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper messageHelper = new MimeMessageHelper(message, "UTF-8");

        try {

            messageHelper.setTo(toMail);
            messageHelper.setFrom(fromMail);
            messageHelper.setSubject(title);
            messageHelper.setText(contents);

            mailSender.send(message);
        } catch (Exception e) {
            res = 0;
            log.info("[ERROR]" + this.getClass().getName() + ".doSendMail : " + e);
        }

        log.info(this.getClass().getName() + ".doSendMail end!");
        return res;
    }

}
