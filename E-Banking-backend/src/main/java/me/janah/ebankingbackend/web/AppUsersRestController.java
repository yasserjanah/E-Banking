package me.janah.ebankingbackend.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.janah.ebankingbackend.dtos.AppUserDTO;
import me.janah.ebankingbackend.entities.Customer;
import me.janah.ebankingbackend.entities.Employee;
import me.janah.ebankingbackend.exceptions.UserNotExistsException;
import me.janah.ebankingbackend.security.JWTUtility;
import me.janah.ebankingbackend.security.entities.AppUser;
import me.janah.ebankingbackend.security.services.AppUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/users")
public class AppUsersRestController {

    private AppUserService appUserService;
    //private BankAccountMapper dtoMapper;
    private UserDetailsService userDetailsService;


    @GetMapping("/current")
    public AppUserDTO getCurrentUser(HttpServletRequest request,
                                     HttpServletResponse response) throws IOException {
        // set CORS header
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response
                .setHeader("Access-Control-Allow-Headers", "Origin, origin, x-requested-with, authorization, " +
                        "Content-Type, Authorization, credential, X-XSRF-TOKEN");
        String token = request.getHeader(JWTUtility.HEADER_STRING);
        if (token == null)
            return null;
        token = token.substring(JWTUtility.TOKEN_PREFIX.length());
        String username = JWTUtility.validateToken(token);
        System.out.println("Username: "+username);
        AppUser appUser = appUserService.loadUserByUsername(username);
        AppUserDTO appUserDTO = new AppUserDTO();
        BeanUtils.copyProperties(appUser, appUserDTO);
        if (appUser instanceof Customer) {
            appUserDTO.setUserType("CUSTOMER");
        } else if (appUser instanceof Employee) {
            appUserDTO.setUserType("EMPLOYEE");
        }
        return appUserDTO;
    }

    @GetMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException, UserNotExistsException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response
                .setHeader("Access-Control-Allow-Headers", "Origin, origin, x-requested-with, authorization, " +
                        "Content-Type, Authorization, credential, X-XSRF-TOKEN");
        String authorizationToken = request.getHeader(JWTUtility.HEADER_STRING);
        if (authorizationToken != null && authorizationToken.startsWith(JWTUtility.TOKEN_PREFIX)) {
            String jwt = authorizationToken.substring(JWTUtility.TOKEN_PREFIX.length());
            String username = JWTUtility.validateToken(jwt);
            if (username != null) {
                AppUser appUser = appUserService.loadUserByUsername(username);
                if (appUser == null) {
                    throw new UserNotExistsException("User not found");
                }
                // create User object
                User user = (User) userDetailsService.loadUserByUsername(username);
                String newAccessToken = JWTUtility.generateToken(user);
                Map<String, String> tokenMap = new HashMap<>();
                tokenMap.put("accessToken", newAccessToken);
                tokenMap.put("refreshToken", jwt);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), tokenMap);
            } else {
                response.setHeader("Error", "Invalid Token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            }
        } else {
            response.setHeader("Error", "No Token found");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No Token found");
        }
    }

}
