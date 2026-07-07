package in.mjunth.resumebuilderjava.controller;

import java.util.Map;

import in.mjunth.resumebuilderjava.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static in.mjunth.resumebuilderjava.utils.AppConstants.TEMPLATES;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(TEMPLATES)
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping
    public ResponseEntity<?> getTemplates(Authentication authentication){
        Map<String,Object> response = templateService.getTemplates(authentication.getPrincipal());
        return ResponseEntity.ok(response);
        }
}
