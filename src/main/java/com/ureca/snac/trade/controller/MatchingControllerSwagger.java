package com.ureca.snac.trade.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "실시간 매칭", description = "실시간 매칭 거래 기능")
@SecurityRequirement(name = "Authorization")
public interface MatchingControllerSwagger {
}
