package com.ghw.maplojbackendmodel.model.dto.questionsubmit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 创建请求
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSubmitAddRequest implements Serializable {
    /**
     * 题目所用语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}