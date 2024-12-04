package com.ghw.maplojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ghw.maplojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.ghw.maplojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.ghw.maplojbackendmodel.model.entity.QuestionSubmit;
import com.ghw.maplojbackendmodel.model.entity.User;
import com.ghw.maplojbackendmodel.model.vo.QuestionSubmitVO;

/**
* @author lenovo
* @description 针对表【question_submit(题目提交表)】的数据库操作Service
* @createDate 2024-11-12 14:48:33
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 点赞
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return 题目提交记录表的id
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQuerySubmitWrapper(QuestionSubmitQueryRequest questionQueryRequest);


    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);
}
