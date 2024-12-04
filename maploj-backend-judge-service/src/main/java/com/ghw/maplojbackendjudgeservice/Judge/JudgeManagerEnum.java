package com.ghw.maplojbackendjudgeservice.Judge;

import com.ghw.maplojbackendcommon.common.ErrorCode;
import com.ghw.maplojbackendcommon.exception.BusinessException;
import com.ghw.maplojbackendjudgeservice.Judge.codesandbox.JudgeManagerFactory;
import com.ghw.maplojbackendjudgeservice.Judge.strategy.*;
import com.ghw.maplojbackendmodel.model.codesandbox.JudgeInfo;

public enum JudgeManagerEnum implements JudgeManagerFactory {

    cpp {
        @Override
        public JudgeInfo doJudge(JudgeContext judgeContext) {
            return new DefaultJudgeStrategy().doJudge(judgeContext);
        }
    },

    java {
        @Override
        public JudgeInfo doJudge(JudgeContext judgeContext) {
            return new JavaLanguageJudgeStrategy().doJudge(judgeContext);
        }
    },

    python {
        @Override
        public JudgeInfo doJudge(JudgeContext judgeContext) {
            return new PythonLanguageJudgeStrategy().doJudge(judgeContext);
        }
    },

    go {
        @Override
        public JudgeInfo doJudge(JudgeContext judgeContext) {
            return new GoLangLanguageJudgeStrategy().doJudge(judgeContext);
        }
    };
    JudgeManagerEnum() {}

    public static JudgeInfo getJudgeInfoByLanguage(String language, JudgeContext judgeContext) {
        if(language.equals("cpp")) {
            return JudgeManagerEnum.cpp.doJudge(judgeContext);
        }
        if(language.equals("java")) {
            return JudgeManagerEnum.java.doJudge(judgeContext);
        }
        if(language.equals("python")) {
            return JudgeManagerEnum.python.doJudge(judgeContext);
        }
        if(language.equals("go")) {
            return JudgeManagerEnum.go.doJudge(judgeContext);
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR, "语言不存在");
    }
}
