package com.ccksy.loan.domain.recommend.service;

import com.ccksy.loan.domain.recommend.command.RecommendCommand;
import com.ccksy.loan.domain.recommend.command.RecommendCommandHandler;
import com.ccksy.loan.domain.recommend.dto.request.RecommendRequest;
import com.ccksy.loan.domain.recommend.result.response.RecommendResponse;
import org.springframework.stereotype.Service;

@Service
public class RecommendFacadeServiceImpl implements RecommendFacadeService {

    private final RecommendCommandHandler commandHandler;

    public RecommendFacadeServiceImpl(RecommendCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public RecommendResponse execute(RecommendRequest request) {
        RecommendCommand command = new RecommendCommand(request);
        return commandHandler.handle(command);
    }
}
