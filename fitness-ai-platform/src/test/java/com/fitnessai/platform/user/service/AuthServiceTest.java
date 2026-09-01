package com.fitnessai.platform.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.fitnessai.platform.common.exception.BusinessException;
import com.fitnessai.platform.security.JwtService;
import com.fitnessai.platform.user.entity.User;
import com.fitnessai.platform.user.mapper.RoleMapper;
import com.fitnessai.platform.user.mapper.UserMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {
    private UserMapper userMapper;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userMapper = mock(UserMapper.class);
        authService = new AuthService(userMapper, mock(RoleMapper.class), mock(PasswordEncoder.class),
                mock(JwtService.class), mock(JdbcTemplate.class));
    }

    @Test
    void returnsCurrentUserFromAuthenticatedUsername() {
        User user = user(9007199254740993L, 1);
        when(userMapper.selectOne(AuthServiceTest.<Wrapper<User>>anyWrapper())).thenReturn(user);
        when(userMapper.selectRoleCodes(user.getId())).thenReturn(List.of("MEMBER"));

        var result = authService.currentUser("member");

        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.username()).isEqualTo("member");
        assertThat(result.roles()).containsExactly("MEMBER");
    }

    @Test
    void rejectsMissingOrDisabledAuthenticatedUser() {
        when(userMapper.selectOne(AuthServiceTest.<Wrapper<User>>anyWrapper())).thenReturn(null);
        assertThatThrownBy(() -> authService.currentUser("missing"))
                .isInstanceOf(BusinessException.class);

        when(userMapper.selectOne(AuthServiceTest.<Wrapper<User>>anyWrapper())).thenReturn(user(1L, 0));
        assertThatThrownBy(() -> authService.currentUser("disabled"))
                .isInstanceOf(BusinessException.class);
    }

    private static User user(long id, int status) {
        User user = new User();
        user.setId(id);
        user.setUsername("member");
        user.setNickname("Member");
        user.setEmail("member@example.com");
        user.setStatus(status);
        return user;
    }

    private static <T> T anyWrapper() {
        return any();
    }
}
