package com.hansung.adhd.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MQTT_Message_Logs")
public class MqttMessageLogs {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column(length = 255)
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(length = 10)
    private String direction;

    @Column(name = "qos_level")
    private Integer qosLevel;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}