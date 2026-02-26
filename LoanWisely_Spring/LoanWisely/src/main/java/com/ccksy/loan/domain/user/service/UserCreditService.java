package com.ccksy.loan.domain.user.service;

import com.ccksy.loan.domain.user.dto.request.UserCreditLv1Request;
import com.ccksy.loan.domain.user.dto.request.UserCreditLv2Request;
import com.ccksy.loan.domain.user.dto.request.UserCreditLv3Request;
import com.ccksy.loan.domain.user.dto.response.UserCreditLv1Response;
import com.ccksy.loan.domain.user.dto.response.UserCreditLv2Response;
import com.ccksy.loan.domain.user.dto.response.UserCreditLv3Response;

public interface UserCreditService {

    UserCreditLv1Response upsertLv1(UserCreditLv1Request request);

    UserCreditLv2Response upsertLv2(UserCreditLv2Request request);

    UserCreditLv3Response upsertLv3(UserCreditLv3Request request);
}
