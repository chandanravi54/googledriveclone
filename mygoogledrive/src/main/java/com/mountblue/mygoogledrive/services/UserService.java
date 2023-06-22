package com.mountblue.mygoogledrive.services;

import com.mountblue.mygoogledrive.entities.User;
import com.mountblue.mygoogledrive.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public String createUser(User user, String confirmPassword) {
        if (userRepository.findByEmailId(user.getEmailId()).isEmpty()) {
            if (user.getPassword().equals(confirmPassword)) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                return "created";
            } else {
                return "Confirm Password Not Matched !!!";
            }
        } else {
            return "Email Already Exist !!!";
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailId(username).get();
        if(user == null){
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        GrantedAuthority authority =new SimpleGrantedAuthority(user.getEmailId());
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
