package com.findit.lostfoundsystem.service;

import com.findit.lostfoundsystem.dto.ChangePasswordDTO;
import com.findit.lostfoundsystem.dto.LoginDTO;
import com.findit.lostfoundsystem.dto.RegisterDTO;
import com.findit.lostfoundsystem.dto.ResetPasswordDTO;
import com.findit.lostfoundsystem.enums.Role;
import com.findit.lostfoundsystem.enums.UserStatus;
import com.findit.lostfoundsystem.model.PasswordResetToken;
import com.findit.lostfoundsystem.model.User;
import com.findit.lostfoundsystem.model.VerificationToken;
import com.findit.lostfoundsystem.repository.PasswordResetTokenRepository;
import com.findit.lostfoundsystem.repository.UserRepository;
import com.findit.lostfoundsystem.repository.VerificationTokenRepository;
import com.findit.lostfoundsystem.security.JWTUtils;
import com.findit.lostfoundsystem.security.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JWTUtils jwtUtils;
    private final MyUserDetailsService userDetailsService;

    @Autowired
    public UserService(UserRepository userRepository, VerificationTokenRepository tokenRepository, PasswordResetTokenRepository passwordResetTokenRepository, PasswordEncoder passwordEncoder, EmailService emailService, JWTUtils jwtUtils, MyUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    public void register(RegisterDTO request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .isVerified(false)
                .status(UserStatus.INACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        tokenRepository.save(verificationToken);

        // 📧 Send email
        String link = "http://localhost:8080/api/auth/verify?token=" + token;

        emailService.sendEmail(
                user.getEmail(),
                "Verify your account",
                "Click this link to verify: " + link
        );
    }

    public void verifyUser(String token) {

        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);
    }

    // Login
    public String login(LoginDTO request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your email before logging in.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        return jwtUtils.generateToken(userDetails);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();

        passwordResetTokenRepository.save(resetToken);

        String message = "You requested to reset your password.\n\n" +
                "Use the following token to reset your password:\n\n" +
                "Token: " + token + "\n\n" +
                "This token will expire in 1 hour." ;

        emailService.sendEmail(
                user.getEmail(),
                "Reset your password",
                message
        );
    }

    public void resetPassword(ResetPasswordDTO request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public void changePassword(String email, ChangePasswordDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

}
