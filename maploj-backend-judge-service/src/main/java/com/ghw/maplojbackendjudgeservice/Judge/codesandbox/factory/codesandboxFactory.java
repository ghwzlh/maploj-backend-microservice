package com.ghw.maplojbackendjudgeservice.Judge.codesandbox.factory;

import com.ghw.maplojbackendcommon.common.ErrorCode;
import com.ghw.maplojbackendcommon.exception.BusinessException;
import com.ghw.maplojbackendjudgeservice.Judge.CodesandboxFactoryEnum;
import com.ghw.maplojbackendjudgeservice.Judge.codesandbox.codesandbox;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class codesandboxFactory {

    public static codesandbox newInstance(String type) {
        List<String> list = Arrays.asList("EXAMPLE", "REMOTE", "THIRD_PART");
        if(StringUtils.isBlank(type) ||  !StringUtils.containsAny(type, list.toString())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return CodesandboxFactoryEnum.valueOf(type).getCodesandbox();
    }
}