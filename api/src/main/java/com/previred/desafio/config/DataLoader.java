package com.previred.desafio.config;

import com.previred.desafio.model.TaskStatus;
import com.previred.desafio.model.TaskStatusEnum;
import com.previred.desafio.model.User;
import com.previred.desafio.repository.TaskStatusRepository;
import com.previred.desafio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.user.username}")
    private String userUsername;

    @Value("${app.user.password}")
    private String userPassword;

    @Override
    public void run(String... args) {
        if (taskStatusRepository.count() == 0) {
            taskStatusRepository.saveAll(Arrays.asList(
                    TaskStatus.builder().name(TaskStatusEnum.PENDIENTE.name()).description(TaskStatusEnum.PENDIENTE.getDisplayName()).build(),
                    TaskStatus.builder().name(TaskStatusEnum.EN_PROGRESO.name()).description(TaskStatusEnum.EN_PROGRESO.getDisplayName()).build(),
                    TaskStatus.builder().name(TaskStatusEnum.COMPLETADA.name()).description(TaskStatusEnum.COMPLETADA.getDisplayName()).build(),
                    TaskStatus.builder().name(TaskStatusEnum.BLOQUEADA.name()).description(TaskStatusEnum.BLOQUEADA.getDisplayName()).build()
            ));
        }

        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .email(adminUsername + "@previred.cl")
                    .role("ROLE_ADMIN")
                    .build());

            userRepository.save(User.builder()
                    .username(userUsername)
                    .password(passwordEncoder.encode(userPassword))
                    .email(userUsername + "@previred.cl")
                    .role("ROLE_USER")
                    .build());
        }
    }
}
