package me.janah.ebankingbackend.security;

import lombok.AllArgsConstructor;
import me.janah.ebankingbackend.security.filters.JwtAuthenticationFilter;
import me.janah.ebankingbackend.security.filters.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors().disable(); // security issue (CORS Misconfiguration) TODO: fix it
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.headers().frameOptions().disable();
        //http.formLogin();

        // for all
        //http.authorizeRequests().antMatchers("/h2-console/**").permitAll();
        http.authorizeRequests().antMatchers("/login").permitAll();
        http.authorizeRequests().antMatchers("/users/refresh-token").authenticated();
        http.authorizeRequests().antMatchers("/users/current").authenticated();

        // for CUSTOMERS
        http.authorizeRequests().antMatchers("/customers/search").hasAuthority("ROLE_CUSTOMER");

        http.authorizeRequests().antMatchers("/customers/**/accounts").hasAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers("/accounts/**/operations").hasAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers("/accounts/**/pageOperations").hasAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers("/accounts/credit").hasAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers("/accounts/debit").hasAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers("/accounts/transfer").hasAuthority("ROLE_CUSTOMER");

        // for EMPLOYEES
        http.authorizeRequests().antMatchers("/customers/**").hasAuthority("ROLE_EMPLOYEE");
        http.authorizeRequests().antMatchers("/accounts/**").hasAuthority("ROLE_EMPLOYEE");


        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(new JwtAuthenticationFilter(authenticationManagerBean()));
        http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
