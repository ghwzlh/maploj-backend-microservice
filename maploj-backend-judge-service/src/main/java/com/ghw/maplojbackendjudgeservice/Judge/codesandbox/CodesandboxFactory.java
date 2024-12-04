package com.ghw.maplojbackendjudgeservice.Judge.codesandbox;

public interface CodesandboxFactory {

    /**
     * 获取不同代码沙箱的实现类
     */
    codesandbox getCodesandbox();
}
