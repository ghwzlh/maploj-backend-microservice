package com.ghw.maplojbackendjudgeservice.Judge.codesandbox;

import com.ghw.maplojbackendjudgeservice.Judge.strategy.JudgeContext;
import com.ghw.maplojbackendmodel.model.codesandbox.JudgeInfo;

public interface JudgeManagerFactory {

    JudgeInfo doJudge(JudgeContext judgeContext);
}