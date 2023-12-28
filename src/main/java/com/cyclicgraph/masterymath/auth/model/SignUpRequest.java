package com.cyclicgraph.masterymath.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(@NotBlank String username, @NotBlank @Email String email, @NotBlank String password) {
}
