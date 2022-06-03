package me.janah.ebankingbackend.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.janah.ebankingbackend.security.JWTUtility;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // set CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response
                .setHeader("Access-Control-Allow-Headers", "Origin, origin, x-requested-with, authorization, " +
                        "Content-Type, Authorization, credential, X-XSRF-TOKEN");
        // skip login request
        // this was a solution for the CORS problem in the login page
        if (request.getRequestURI().equals("/login")) {
            //System.out.printf("%s:%n", "skip login request");
            filterChain.doFilter(request, response);
            return;
        }

        // skip OPTIONS request to avoid CORS error
        if (request.getMethod().equals("OPTIONS")) {
            System.out.printf("%s:%n", "skip OPTIONS request");
            // send OK response
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String authorizationToken = request.getHeader(JWTUtility.HEADER_STRING);

        if (authorizationToken == null || !authorizationToken.startsWith(JWTUtility.TOKEN_PREFIX)) {
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authorizationToken.substring(JWTUtility.TOKEN_PREFIX.length());
        String username = JWTUtility.validateToken(jwt);
        System.out.println("username: " + username);
        if (username == null) {
            SecurityContextHolder.getContext().setAuthentication(null);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            Map<String, String> errorMessage = new HashMap<>();
            errorMessage.put("error", "Invalid token");
            new ObjectMapper().writeValue(response.getWriter(), errorMessage);
            return;
        }

        // get Roles from token
        List<String> roles = JWTUtility.getRoles(jwt);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}