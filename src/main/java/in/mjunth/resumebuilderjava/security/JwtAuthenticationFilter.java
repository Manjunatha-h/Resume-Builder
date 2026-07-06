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
                log.error("token invalid/not found");
            }
        }

        if(userId!=null && SecurityContextHolder.getContext().getAuthentication() == null){
            try{
                log.info("Inside JwtAuthenticationFilter ");
                if(jwtUtils.validateToken(token) && !jwtUtils.isTokenExpired(token)){
                    User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("user not found"));
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user,null,new ArrayList<>());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }catch (Exception e){
                log.error("token invalid/not found");
            }
        }
        filterChain.doFilter(request,response);

    }
}


