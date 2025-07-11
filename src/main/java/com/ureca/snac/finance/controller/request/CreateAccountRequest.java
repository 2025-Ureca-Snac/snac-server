package com.ureca.snac.finance.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateAccountRequest {

    @NotNull
    private Long bankId;

    @NotBlank
    private String accountNumber;
}
