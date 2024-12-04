package com.ghw.maplojbackendjudgeservice.Judge.codesandbox.impl;

import com.ghw.maplojbackendjudgeservice.Judge.codesandbox.codesandbox;
import com.ghw.maplojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.ghw.maplojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.ghw.maplojbackendmodel.model.codesandbox.JudgeInfo;
import com.ghw.maplojbackendmodel.model.enums.JudgeInfoEnum;
import com.ghw.maplojbackendmodel.model.enums.QuestionSubmitEnum;

import java.util.List;

/**
 * 默认实现代码沙箱
 */
public class Examplecodesandboximpl implements codesandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
         List<String> inputList = executeCodeRequest.getInputList();
        System.out.println("Executing Example Code");
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(1000L);
        judgeInfo.setMemory(1000L);
        judgeInfo.setMessage(JudgeInfoEnum.ACCEPTED.getValue());
        return executeCodeResponse;
    }
}