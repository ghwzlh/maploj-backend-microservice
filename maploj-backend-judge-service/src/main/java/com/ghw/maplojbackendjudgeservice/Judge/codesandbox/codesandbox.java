package com.ghw.maplojbackendjudgeservice.Judge.codesandbox;

import com.ghw.maplojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.ghw.maplojbackendmodel.model.codesandbox.ExecuteCodeResponse;

public interface codesandbox {

    /**
     * 执行代码
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
