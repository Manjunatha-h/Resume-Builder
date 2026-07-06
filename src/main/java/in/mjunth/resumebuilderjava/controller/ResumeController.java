package in.mjunth.resumebuilderjava.controller;

import in.mjunth.resumebuilderjava.document.Resume;
import in.mjunth.resumebuilderjava.dto.CreateResumeRequest;
import in.mjunth.resumebuilderjava.dto.ResumeUpdateRequest;
import in.mjunth.resumebuilderjava.service.ImageUploadService;
import in.mjunth.resumebuilderjava.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static in.mjunth.resumebuilderjava.utils.AppConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(RESUME)
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;
    private final ImageUploadService imageUploadService;

    @PostMapping
    public ResponseEntity<?> createResume(@Valid @RequestBody CreateResumeRequest request, Authentication authentication){
        Resume resume = resumeService.createResume(request,authentication.getPrincipal());
        return ResponseEntity.status(HttpStatus.CREATED).body(resume);
    }

    @GetMapping
    public ResponseEntity<?> getResumes(Authentication authentication){
        log.info("Inside --> GetResumes()");
        List<Resume> resumes = resumeService.getUserResumes(authentication.getPrincipal());
        return ResponseEntity.ok(resumes);
    }

    @GetMapping(ID)
    public ResponseEntity<?> getResumeById(@PathVariable String id,Authentication authentication){
        Resume resume = resumeService.getResumeById(id, authentication.getPrincipal());
        return ResponseEntity.ok(resume);
    }

    @PutMapping(ID)
    public ResponseEntity<?> updateResume(@PathVariable String id,
                                          @RequestBody ResumeUpdateRequest updatedData,
                                          Authentication authentication){
        Resume updatedResume = resumeService.updateResumeContent(id,updatedData,authentication.getPrincipal());
        return ResponseEntity.ok(updatedResume);
    }

    @PutMapping(UPLOAD_IMAGE)
    public ResponseEntity<?> uploadResumeImages(@PathVariable String id,
                                                @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
                                                @RequestPart(value = "profileImage",required = false) MultipartFile profileImage,
                                                Authentication authentication) throws IOException {
        Map<String,String> linkResponse = imageUploadService.uploadThumbnailAndProfileInfo(id,thumbnail,profileImage,authentication.getPrincipal());
        return ResponseEntity.ok(linkResponse);
    }

    @DeleteMapping(ID)
    public ResponseEntity<?> deleteResume(@PathVariable String id,Authentication authentication){
        Resume deletedResume = resumeService.deleteResume(id,authentication.getPrincipal());
        return ResponseEntity.ok(Map.of("Message","Resume deleted Successfully"));
    }

}
