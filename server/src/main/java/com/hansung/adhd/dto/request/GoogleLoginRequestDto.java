package com.hansung.adhd.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleLoginRequestDto {
    @NotBlank(message = "ID Token은 필수입니다.")
    private String idToken;
}