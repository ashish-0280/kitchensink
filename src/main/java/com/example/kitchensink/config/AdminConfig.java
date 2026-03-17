package com.example.kitchensink.config;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminConfig {

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner runner(MemberRepository repository, PasswordEncoder encoder) {
        return args -> {

            if (repository.findByEmail(adminEmail).isEmpty()) {

                Member admin = new Member();
                admin.setEmail(adminEmail);
                admin.setPassword(encoder.encode(adminPassword));
                admin.setRole("ADMIN");

                repository.save(admin);
            }

        };
    }
}