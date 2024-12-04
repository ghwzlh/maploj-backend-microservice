package com.ghw.maplojbackendjudgeservice.Judge.strategy;

import cn.hutool.json.JSONUtil;
import com.ghw.maplojbackendcommon.constant.CommonConstant;
import com.ghw.maplojbackendmodel.model.codesandbox.JudgeInfo;
import com.ghw.maplojbackendmodel.model.dto.question.JudgeCase;
import com.ghw.maplojbackendmodel.model.dto.question.JudgeConfig;
import com.ghw.maplojbackendmodel.model.entity.Question;
import com.ghw.maplojbackendmodel.model.enums.JudgeInfoEnum;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * java程序判题策略
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        List<JudgeInfo> judgeInfoList = judgeContext.getJudgeInfo();
        final Long[] maxTime = {0L};
        final Long[] maxSpace = {0L};
        judgeInfoList.forEach(item -> maxTime[0] = Math.max(item.getTime(), maxTime[0]));
        judgeInfoList.forEach(item -> maxSpace[0] = Math.max(item.getMemory(), maxSpace[0]));
        // 设置返回结果
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setTime(maxTime[0]);
        judgeInfoResponse.setMemory(maxSpace[0]);
        // 4. 根据代码沙箱的返回结果，设置题目运行结果
        JudgeInfoEnum judgeInfoEnum = JudgeInfoEnum.ACCEPTED;
        if(JudgeInfoEnum.SYSTEM_ERROR.getValue().equals(judgeContext.getMessage())){
            judgeInfoEnum = JudgeInfoEnum.SYSTEM_ERROR;
            judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
            return judgeInfoResponse;
        }
        if(JudgeInfoEnum.COMPILED_ERROR.getValue().equals(judgeContext.getMessage())){
            judgeInfoEnum = JudgeInfoEnum.COMPILED_ERROR;
            judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
            return judgeInfoResponse;
        }
        // 校验结果是否正确
        List<String> outputList = judgeContext.getOutputlist();
        List<JudgeCase> list = judgeContext.getList();
        if (outputList.size() != list.size()) {
            judgeInfoEnum = JudgeInfoEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
            return judgeInfoResponse;
        }
        for (int i = 0; i < list.size(); i++) {
            String output = list.get(i).getOutput();
            if (!Objects.equals(output, outputList.get(i))) {
                judgeInfoEnum = JudgeInfoEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
                return judgeInfoResponse;
            }
        }
        // 判断题目限制
        Question question = judgeContext.getQuestion();
        JudgeConfig judgeConfigs = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);
        List<JudgeInfo> collect = judgeInfoList.stream().filter(item ->
                        item.getTime() > judgeConfigs.getTimeLimit() + CommonConstant.JAVA_COST_TIME)
                .collect(Collectors.toList());
        if(!collect.isEmpty()){
            judgeInfoEnum = JudgeInfoEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
            return judgeInfoResponse;
        }

//        List<JudgeInfo> collect1 = judgeInfoList.stream().filter(item ->
//                        item.getMemory() > judgeConfigs.getMemoryLimit() + CommonConstant.JAVA_COST_MEMORY)
//                .collect(Collectors.toList());
//        if(!collect1.isEmpty()){
//            judgeInfoEnum = JudgeInfoEnum.MEMORY_LIMIT_EXCEEDED;
//            judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
//            return judgeInfoResponse;
//        }
        judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
        return judgeInfoResponse;
    }
}
