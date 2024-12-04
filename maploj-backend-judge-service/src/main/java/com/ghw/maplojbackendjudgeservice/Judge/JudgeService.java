package com.ghw.maplojbackendjudgeservice.Judge;

import com.ghw.maplojbackendmodel.model.entity.QuestionSubmit;

/**
 * 判题服务
 */
public interface JudgeService {

    QuestionSubmit doJudge(long questionSubmitId);
}
