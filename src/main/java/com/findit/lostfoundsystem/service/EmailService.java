package com.findit.lostfoundsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    public void sendMatchFoundEmail(String toEmail, String userName,
                                    String itemTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("FindIt - Possible Match Found for Your Item!");
        message.setText(
                "Hello " + userName + ",\n\n" +
                        "Great news! We found a possible match for your item: " + itemTitle + "\n\n" +
                        "Please login to your account to review the match and proceed.\n\n" +
                        "Best regards,\n" +
                        "FindIt Team"
        );
        mailSender.send(message);
    }

    // ─── CLAIM FILED ──────────────────────────────────────
    public void sendClaimFiledEmail(String toEmail, String userName,
                                    String itemTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("FindIt - Someone Claimed Your Found Item");
        message.setText(
                "Hello " + userName + ",\n\n" +
                        "Someone has filed a claim on your found item: " + itemTitle + "\n\n" +
                        "Please login to your account to review the claim.\n\n" +
                        "Best regards,\n" +
                        "FindIt Team"
        );
        mailSender.send(message);
    }

    // ─── CLAIM APPROVED ───────────────────────────────────
    public void sendClaimApprovedEmail(String toEmail, String userName,
                                       String itemTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("FindIt - Your Claim Has Been Approved!");
        message.setText(
                "Hello " + userName + ",\n\n" +
                        "Your claim for the item: " + itemTitle + " has been APPROVED!\n\n" +
                        "Please contact the finder to arrange pickup.\n\n" +
                        "Best regards,\n" +
                        "FindIt Team"
        );
        mailSender.send(message);
    }

    // ─── CLAIM REJECTED ───────────────────────────────────
    public void sendClaimRejectedEmail(String toEmail, String userName,
                                       String itemTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("FindIt - Your Claim Has Been Rejected");
        message.setText(
                "Hello " + userName + ",\n\n" +
                        "Unfortunately your claim for the item: " + itemTitle +
                        " has been REJECTED.\n\n" +
                        "You can still browse other items or file a new claim.\n\n" +
                        "Best regards,\n" +
                        "FindIt Team"
        );
        mailSender.send(message);
    }

    public void sendMatchConfirmedEmail(String toEmail, String userName,
                                        String itemTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("FindIt - Match Confirmed! 🎉");
        message.setText(
                "Hello " + userName + ",\n\n" +
                        "Great news! A match for your item '" + itemTitle +
                        "' has been officially CONFIRMED by our team.\n\n" +
                        "Please login to your account to proceed with the next steps.\n\n" +
                        "Best regards,\nFindIt Team"
        );
        mailSender.send(message);
    }

    public void sendMatchRejectedEmail(String toEmail, String userName,
                                       String itemTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("FindIt - Match Update");
        message.setText(
                "Hello " + userName + ",\n\n" +
                        "Unfortunately the suggested match for your item '" + itemTitle +
                        "' has been reviewed and rejected by our team.\n\n" +
                        "Don't worry — your item is back to OPEN status " +
                        "and we'll keep looking for matches.\n\n" +
                        "Best regards,\nFindIt Team"
        );
        mailSender.send(message);
    }
}
