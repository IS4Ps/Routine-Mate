package com.hansung.adhd.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-path:}")
    private String serviceAccountPath;

    @PostConstruct
    public void initialize() {
        if (!FirebaseApp.getApps().isEmpty()) return;

        if (serviceAccountPath == null || serviceAccountPath.isBlank()) {
            log.info("Firebase 서비스 계정 키가 설정되지 않아 초기화를 건너뜁니다.");
            return;
        }

        String resourcePath = serviceAccountPath.startsWith("classpath:")
                ? serviceAccountPath.substring("classpath:".length())
                : serviceAccountPath;

        try (InputStream serviceAccount =
                     new ClassPathResource(resourcePath).getInputStream()) {

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("Firebase 초기화 완료");
        } catch (IOException e) {
            log.error("Firebase 초기화 실패 - 서비스 계정 키 경로를 확인하세요: {}", e.getMessage());
        }
    }
}
