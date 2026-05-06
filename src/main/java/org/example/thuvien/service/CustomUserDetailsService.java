package org.example.thuvien.service;

import org.example.thuvien.model.User;
import org.example.thuvien.repository.UserRepository;
import org.example.thuvien.config.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Spring sẽ chỉ bốc đúng user cần tìm, không bị đụng trúng các user bị NULL khác
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));

        return new CustomUserDetails(user);
    }
}