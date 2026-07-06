package in.mjunth.resumebuilderjava.controller;

import in.mjunth.resumebuilderjava.document.User;
import in.mjunth.resumebuilderjava.dto.AuthResponse;
import in.mjunth.resumebuilderjava.dto.LoginRequest;
import in.mjunth.resumebuilderjava.dto.RegisterRequest;
import in.mjunth.resumebuilderjava.repository.UserRepository;
import in.mjunth.resumebuilderjava.service.AuthService;
import in.mjunth.resumebuilderjava.service.ImageUploadService;
import jakarta.mail.Multipart;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static in.mjunth.resumebuilderjava.utils.AppConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH_CONTROLLER)
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final ImageUploadService imageUploadService;
    private final UserRepository userRepository;

    @PostMapping(REGISTER)
    public ResponseEntity<?> regsiter(@Valid @RequestBody RegisterRequest requestBody) {
        AuthResponse response = authService.register(requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        authService.VerfiyEmail(token);
        return ResponseEntity.status(HttpStatus.FOUND).body(Map.of("message","Email Verified Successfully"));
    }

    @PostMapping(UPLOAD_PROFILE)
    public ResponseEntity<?> uploadImage(@RequestPart("image") MultipartFile file) throws IOException {
        Map<String, String> publicUrl = imageUploadService.uploadSingleImage(file);
        return ResponseEntity.ok(publicUrl);
    }

    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        log.info("INSIDE THE /login : calling AuthService");
        AuthResponse response = authService.login(request);
        log.info("Login done" + response);
        return ResponseEntity.ok(response);
    }

    @PostMapping(RESEND_VERIFICATION)
    public ResponseEntity<?> resendEmailVerfication(@RequestBody Map<String,String> body){
        String email = body.get("email");

        if(Objects.isNull(email)){
            return ResponseEntity.badRequest().body(Map.of("message","email is required"));
        }
        authService.resendVerification(email);
        return ResponseEntity.ok().body(Map.of("success",true,",message","Email verification is sent successfully"));
    }

    @GetMapping(PROFILE)
    public ResponseEntity<?> getProfile(Authentication authentication){
        Object principalObject = authentication.getPrincipal();
        AuthResponse authResponse = authService.getProfile(principalObject);
        return ResponseEntity.ok(authResponse);
    }
}
