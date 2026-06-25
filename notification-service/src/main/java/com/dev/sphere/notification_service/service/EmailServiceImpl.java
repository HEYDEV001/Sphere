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

        String body = """
<html>
<body style="margin:0;padding:0;background-color:#f4f6f8;font-family:Arial,sans-serif;">
    <table width="100%%" cellpadding="0" cellspacing="0">
        <tr>
            <td align="center">
                <table width="600" cellpadding="0" cellspacing="0"
                       style="background:#ffffff;margin:40px auto;
                              border-radius:12px;padding:40px;">

                    <tr>
                        <td align="center">
                            <h1 style="color:#0077B5;margin:0;">
                                Sphere
                            </h1>
                        </td>
                    </tr>

                    <tr>
                        <td align="center" style="padding-top:20px;">
                            <h2 style="font-size:38px;
                                       color:#333333;
                                       margin:0;">
                                Reset Your Password
                            </h2>
                        </td>
                    </tr>

                    <tr>
                        <td style="padding-top:35px;">
                            <p style="font-size:18px;color:#444;">
                                Hi %s,
                            </p>

                            <p style="font-size:16px;
                                      line-height:1.7;
                                      color:#555;">
                               Forgot your password? That’s okay, it happens!
                            </p>

                            <p style="font-size:16px;
                                      line-height:1.0;
                                      color:#555;">
                                Click the button below to create a new password for your SPHERE account.
                                For security reasons, this link will expire in
                                <strong>15 minutes</strong>.
                            </p>
                        </td>
                    </tr>

                    <tr>
                        <td align="center" style="padding:30px 0;">
                            <a href="%s"
                               style="background:#0077B5;
                                      color:#ffffff;
                                      text-decoration:none;
                                      padding:14px 32px;
                                      border-radius:8px;
                                      font-size:16px;
                                      font-weight:bold;
                                      display:inline-block;">
                                RESET PASSWORD
                            </a>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            <p style="font-size:14px;
                                      color:#777;
                                      line-height:1.6;">
                                If you didn't request a password reset,
                                you can safely ignore this email.
                                Your password will remain unchanged.
                            </p>
                        </td>
                    </tr>

                    <tr>
                        <td style="padding-top:25px;">
                            <hr style="border:none;border-top:1px solid #e5e7eb;">
                        </td>
                    </tr>

                    <tr>
                        <td style="padding-top:20px;">
                            <p style="font-size:14px;color:#777;">
                                Regards,<br>
                                <strong>Sphere Team</strong>
                            </p>
                        </td>
                    </tr>

                </table>
            </td>
        </tr>
    </table>
</body>
</html>
""".formatted(name, resetLink);

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
