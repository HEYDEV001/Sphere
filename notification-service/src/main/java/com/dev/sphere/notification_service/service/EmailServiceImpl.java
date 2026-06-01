package com.dev.sphere.notification_service.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final SendGrid sendGrid;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name}")
    private String fromName;


    @Override
    public void sendPasswordResetEmail(String toEmail, String name, String resetLink) throws IOException {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);
        String subject = "Reset your Sphere password.";

        String body = "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                + "<h2>Hi " + name + ",</h2>"
                + "<p>You requested to reset your <strong>Sphere</strong> password.</p>"
                + "<p>Click the button below to reset it. "
                + "This link expires in <strong>15 minutes</strong>.</p>"
                + "<a href='" + resetLink + "' "
                + "style='background-color:#0077B5;color:white;padding:12px 24px;"
                + "text-decoration:none;border-radius:6px;display:inline-block;margin:16px 0;'>"
                + "Reset Password</a>"
                + "<p>If you didn't request this, you can safely ignore this email.</p>"
                + "<p>— The Sphere Team</p>"
                + "</div>";

        Content content = new Content("text/html", body);
        Mail mail = new Mail(from, subject, to, content);

        try{
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            log.info("SendGrid response status: {} for email: {}",
                    response.getStatusCode(), toEmail);

            if (response.getStatusCode() >= 400) {
                log.error("SendGrid error body: {}", response.getBody());
                throw new RuntimeException("Failed to send reset email — SendGrid error");
            }


        } catch(IOException exception){
            log.error("Failed to send reset email to {}: {}", toEmail, exception.getMessage());
            throw new RuntimeException("Failed to send reset email");
        }
    }


}
