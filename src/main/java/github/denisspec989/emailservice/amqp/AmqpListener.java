package github.denisspec989.emailservice.amqp;

import github.denisspec989.emailservice.dto.EmailDTO;
import github.denisspec989.emailservice.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AmqpListener {

    @Autowired
    private EmailService emailService;
    @Value("${spring.mail.username}")
    private String sender;

    @RabbitListener(queues = "sendEmail")
    public void emailMessage(EmailDTO message) {
        var helper = emailService.createEmail(sender, message.getSubject(), message.getMessage());
        if (isMessageWithAttachment(message)) {
            helper = emailService.createEmailAttachment(helper, message.getAttachment());
        }
        for (var address : message.getToAddress()) {
            emailService.sendEmail(helper, address);
        }
    }

    private boolean isMessageWithAttachment(EmailDTO emailDto) {
        var attachment = emailDto.getAttachment();
        return attachment != null && !attachment.isEmpty();
    }
}
