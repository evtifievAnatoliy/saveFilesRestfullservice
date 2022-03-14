package com.quantori.restfullservice.models;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignInDto {

    @NotNull
    private String username;

    @NotNull
    private String password;

}
