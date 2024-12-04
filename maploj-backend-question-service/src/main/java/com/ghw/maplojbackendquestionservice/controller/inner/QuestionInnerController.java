package com.ghw.maplojbackendquestionservice.controller.inner;

import com.ghw.maplojbackendmodel.model.entity.Question;
import com.ghw.maplojbackendmodel.model.entity.QuestionSubmit;
import com.ghw.maplojbackendquestionservice.service.QuestionService;
import com.ghw.maplojbackendquestionservice.service.QuestionSubmitService;
import com.ghw.maplojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("inner")
public class QuestionInnerController implements QuestionFeignClient {


    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @GetMapping("/get/id")
    @Override
    public Question getQuestionById(@RequestParam("questionId") long questionId){
        return questionService.getById(questionId);
    }

    @GetMapping("/question_submit/get/id")
    @Override
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId){
        return questionSubmitService.getById(questionSubmitId);
    }

    @PostMapping("/question_submit/update")
    @Override
    public boolean updateById(@RequestBody QuestionSubmit questionSubmit){
        return questionSubmitService.updateById(questionSubmit);
    }
}
