package in.mjunth.resumebuilderjava.service;

import in.mjunth.resumebuilderjava.document.User;
import in.mjunth.resumebuilderjava.dto.AuthResponse;
import in.mjunth.resumebuilderjava.dto.LoginRequest;
import in.mjunth.resumebuilderjava.dto.RegisterRequest;
import in.mjunth.resumebuilderjava.exception.ResourceExistsException;
import in.mjunth.resumebuilderjava.repository.UserRepository;
import in.mjunth.resumebuilderjava.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository usersRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Value("${app.baseUrl}")
    private String appBaseUrl;

    public AuthResponse register(RegisterRequest request){
        log.info("Inside AuthService: regsiter() {}",request);
        if(usersRepository.existsByEmail(request.getEmail())){
            throw new ResourceExistsException("User already Exists with the EMail");

        }else{
            User user = toDocument(request);

            usersRepository.save(user);

            sendVerficationMail(user);

            AuthResponse response = toResponse(user);

            return response;
        }
    }
    
    public void VerfiyEmail(String token){
        User user = usersRepository.findByVerificationToken(token).orElseThrow(()->new RuntimeException("Invalid Token"));

        if(user.getVerificationExpires() != null && user.getVerificationExpires().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Verification Token has expired, Please request new one");
        }

        user.setEmailVerified(true);
        user.setVerificationExpires(null);
        user.setVerificationToken(null);
        usersRepository.save(user);
    }

    private void sendVerficationMail(User user) {
        try{
            String link = appBaseUrl+"/api/auth/verify-email?token="+user.getVerificationToken();
            String html =
                    "<div style='font-family:Arial,sans-serif;max-width:600px;margin:auto;padding:20px;'>" +
                            "<h2>Verify Your Email</h2>" +
                            "<p>Hi " + user.getName() + ",</p>" +
                            "<p>Thank you for registering. Please click the button below to verify your email address.</p>" +
                            "<p><a href='" + link + "' " +
                            "style='display:inline-block;padding:12px 20px;background:#6366F1;color:white;text-decoration:none;border-radius:6px;'>Verify Email</a></p>" +
                            "<p>If the button doesn't work, copy this link:</p>" +
                            "<p><a href='" + link + "'>" + link + "</a></p>" +
                            "<p>This verification link expires in 24 hours.</p>" +
                            "<hr>" +
                            "<p style='font-size:12px;color:#666;'>If you did not create an account, ignore this email.</p>" +
                            "</div>";
            emailService.sendHtmlContent(user.getEmail(),"Verify your Email",html);

        }catch(Exception e){
            throw new RuntimeException("Failed to send the verfication mail"+ e.getMessage());
        }
    }

    private AuthResponse toResponse(User user) {
        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .token(user.getVerificationToken())
                .subscriptionPlan(user.getSubscriptionPlan())
                .emailVerified(user.isEmailVerified())
                .build();
    }

    private User toDocument(RegisterRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileImageUrl(request.getProfileImageUrl())
                .emailVerified(false)
                .subscriptionPlan("Basic")
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("INSIDE AuthService : login()");
        User existingUser = usersRepository.findByEmail((request.getEmail())).orElseThrow(()->new RuntimeException("User not found"));
        log.info("User found "+ existingUser);
        if(!passwordEncoder.matches(request.getPassword(),existingUser.getPassword())){
            throw new UsernameNotFoundException("Invalid email or password ");
        }

        if(!existingUser.isEmailVerified()){
            throw new RuntimeException("please verify email before Logging in.");
        }

        String token = jwtUtils.generateToken(existingUser.getId());

        AuthResponse response = toResponse(existingUser);
        response.setToken(token);

        return response;
    }
}
