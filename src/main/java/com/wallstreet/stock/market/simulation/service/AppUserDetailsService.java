package com.wallstreet.stock.market.simulation.service;

import com.wallstreet.stock.market.simulation.model.User;
import com.wallstreet.stock.market.simulation.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Loads application user from the database and maps it to Spring Security UserDetails.
 * Uses email as the username.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load a user by email (treated as username).
     *
     * @param emailOrUsername email of user
     * @return UserDetails for Spring Security
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(emailOrUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + emailOrUsername));

        return mapToUserDetails(user);
    }

    private UserDetails mapToUserDetails(User user) {
        Collection<? extends GrantedAuthority> authorities = toAuthorities(user.getRole());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash()) // ensure this column stores BCrypt hashed password
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false) // change if you add an 'enabled' flag to User
                .build();
    }

    /**
     * Convert role string to GrantedAuthority list.
     * Accepts single role like "USER" or multiple roles comma-separated like "USER,ADMIN".
     * Ensures prefix "ROLE_" is present for each authority.
     */
    private List<GrantedAuthority> toAuthorities(String roleString) {
        if (roleString == null || roleString.isBlank()) return List.of();

        return Arrays.stream(roleString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.startsWith("ROLE_") ? s : "ROLE_" + s)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
