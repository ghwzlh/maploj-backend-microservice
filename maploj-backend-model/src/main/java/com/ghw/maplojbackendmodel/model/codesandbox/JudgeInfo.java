package com.ghw.maplojbackendmodel.model.codesandbox;

import lombok.Data;

/**
 * 题目提交后返回信息
 */
@Data
public class JudgeInfo {

    /**
     * 运行时间 ms
     */
    private Long time;

    /**
     * 运行消耗空间 kb
     */
    private Long memory;

    /**
     * 程序运行信息
     */
    private String message;
}
