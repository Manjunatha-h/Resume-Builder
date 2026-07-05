package in.mjunth.resumebuilderjava.security;

import in.mjunth.resumebuilderjava.document.User;
import in.mjunth.resumebuilderjava.repository.UserRepository;
import in.mjunth.resumebuilderjava.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userId = null;

        if(authHeader!=null && authHeader.startsWith("Bearer")){
            token = authHeader.substring(7);
            try{
                userId = jwtUtils.getUserIdFromToken(token);
            }catch (Exception e){
                log.error("Token is Not valid/available");
            }

            if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null){
                try{
                    if(jwtUtils.validateToken(token) && !jwtUtils.isTokenExpired(token)){
                        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not found"));
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user,null,new ArrayList<>());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }catch (Exception e){
                    log.error("Exception occured while validating the user "+ e.getMessage());
                }


            }
        }

        filterChain.doFilter(request,response);


    }
}
