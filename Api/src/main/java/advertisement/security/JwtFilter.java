package advertisement.security;

import advertisement.JwtHandler;
import advertisement.security.exception.AuthException;
import advertisement.security.exception.JwtAuthenticationEntryPoint;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    JwtHandler jwtHandler;
    @Autowired
    LoginPasswordUserDetailsService userDetailsService;
    @Autowired
    JwtAuthenticationEntryPoint entryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authToken == null || !authToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authToken.substring(7);
        String login;
        try {
            login = jwtHandler.extractUsername(jwt);
        } catch (JwtException e) {
            logger.error("Переданный токен неверен либо его срок действия истёк.");
            entryPoint.commence(request, response,
                    new AuthException("Переданный токен неверен либо его срок действия истёк."));
            return;
        }

        if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(login);

            if (jwtHandler.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(token);
            } else {
                logger.error("Переданный токен неверен либо его срок действия истёк.");
                entryPoint.commence(request, response,
                        new AuthException("Переданный токен неверен либо его срок действия истёк."));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}