package in.mjunth.resumebuilderjava.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadService {

    private final Cloudinary cloudinary;

    public Map<String,String> uploadSingleImage(MultipartFile file) throws IOException {
        Map<String, Object> ImageUpRes = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type","image"));
        return Map.of("imageUrl",ImageUpRes.get("secure_url").toString());
    }
}
