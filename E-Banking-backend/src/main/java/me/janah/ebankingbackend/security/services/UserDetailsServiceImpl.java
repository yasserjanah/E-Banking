package me.janah.ebankingbackend.security.services;

import lombok.AllArgsConstructor;
import me.janah.ebankingbackend.entities.Customer;
import me.janah.ebankingbackend.repositories.CustomerRepository;
import me.janah.ebankingbackend.security.entities.AppUser;
import me.janah.ebankingbackend.security.repositories.AppUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private CustomerRepository customerRepository;
    private AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Customer customer = customerRepository.findByEmail(username);
        //if (customer == null) {
          //  throw new UsernameNotFoundException("User not found");
        //}
        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("User not found");
        }

        Collection<GrantedAuthority> authorities = appUser.getAppRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());

        System.out.println("********************************************");
        System.out.println("User found: " + appUser.getUsername());
        System.out.println("authorities: " + authorities);
        System.out.println("********************************************");

        return new User(appUser.getUsername(), appUser.getPassword(), authorities);
    }
}

