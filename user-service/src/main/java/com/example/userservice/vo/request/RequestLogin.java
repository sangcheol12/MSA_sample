package com.example.userservice.vo.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestLogin {
    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "Email not be less than two characters")
    private String email;

    @NotNull(message = "Password cannot be null")
    @Size(min = 2, message = "Passoword must be equals or greater than 8 characters")
    private String pwd;
}
