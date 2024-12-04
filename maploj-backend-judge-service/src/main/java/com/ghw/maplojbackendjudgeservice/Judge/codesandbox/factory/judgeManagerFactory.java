package com.ghw.maplojbackendjudgeservice.Judge.codesandbox.factory;


import com.ghw.maplojbackendjudgeservice.Judge.JudgeManagerEnum;
import com.ghw.maplojbackendjudgeservice.Judge.strategy.JudgeContext;
import com.ghw.maplojbackendmodel.model.codesandbox.JudgeInfo;
import com.ghw.maplojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 选择判题策略
 */
@Service
public class judgeManagerFactory {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    public JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        language = Optional.ofNullable(language).orElse("cpp");
        return JudgeManagerEnum.getJudgeInfoByLanguage(language, judgeContext);
    }
}
