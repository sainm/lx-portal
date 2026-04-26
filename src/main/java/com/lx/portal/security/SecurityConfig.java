package com.lx.portal.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**", "/api/admin/**").authenticated()
                        .anyRequest().permitAll())
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(AdminUserRepository repository) {
        return username -> repository.findByUsername(username)
                .filter(AdminUser::isEnabled)
                .map(user -> User.withUsername(user.getUsername())
                        .password(user.getPasswordHash())
                        .roles("ADMIN")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("后台账号不存在"));
    }

    @Bean
    CommandLineRunner adminUserInitializer(AdminUserRepository repository, PasswordEncoder passwordEncoder,
            AdminProperties properties) {
        return args -> repository.findByUsername(properties.username()).orElseGet(() -> {
            AdminUser user = new AdminUser();
            user.setUsername(properties.username());
            user.setPasswordHash(passwordEncoder.encode(properties.password()));
            user.setDisplayName("系统管理员");
            user.setEnabled(true);
            return repository.save(user);
        });
    }

    @Bean
    @ConfigurationProperties(prefix = "app.admin")
    AdminProperties adminProperties() {
        return new AdminProperties("admin", "admin123");
    }

    public record AdminProperties(String username, String password) {
    }
}

