package github.denisspec989.emailservice.service;

import github.denisspec989.emailservice.dto.AttachementDTO;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.List;

public interface EmailService {
    MimeMessageHelper createEmail(String sender, String subject, String message);
    MimeMessageHelper createEmailAttachment(MimeMessageHelper helper, List<AttachementDTO> attachment);
    void sendEmail(MimeMessageHelper helper, String toAddress);
}
