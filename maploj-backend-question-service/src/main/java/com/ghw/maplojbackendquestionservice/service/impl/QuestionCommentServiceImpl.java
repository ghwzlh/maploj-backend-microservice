package com.ghw.maplojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghw.maplojbackendmodel.model.entity.QuestionComment;
import com.ghw.maplojbackendquestionservice.mapper.QuestionCommentMapper;
import com.ghw.maplojbackendquestionservice.service.QuestionCommentService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
* @author lenovo
* @description 针对表【question_comment(题目评论表)】的数据库操作Service实现
* @createDate 2024-11-24 10:34:38
*/
@Service
public class QuestionCommentServiceImpl extends ServiceImpl<QuestionCommentMapper, QuestionComment>
    implements QuestionCommentService {

    @Override
    public QueryWrapper<QuestionComment> getQueryCommentWrapper(long questionId) {
        QueryWrapper<QuestionComment> queryWrapper = new QueryWrapper<>();
        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq("isDelete", 0);
        return queryWrapper;
    }
}




