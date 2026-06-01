package com.dev.sphere.notification_service.service;

import java.io.IOException;

public interface EmailService {

    void sendPasswordResetEmail(String toEmail, String name, String resetLink)throws IOException;

}
