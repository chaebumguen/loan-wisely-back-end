package com.ccksy.loan.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCreditLv1Request {

    private Long userId;

    @NotNull
    @PositiveOrZero
    private Integer age;

    @NotNull
    @PositiveOrZero
    private Long incomeYear;

    @NotBlank
    private String gender;
}
