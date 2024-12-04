package com.ghw.maplojbackendjudgeservice.Judge.strategy;

import com.ghw.maplojbackendmodel.model.codesandbox.JudgeInfo;
import com.ghw.maplojbackendmodel.model.dto.question.JudgeCase;
import com.ghw.maplojbackendmodel.model.entity.Question;
import com.ghw.maplojbackendmodel.model.entity.QuestionSubmit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 上下文（用于在策略模式中传入的参数）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JudgeContext {

    private List<JudgeInfo> judgeInfo;

    private List<String> inputlist;

    private List<String> outputlist;

    private List<JudgeCase> list;

    private QuestionSubmit questionSubmit;

    private Question question;

    private String message;
}
