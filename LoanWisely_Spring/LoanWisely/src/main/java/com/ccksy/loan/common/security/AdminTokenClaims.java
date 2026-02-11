package com.ccksy.loan.common.security;

import java.util.List;

public record AdminTokenClaims(String adminId, List<String> roles) {
}
