package com.project.bedemo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class LoginServiceTest {

    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loginService = new LoginService();
    }

    @Test
    void returnsTokenWhenEmailAndPasswordAreValid() {
        Optional<String> result = loginService.login("user@example.com", LoginService.PASSWORD);

        assertThat(result).isPresent();
        assertThat(result.get()).isNotBlank();
        assertThat(loginService.isValidToken(result.get())).isTrue();
    }

    @Test
    void returnsEmptyWhenCredentialsAreInvalid() {
        assertThat(loginService.login("user@example.com", "wrong")).isEmpty();
        assertThat(loginService.login(null, LoginService.PASSWORD)).isEmpty();
        assertThat(loginService.login("", LoginService.PASSWORD)).isEmpty();
    }

    @Test
    void invalidateTokenRemovesTokenSoItIsNoLongerValid() {
        String token = loginService.login("user@example.com", LoginService.PASSWORD).orElseThrow();
        boolean removed = loginService.invalidateToken(token);

        assertThat(removed).isTrue();
        assertThat(loginService.isValidToken(token)).isFalse();
    }
}
