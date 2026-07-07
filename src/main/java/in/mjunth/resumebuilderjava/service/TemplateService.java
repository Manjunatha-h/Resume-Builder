package in.mjunth.resumebuilderjava.service;

import in.mjunth.resumebuilderjava.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.aggregation.VariableOperators;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static in.mjunth.resumebuilderjava.utils.AppConstants.PREMIUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {

    private final AuthService authService;

    public Map<String, Object> getTemplates(Object principal) {
        AuthResponse profile = authService.getProfile(principal);

        Map<String,Object> response = new HashMap<>();
        List<String> availableTemplates;

        boolean isPremium = PREMIUM.equalsIgnoreCase(profile.getSubscriptionPlan());
        if(isPremium){
            availableTemplates = List.of("01","02","03");
        }
        else{
            availableTemplates = List.of("01");
        }

        response.put("availableTemplates",availableTemplates);
        response.put("subscriptionPlan",profile.getSubscriptionPlan());
        response.put("isPremium",true);
        response.put("allTemplates",List.of("01","02","03"));

        return response;
    }
}
