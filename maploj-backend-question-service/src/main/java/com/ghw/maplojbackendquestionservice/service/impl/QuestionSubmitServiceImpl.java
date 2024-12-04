package com.ghw.maplojbackendquestionservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghw.maplojbackendcommon.common.ErrorCode;
import com.ghw.maplojbackendcommon.constant.CommonConstant;
import com.ghw.maplojbackendcommon.constant.MessageQueueConstant;
import com.ghw.maplojbackendcommon.exception.BusinessException;
import com.ghw.maplojbackendcommon.utils.SqlUtils;
import com.ghw.maplojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.ghw.maplojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.ghw.maplojbackendmodel.model.entity.Question;
import com.ghw.maplojbackendmodel.model.entity.QuestionSubmit;
import com.ghw.maplojbackendmodel.model.entity.User;
import com.ghw.maplojbackendmodel.model.enums.JudgeInfoEnum;
import com.ghw.maplojbackendmodel.model.enums.QuestionSubmitEnum;
import com.ghw.maplojbackendmodel.model.enums.QuestionSubmitLanuageEnum;
import com.ghw.maplojbackendmodel.model.vo.QuestionSubmitVO;
import com.ghw.maplojbackendmodel.model.vo.UserVO;
import com.ghw.maplojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.ghw.maplojbackendquestionservice.message.MessageProvider;
import com.ghw.maplojbackendquestionservice.service.QuestionService;
import com.ghw.maplojbackendquestionservice.service.QuestionSubmitService;
import com.ghw.maplojbackendserviceclient.service.JudgeFeignClient;
import com.ghw.maplojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author lenovo
* @description 针对表【question_submit(题目提交表)】的数据库操作Service实现
* @createDate 2024-11-12 14:48:33
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private  QuestionService questionService;

    @Resource
    private JudgeFeignClient judgeFeignClient;

    @Resource
    private MessageProvider messageProvider;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 判断用户语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanuageEnum enumByValue = QuestionSubmitLanuageEnum.getEnumByValue(language);
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(loginUser.getId());
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // 设置判题状态
        questionSubmit.setStatus(QuestionSubmitEnum.WAITTING.getValue());
        // 设置题目运行信息
        questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(JudgeInfoEnum.WAITTING.getValue()));
        boolean saved = this.save(questionSubmit);
        if(!saved){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据载入失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        // 执行判题服务
        messageProvider.sendMessage(MessageQueueConstant.EXCHANGE_NAME, "my_question_queue", String.valueOf(questionSubmitId));
//        CompletableFuture.runAsync(() -> judgeFeignClient.doJudge(questionSubmitId));
        return questionSubmitId;
    }

    /**
     * 获取查询包装类
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQuerySubmitWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String userName = questionSubmitQueryRequest.getUserName();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.eq(QuestionSubmitEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取查询结果封装类
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 关联查询用户信息
        // 1. 关联查询用户信息
        Long userId = questionSubmit.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userFeignClient.getById(userId);
        }
        UserVO userVO = userFeignClient.getUserVO(user);
        questionSubmitVO.setUserId(userVO.getId());
        questionSubmitVO.setUserName(userVO.getUserName());
        questionSubmitVO.setUserAvatar(userVO.getUserAvatar());
        // 保证非管理员看不到别人的代码
        if(!Objects.equals(loginUser.getId(), userId) && !userFeignClient.isAdmin(loginUser)){
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    /**
     * 分页获取查询结果封装类
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionSubmitList.stream().map(QuestionSubmit::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> {
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
            Long userId = questionSubmit.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            UserVO userVO = userFeignClient.getUserVO(user);
            questionSubmitVO.setUserId(userVO.getId());
            questionSubmitVO.setUserName(userVO.getUserName());
            questionSubmitVO.setUserAvatar(userVO.getUserAvatar());
            if(!Objects.equals(loginUser.getId(), userId) && userFeignClient.isAdmin(loginUser)){
                questionSubmitVO.setCode(null);
            }
            return questionSubmitVO;
        }).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}




