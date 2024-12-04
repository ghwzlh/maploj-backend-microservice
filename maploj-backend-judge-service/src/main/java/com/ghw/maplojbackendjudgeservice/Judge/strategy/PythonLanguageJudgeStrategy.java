package com.ghw.maplojbackendjudgeservice.Judge.strategy;

import com.ghw.maplojbackendmodel.model.codesandbox.JudgeInfo;

/**
 * python程序判题策略
 */
@Deprecated
public class PythonLanguageJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
//        List<JudgeInfo> judgeInfo = judgeContext.getJudgeInfo();
//        Long time = judgeInfo.getTime();
//        Long memory = judgeInfo.getMemory();
//        // 设置返回结果
//        JudgeInfo judgeInfoResponse = new JudgeInfo();
//        judgeInfoResponse.setTime(time);
//        judgeInfoResponse.setMemory(memory);
//        // 4. 根据代码沙箱的返回结果，设置题目运行结果
//        JudgeInfoEnum judgeInfoEnum = JudgeInfoEnum.ACCEPTED;
//        // 校验结果是否正确
//        List<String> outputList = judgeContext.getOutputlist();
//        List<JudgeCase> list = judgeContext.getList();
//        if (outputList.size() != list.size()) {
//            judgeInfoEnum = JudgeInfoEnum.WRONG_ANSWER;
//            judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
//            return judgeInfoResponse;
//        }
//        for (int i = 0; i < list.size(); i++) {
//            String output = list.get(i).getOutput();
//            if (!Objects.equals(output, outputList.get(i))) {
//                judgeInfoEnum = JudgeInfoEnum.WRONG_ANSWER;
//                judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
//                return judgeInfoResponse;
//            }
//        }
//        // 判断题目限制
//        Question question = judgeContext.getQuestion();
//        JudgeConfig judgeConfigs = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);
//        if(judgeConfigs.getTimeLimit() + CommonConstant.PYTHON_COST_TIME < time) {
//            judgeInfoEnum = JudgeInfoEnum.TIME_LIMIT_EXCEEDED;
//            judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
//            return judgeInfoResponse;
//        }
//        if(judgeConfigs.getMemoryLimit() + CommonConstant.PYTHON_COST_MEMORY < memory) {
//            judgeInfoEnum = JudgeInfoEnum.MEMORY_LIMIT_EXCEEDED;
//            judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
//            return judgeInfoResponse;
//        }
//        judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
//        return judgeInfoResponse;
        return null;
    }
}
