package com.ghw.maplojbackendjudgeservice.Judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ghw.maplojbackendcommon.common.ErrorCode;
import com.ghw.maplojbackendcommon.exception.BusinessException;
import com.ghw.maplojbackendjudgeservice.Judge.codesandbox.codesandbox;
import com.ghw.maplojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.ghw.maplojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱
 * 实际调用接口的代码沙箱
 */
public class Remotecodesandboximpl implements codesandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("Executing Remote codesandbox");
        String url = "http://127.0.0.1:8091/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String body = HttpUtil.createPost(url)
                .body(json)
                .execute()
                .body();
        if(StringUtils.isBlank(body)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        System.out.println(body);
        return JSONUtil.toBean(body, ExecuteCodeResponse.class);
    }
}