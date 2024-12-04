package com.ghw.maplojbackendmodel.model.dto.question;

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
public class QuestionCommentQueryRequest implements Serializable {

    /**
     * 题目id
     */
    private Integer questionId;

    private static final long serialVersionUID = 1L;
}