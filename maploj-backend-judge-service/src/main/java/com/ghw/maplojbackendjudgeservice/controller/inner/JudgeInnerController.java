package com.ghw.maplojbackendjudgeservice.controller.inner;

import com.ghw.maplojbackendjudgeservice.Judge.JudgeServiceimpl;
import com.ghw.maplojbackendmodel.model.entity.QuestionSubmit;
import com.ghw.maplojbackendserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("inner")
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private JudgeServiceimpl judgeServiceimpl;

    @PostMapping("/do")
    @Override
    public QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId){
        return judgeServiceimpl.doJudge(questionSubmitId);
    }
}
