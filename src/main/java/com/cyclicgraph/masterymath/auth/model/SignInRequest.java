package com.cyclicgraph.masterymath.auth.model;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(@NotBlank String username, @NotBlank String password) {
}
