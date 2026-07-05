package in.mjunth.resumebuilderjava.controller;

import in.mjunth.resumebuilderjava.dto.AuthResponse;
import in.mjunth.resumebuilderjava.dto.LoginRequest;
import in.mjunth.resumebuilderjava.dto.RegisterRequest;
import in.mjunth.resumebuilderjava.service.AuthService;
import in.mjunth.resumebuilderjava.service.ImageUploadService;
import jakarta.mail.Multipart;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static in.mjunth.resumebuilderjava.utils.AppConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH_CONTROLLER)
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final ImageUploadService imageUploadService;

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

    @PostMapping(UPLOAD_IMAGE)
    public ResponseEntity<?> uploadImage(@RequestPart("image") MultipartFile file) throws IOException {
        Map<String, String> publicUrl = imageUploadService.uploadSingleImage(file);
        return ResponseEntity.ok(publicUrl);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        log.info("INSIDE THE /login : calling AuthService");
        AuthResponse response = authService.login(request);
        log.info("Login done" + response);
        return ResponseEntity.ok(response);
    }
}
