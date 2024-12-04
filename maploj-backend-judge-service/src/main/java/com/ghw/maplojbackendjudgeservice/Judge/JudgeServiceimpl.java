package com.ghw.maplojbackendjudgeservice.Judge;

import cn.hutool.json.JSONUtil;
import com.ghw.maplojbackendcommon.common.ErrorCode;
import com.ghw.maplojbackendcommon.exception.BusinessException;
import com.ghw.maplojbackendjudgeservice.Judge.codesandbox.CodeSandBoxProxy;
import com.ghw.maplojbackendjudgeservice.Judge.codesandbox.codesandbox;
import com.ghw.maplojbackendjudgeservice.Judge.codesandbox.factory.codesandboxFactory;
import com.ghw.maplojbackendjudgeservice.Judge.strategy.JudgeContext;
import com.ghw.maplojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.ghw.maplojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.ghw.maplojbackendmodel.model.codesandbox.JudgeInfo;
import com.ghw.maplojbackendmodel.model.dto.question.JudgeCase;
import com.ghw.maplojbackendmodel.model.entity.Question;
import com.ghw.maplojbackendmodel.model.entity.QuestionSubmit;
import com.ghw.maplojbackendmodel.model.enums.QuestionSubmitEnum;
import com.ghw.maplojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JudgeServiceimpl implements JudgeService{

    @Value("${codesandbox.type}")
    private String type;

    @Resource
    private QuestionFeignClient questionFeignClient;


    @Resource
    private com.ghw.maplojbackendjudgeservice.Judge.codesandbox.factory.judgeManagerFactory judgeManagerFactory;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1. 根据传入的提交题目id获取到题目的提交信息
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交题目信息不存在");
        }
        Question question = questionFeignClient.getQuestionById(questionSubmit.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目信息不存在");
        }
        // 2. 判断题目判题状态，如果题目判题状态不是待判题，就不用进行判题
        if (!Objects.equals(questionSubmit.getStatus(), QuestionSubmitEnum.WAITTING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已在判题，请勿重复提交");
        }
        questionSubmit.setStatus(QuestionSubmitEnum.RUNNING.getValue());
        boolean b = questionFeignClient.updateById(questionSubmit);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 3. 调用代码沙箱，运行题目
        codesandbox codesandbox = codesandboxFactory.newInstance(type);
        codesandbox = new CodeSandBoxProxy(codesandbox);
        String code = questionSubmit.getCode();
        String language = questionSubmit.getLanguage();
        String judgeCase = question.getJudgeCase();
        List<JudgeCase> list = JSONUtil.toList(judgeCase, JudgeCase.class);
        List<String> inputList = list.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build(); // 链式调用
        ExecuteCodeResponse executeCodeResponse = codesandbox.executeCode(executeCodeRequest);
        // 4. 根据代码沙箱的返回结果，设置题目运行结果
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setInputlist(inputList);
        judgeContext.setOutputlist(executeCodeResponse.getOutputList());
        judgeContext.setList(list);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setMessage(executeCodeResponse.getMessage());
        JudgeInfo judgeInfoResponse = judgeManagerFactory.doJudge(judgeContext);
        // 修改数据库
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmit.getId());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoResponse));
        questionSubmitUpdate.setStatus(QuestionSubmitEnum.SUCCEED.getValue());
        boolean b1 = questionFeignClient.updateById(questionSubmitUpdate);
        if(!b1){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题目信息失败");
        }
        return questionFeignClient.getQuestionSubmitById(questionSubmitId);
    }
}