package com.ghw.maplojbackendmodel.model.dto.questionsubmit;

import com.ghw.maplojbackendcommon.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 创建请求
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {
    /**
     * 题目所用语言
     */
    private String language;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 判题状态: 0 - 待判题，1 - 判题中，2 - 成功， 3 - 失败
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 提交用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}