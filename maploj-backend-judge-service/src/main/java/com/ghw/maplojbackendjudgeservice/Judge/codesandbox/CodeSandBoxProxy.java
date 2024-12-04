package com.ghw.maplojbackendjudgeservice.Judge.codesandbox;

import com.ghw.maplojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.ghw.maplojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeSandBoxProxy implements codesandbox{

    private final codesandbox codesandbox;

    public CodeSandBoxProxy(codesandbox codesandbox) {
        this.codesandbox = codesandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息: {}", executeCodeRequest);
        ExecuteCodeResponse executeCodeResponse = codesandbox.executeCode(executeCodeRequest);
        log.info("代码沙箱返回信息：{}", executeCodeResponse);
        return executeCodeResponse;
    }
}
