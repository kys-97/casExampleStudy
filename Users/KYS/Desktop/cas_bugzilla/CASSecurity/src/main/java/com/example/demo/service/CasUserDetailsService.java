package com.example.demo.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.data.entity.CasUser;
import com.example.demo.data.repository.UserRepository;

@Service
public class CasUserDetailsService implements UserDetailsService {

	@Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Get the user from the database.
        CasUser casUser = getUserFromDatabase(email);

        // Create a UserDetails object.
        UserDetails userDetails = new User(
            casUser.getEmail(),
            casUser.getPassword(),
           Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        return userDetails;
    }

    private CasUser getUserFromDatabase(String email) {
       return userRepository.findByEmail(email);
    }
}
