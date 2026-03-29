package com.ccksy.loan.common.security;

public record UserTokenClaims(Long userId, String username) {}
