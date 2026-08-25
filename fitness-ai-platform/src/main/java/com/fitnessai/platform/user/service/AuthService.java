package com.fitnessai.platform.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitnessai.platform.common.exception.BusinessException;
import com.fitnessai.platform.security.JwtService;
import com.fitnessai.platform.user.dto.LoginRequest;
import com.fitnessai.platform.user.dto.RegisterRequest;
import com.fitnessai.platform.user.entity.Role;
import com.fitnessai.platform.user.entity.User;
import com.fitnessai.platform.user.mapper.RoleMapper;
import com.fitnessai.platform.user.mapper.UserMapper;
import com.fitnessai.platform.user.vo.LoginVO;
import com.fitnessai.platform.user.vo.UserVO;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JdbcTemplate jdbcTemplate;

    public AuthService(UserMapper userMapper, RoleMapper roleMapper, PasswordEncoder passwordEncoder,
                       JwtService jwtService, JdbcTemplate jdbcTemplate) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public UserVO register(RegisterRequest request) {
        if (findByUsername(request.username()) != null) throw new BusinessException(40901, "用户名已存在");
        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setNickname(request.nickname());
        user.setEmail(request.email());
        user.setStatus(1);
        userMapper.insert(user);
        Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getCode, "MEMBER"));
        if (role == null) throw new BusinessException(50001, "默认角色未初始化");
        jdbcTemplate.update("insert into sys_user_role(user_id, role_id) values (?, ?)", user.getId(), role.getId());
        return toVO(user, List.of(role.getCode()));
    }

    public LoginVO login(LoginRequest request) {
        User user = findByUsername(request.username());
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash()))
            throw new BusinessException(40101, "用户名或密码错误");
        if (!Integer.valueOf(1).equals(user.getStatus())) throw new BusinessException(40301, "账号已被禁用");
        List<String> roles = userMapper.selectRoleCodes(user.getId());
        String token = jwtService.createToken(user.getId(), user.getUsername(), roles);
        return new LoginVO(token, "Bearer", jwtService.expirationSeconds(), toVO(user, roles));
    }

    private User findByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    private UserVO toVO(User user, List<String> roles) {
        return new UserVO(user.getId(), user.getUsername(), user.getNickname(), user.getEmail(), roles);
    }
}
