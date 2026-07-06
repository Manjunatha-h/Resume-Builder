package in.mjunth.resumebuilderjava.service;

import in.mjunth.resumebuilderjava.document.Resume;
import in.mjunth.resumebuilderjava.dto.AuthResponse;
import in.mjunth.resumebuilderjava.dto.CreateResumeRequest;
import in.mjunth.resumebuilderjava.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResumeService {

    private final AuthService authService;
    private final ResumeRepository resumeRepository;

    public Resume createResume(CreateResumeRequest request, Object principal) {
        Resume resume = new Resume();

        AuthResponse user = authService.getProfile(principal);

        resume.setUserId(user.getId());
        resume.setTitle(request.getTitle());

        setDefaultResumeData(resume);

        return resumeRepository.save(resume);

    }

    private void setDefaultResumeData(Resume resume) {
        resume.setProfileInfo(new Resume.ProfileInfo());
        resume.setContactInfo(new Resume.ContactInfo());
        resume.setWorkExperiences(new ArrayList<>());
        resume.setCertifications(new ArrayList<>());
        resume.setEducation(new ArrayList<>());
        resume.setInterests(new ArrayList<>());
        resume.setSkills(new ArrayList<>());
        resume.setProjects(new ArrayList<>());
        resume.setLanguages(new ArrayList<>());
    }

    public List<Resume> getUserResumes(Object principal) {
        AuthResponse profile = authService.getProfile(principal);
        List<Resume> resumes = resumeRepository.findByUserIdOrderByUpdatedAt(profile.getId());
        return resumes;
    }

    public Resume getResumeById(String resumeId, Object principalObject) {

        AuthResponse profile = authService.getProfile(principalObject);
        return resumeRepository.findByUserIdAndId(profile.getId(),resumeId).orElseThrow(()-> new RuntimeException("Resume Not Found"));
    }
}
