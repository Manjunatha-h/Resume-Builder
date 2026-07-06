package in.mjunth.resumebuilderjava.service;

import in.mjunth.resumebuilderjava.document.Resume;
import in.mjunth.resumebuilderjava.dto.AuthResponse;
import in.mjunth.resumebuilderjava.dto.CreateResumeRequest;
import in.mjunth.resumebuilderjava.dto.ResumeUpdateRequest;
import in.mjunth.resumebuilderjava.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public Resume updateResumeContent(String resumeId,
                                      ResumeUpdateRequest updatedData,
                                      Object principal) {

        AuthResponse profile = authService.getProfile(principal);

        Resume currentResume = resumeRepository
                .findByUserIdAndId(profile.getId(), resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        currentResume.setTitle(updatedData.getTitle());
        currentResume.setThumbnailLink(updatedData.getThumbnailLink());

        // Template
        if(updatedData.getTemplate() != null){
            Resume.Template template = new Resume.Template();
            template.setTheme(updatedData.getTemplate().getTheme());
            template.setColorPalette(updatedData.getTemplate().getColorPalette());

            currentResume.setTemplate(template);
        }

        // Profile Info
        if(updatedData.getProfileInfo() != null){
            Resume.ProfileInfo profileInfo = new Resume.ProfileInfo();
            profileInfo.setProfilePreviewUrl(updatedData.getProfileInfo().getProfilePreviewUrl());
            profileInfo.setFullName(updatedData.getProfileInfo().getFullName());
            profileInfo.setDesignation(updatedData.getProfileInfo().getDesignation());
            profileInfo.setSummary(updatedData.getProfileInfo().getSummary());

            currentResume.setProfileInfo(profileInfo);
        }

        // Contact Info
        if(updatedData.getContactInfo() != null){
            Resume.ContactInfo contactInfo = new Resume.ContactInfo();
            contactInfo.setEmail(updatedData.getContactInfo().getEmail());
            contactInfo.setPhone(updatedData.getContactInfo().getPhone());
            contactInfo.setLocation(updatedData.getContactInfo().getLocation());
            contactInfo.setLinkedIn(updatedData.getContactInfo().getLinkedIn());
            contactInfo.setGithub(updatedData.getContactInfo().getGithub());
            contactInfo.setWebsite(updatedData.getContactInfo().getWebsite());

            currentResume.setContactInfo(contactInfo);
        }

        // Collections
        currentResume.setWorkExperiences(
                updatedData.getWorkExperiences()
                        .stream()
                        .map(exp -> Resume.WorkExperience.builder()
                                .company(exp.getCompany())
                                .role(exp.getRole())
                                .startDate(exp.getStartDate())
                                .endDate(exp.getEndDate())
                                .description(exp.getDescription())
                                .build())
                        .toList()
        );

        currentResume.setEducation(
                updatedData.getEducation()
                        .stream()
                        .map(edu -> Resume.Education.builder()
                                .degree(edu.getDegree())
                                .institution(edu.getInstitution())
                                .startDate(edu.getStartDate())
                                .endDate(edu.getEndDate())
                                .build())
                        .toList()
        );

        currentResume.setSkills(
                updatedData.getSkills()
                        .stream()
                        .map(skill -> Resume.Skill.builder()
                                .skill(skill.getSkill())
                                .progress(skill.getProgress())
                                .build())
                        .toList()
        );

        currentResume.setProjects(
                updatedData.getProjects()
                        .stream()
                        .map(project -> Resume.Project.builder()
                                .title(project.getTitle())
                                .description(project.getDescription())
                                .github(project.getGithub())
                                .liveDemo(project.getLiveDemo())
                                .build())
                        .toList()
        );

        currentResume.setCertifications(
                updatedData.getCertifications()
                        .stream()
                        .map(cert -> Resume.Certification.builder()
                                .title(cert.getTitle())
                                .issuer(cert.getIssuer())
                                .year(cert.getYear())
                                .build())
                        .toList()
        );

        currentResume.setLanguages(
                updatedData.getLanguages()
                        .stream()
                        .map(lang -> Resume.Languages.builder()
                                .name(lang.getName())
                                .progress(lang.getProgress())
                                .build())
                        .toList()
        );

        currentResume.setInterests(updatedData.getInterests());

        return resumeRepository.save(currentResume);
    }


    public Resume deleteResume(String resumeId, Object principal) {
        AuthResponse profile = authService.getProfile(principal);
        Resume deletingResume = resumeRepository.findByUserIdAndId(profile.getId(),resumeId).orElseThrow(()->new RuntimeException("Resume not found"));
        resumeRepository.deleteByUserIdAndId(profile.getId(),resumeId);
        return deletingResume;
    }
}
