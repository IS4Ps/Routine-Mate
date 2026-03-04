package com.hansung.adhd.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobResponseDto {
    private Long id;
    private String jobCode;
    private String jobName;
    private String description;
    private Integer baseStrength;
    private Integer baseIntelligence;
    private Integer baseCreativity;
}