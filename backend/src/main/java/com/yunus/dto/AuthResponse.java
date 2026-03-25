package com.yunus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {


    private String username;
    private String message;
    private String token;
    private java.util.Set<String> roles;
    private Long id;
}
