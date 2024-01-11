package be.yapock.overwatchtournamentmanager.bll.mailing;

import be.yapock.overwatchtournamentmanager.dal.models.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    /**
     * envoye un mail de reset de mot de passe
     * @param user
     * @throws MessagingException
     */
    public void sendPasswordResetRequest(User user) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", user.getUsername());
        properties.put("password", user.getPassword());
        properties.put("login_link", "http://localhost:8081/swagger-ui/index.html#/user-controller/login");
        Mail mail = Mail.builder()
                .to(user.getEmail())
                .from("playzoneguichet@gmail.com")
                .mailTemplate(new Mail.MailTemplate("resetPassword", properties))
                .subject("demande de reset de mot de passe")
                .build();

        String html = getHtmlContent(mail);

        helper.setTo(mail.getTo());
        helper.setFrom(mail.getFrom());
        helper.setSubject(mail.getSubject());
        helper.setText(html,true);

        mailSender.send(message);
    }

    private String getHtmlContent(Mail mail){
        Context context = new Context();
        context.setVariables(mail.getMailTemplate().getProps());
        return springTemplateEngine.process(mail.getMailTemplate().getTemplate(), context);
    }
}