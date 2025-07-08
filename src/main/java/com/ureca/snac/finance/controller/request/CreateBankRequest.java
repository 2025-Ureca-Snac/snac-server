package com.ureca.snac.finance.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateBankRequest {
    @NotBlank
    private String name;
}
