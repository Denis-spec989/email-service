package github.denisspec989.emailservice.service.impl;

import github.denisspec989.emailservice.dto.AttachementDTO;
import github.denisspec989.emailservice.service.EmailService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;

    @Override
    @SneakyThrows
    public MimeMessageHelper createEmail(String sender, String subject, String message) {
        var mimeMessage = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(mimeMessage,true,"utf-8");
        helper.setFrom(sender);
        helper.setSubject(subject);
        helper.setText(message,true);
        return helper;
    }

    @SneakyThrows
    @Override
    public MimeMessageHelper createEmailAttachment(MimeMessageHelper helper, List<AttachementDTO> attachment) {
        for(var attach: attachment){
            var content = Base64.getDecoder().decode(attach.getContent());
            var br = new ByteArrayResource(content);
            helper.addAttachment(attach.getFilename(), br, attach.getContentType());
        }
        return helper;
    }

    @SneakyThrows
    @Override
    public void sendEmail(MimeMessageHelper helper, String toAddress) {
        helper.setTo(toAddress);
        helper.setFrom(sender);
        mailSender.send(helper.getMimeMessage());
    }
}
