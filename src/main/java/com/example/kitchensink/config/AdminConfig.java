package com.example.kitchensink.config;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminConfig {

    @Bean
    CommandLineRunner runner(MemberRepository repository, PasswordEncoder encoder) {
        return args -> {
            if (repository.findByEmail("admin@gmail.com").isEmpty()) {
                Member admin = new Member();
                admin.setEmail("admin@gmail.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ADMIN");
                repository.save(admin);
            }
        };
    }
}
