package com.ghw.maplojbackendjudgeservice.Judge.codesandbox.impl;

import com.ghw.maplojbackendjudgeservice.Judge.codesandbox.codesandbox;
import com.ghw.maplojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.ghw.maplojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 第三方代码沙箱
 */
public class thirdpartcodesandboximpl implements codesandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("Third part codesandbox");
        return null;
    }
}
