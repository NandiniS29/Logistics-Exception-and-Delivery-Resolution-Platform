package com.logistics.logisticsbackend;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {

        if (login == null || login.isBlank()) {
            throw new UsernameNotFoundException(
                    "Please enter your name, username, or email."
            );
        }

        String loginValue = login.trim();

        // First, search using the registered username.
        User user = userRepository.findByUsername(loginValue)
                .orElse(null);

        // If not found, search using the registered email.
        if (user == null) {
            user = userRepository.findByEmailIgnoreCase(loginValue)
                    .orElse(null);
        }

        // If not found, search using the registered full name.
        if (user == null) {
            user = userRepository.findByFullNameIgnoreCase(loginValue)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "No account found with this name, username, or email."
                            )
                    );
        }

        // Return the stored password hash and registered role.
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}