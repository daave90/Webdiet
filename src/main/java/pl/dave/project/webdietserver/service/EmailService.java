package pl.dave.project.webdietserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.dave.project.webdietserver.dto.EmailRequest;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.exeption.ErrorCode;
import pl.dave.project.webdietserver.exeption.RestApiException;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    public void sendEmail(EmailRequest request, User user, String text) {
        log.info("**************************************************************************************************");
        log.info("Send email to: " + request.getRecipient());
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);
        RestApiException.throwExceptionIfConditionIsTrue(StringUtils.isEmpty(request.getRecipient()), ErrorCode.EMPTY_EMAIL_RECIPIENT);
        RestApiException.throwExceptionIfConditionIsTrue(StringUtils.isEmpty(text), ErrorCode.EMPTY_EMAIL_TEXT);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getRecipient());
        message.setSubject(request.getSubject());
        message.setText(text);
        emailSender.send(message);
    }
}
