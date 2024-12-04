package com.ghw.maplojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ghw.maplojbackendmodel.model.entity.QuestionComment;

/**
* @author lenovo
* @description 针对表【question_comment(题目评论表)】的数据库操作Service
* @createDate 2024-11-24 10:34:38
*/
public interface QuestionCommentService extends IService<QuestionComment> {

    /**
     * 获取查询条件
     *
     * @param questionId
     * @return
     */
    QueryWrapper<QuestionComment> getQueryCommentWrapper(long questionId);
}
