package in.mjunth.resumebuilderjava.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import in.mjunth.resumebuilderjava.document.Resume;
import in.mjunth.resumebuilderjava.dto.AuthResponse;
import in.mjunth.resumebuilderjava.repository.ResumeRepository;
import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadService {

    private final Cloudinary cloudinary;
    private final AuthService authService;
    private final ResumeRepository resumeRepository;

    public String uploadSingleImage(MultipartFile file) throws IOException {
        Map<String, Object> ImageUpRes = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type","image"));
        return ImageUpRes.get("secure_url").toString();
    }

    public Map<String, String> uploadThumbnailAndProfileInfo(String resumeId, MultipartFile thumbnail, MultipartFile profileImage, Object principalObject) throws IOException {
        AuthResponse profile = authService.getProfile(principalObject);
        Map<String,String> response = new HashMap<>();

        Resume existingResume = resumeRepository.findByUserIdAndId(profile.getId(),resumeId).orElseThrow(()->new RuntimeException("Resume not found"));

        if(Objects.nonNull(thumbnail)){
            log.info("inside uploadThumbnailAndProfileInfo()-> if(Objects.nonNull(thumbnail))");
            String thumbnailImageLink = uploadSingleImage(thumbnail);
            existingResume.setThumbnailLink(thumbnailImageLink);
            response.put("thumbnailLink",thumbnailImageLink);
        }

        if(Objects.nonNull(profileImage)){
            log.info("inside uploadThumbnailAndProfileInfo()-> if(Objects.nonNull(profileImage))");
            String profileImageLink = uploadSingleImage(profileImage);
            existingResume.getProfileInfo().setProfilePreviewUrl(profileImageLink);
            response.put("profilePreviewUrl",profileImageLink);
        }

        resumeRepository.save(existingResume);
        return response;
    }
}
