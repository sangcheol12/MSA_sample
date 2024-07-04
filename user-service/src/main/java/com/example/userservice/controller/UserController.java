package com.example.userservice.controller;

import com.example.userservice.entity.UserEntity;
import com.example.userservice.security.JwtTokenProvider;
import com.example.userservice.vo.request.RequestLogin;
import com.example.userservice.vo.request.RequestUser;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.response.ResponseUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Environment env;

    @GetMapping("/health_check")
    public String status(HttpServletRequest request) {
        return String.format("It's working in User Service"
                + ", port(local.server.port)=" + env.getProperty("local.server.port")
                + ", port(server.port)=" + env.getProperty("server.port")
                + ", ip(gateway.ip)=" + env.getProperty("gateway.ip")
                + ", token expiration hours=" + env.getProperty("token.expiration-hour"));
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String createUser(@RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.CreateUser(userDto);

        return userDto.getUserId();
    }

    @ResponseBody
    @PostMapping("/signin")
    @ResponseStatus(value = HttpStatus.OK)
    public String loginUser(@RequestBody RequestLogin requestLogin, HttpServletResponse response) {
        UserDto userDto = userService.matchAccount(requestLogin.getEmail(), requestLogin.getPwd());

        String accessToken = jwtTokenProvider.createToken(userDto.getUserId(), "ROLE_USER");
        response.setHeader("Authorization", String.format("Bearer %s", accessToken));

        return userDto.getUserId();
    }

    @GetMapping(value = "/users")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseUser> getUsers() {
        Iterable<UserEntity> userList = userService.getUserByAll();

        ModelMapper mapper = new ModelMapper();

        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {
            result.add(mapper.map(v, ResponseUser.class));
        });

        return result;
    }

    @GetMapping(value = "/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseUser getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);

        return new ModelMapper().map(userDto, ResponseUser.class);
    }
}
