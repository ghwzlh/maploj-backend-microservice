package com.ghw.maplojbackendquestionservice.controller;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghw.maplojbackendcommon.annotation.AuthCheck;
import com.ghw.maplojbackendcommon.common.BaseResponse;
import com.ghw.maplojbackendcommon.common.DeleteRequest;
import com.ghw.maplojbackendcommon.common.ErrorCode;
import com.ghw.maplojbackendcommon.common.ResultUtils;
import com.ghw.maplojbackendcommon.constant.UserConstant;
import com.ghw.maplojbackendcommon.exception.BusinessException;
import com.ghw.maplojbackendcommon.exception.ThrowUtils;
import com.ghw.maplojbackendmodel.model.dto.question.*;
import com.ghw.maplojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.ghw.maplojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.ghw.maplojbackendmodel.model.entity.*;
import com.ghw.maplojbackendmodel.model.vo.QuestionSubmitVO;
import com.ghw.maplojbackendmodel.model.vo.QuestionVO;
import com.ghw.maplojbackendquestionservice.service.QuestionCommentService;
import com.ghw.maplojbackendquestionservice.service.QuestionService;
import com.ghw.maplojbackendquestionservice.utils.RedisUtils;
import com.ghw.maplojbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 题目接口
 *
 */
@RestController
@RequestMapping("/")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private com.ghw.maplojbackendquestionservice.service.QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionCommentService questionCommentService;

    @Resource
    private RedissonClient redisson;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisUtils redisUtils;

    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        question.setJudgeConfig(JSONUtil.toJsonStr(questionAddRequest.getJudgeConfig()));
        question.setJudgeCase(JSONUtil.toJsonStr(questionAddRequest.getJudgeCase()));
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        questionService.validQuestion(question, true);
        User loginUser = userFeignClient.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        // 添加缓存
        HashMap<String, Object> hashMap = redisUtils.getHashMap(question);
        stringRedisTemplate.opsForHash().putAll(String.format("com:ghw:maploj:message:question:%s", newQuestionId), hashMap);
        return ResultUtils.success(newQuestionId);
    }

    @Deprecated
    private HashMap<String, Object> getHashMap(Question question) {
        HashMap<String, Object> hashMap = new HashMap<>();
        Long id = question.getId();
        hashMap.put("id", String.valueOf(id));
        String title = question.getTitle();
        hashMap.put("title", title);
        String content = question.getContent();
        hashMap.put("content", content);
        String tags = question.getTags();
        hashMap.put("tags", tags);
        String answer = question.getAnswer();
        hashMap.put("answer", answer);
        hashMap.put("submitNum", "0");
        hashMap.put("acceptNum", "0");
        String judgeConfig = question.getJudgeConfig();
        hashMap.put("judgeConfig", judgeConfig);
        String judgeCase = question.getJudgeCase();
        hashMap.put("judgeCase", judgeCase);
        hashMap.put("thumbNum", "0");
        hashMap.put("favourNum", "0");
        Long userId = question.getUserId();
        hashMap.put("userId", String.valueOf(userId));
        hashMap.put("createTime", LocalDateTime.now().toString());
        hashMap.put("updateTime", LocalDateTime.now().toString());
        hashMap.put("isDelete", "0");
        return hashMap;
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userFeignClient.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        question.setJudgeConfig(JSONUtil.toJsonStr(questionUpdateRequest.getJudgeConfig()));
        question.setJudgeCase(JSONUtil.toJsonStr(questionUpdateRequest.getJudgeCase()));
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        RLock rLock = redisson.getLock("com:ghw:maploj:update:question");
        try {
            if(rLock.tryLock(0, -1, TimeUnit.SECONDS)){
                // 更新数据库
                boolean result = questionService.updateById(question);
                // 删暖存
                stringRedisTemplate.opsForHash().delete(String.format("com:ghw:maploj:message:question:%s", id));
                return ResultUtils.success(result);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rLock.unlock();
        }
        return ResultUtils.error(false, "系统繁忙，请稍后再试");
    }

    /**
     * 根据 id 获取脱敏后的题目信息
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = null;
        // 先尝试从缓存中获取
        question = redisUtils.getQuestionById(id);
        if(question == null){
            // 缓存不存在
            question = questionService.getById(id);
            // 写入缓存
            if(question != null){
                HashMap<String, Object> hashMap = redisUtils.getHashMap(question);
                stringRedisTemplate.opsForHash().putAll(String.format("com:ghw:maploj:message:question:%s", id), hashMap);
            } else {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 根据 id 获取原始数据
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = null;
        // 先尝试从缓存中获取
        question = redisUtils.getQuestionById(id);
        if(question == null){
            // 缓存不存在
            question = questionService.getById(id);
            // 写入缓存
            if(question != null){
                HashMap<String, Object> hashMap = redisUtils.getHashMap(question);
                stringRedisTemplate.opsForHash().putAll(String.format("com:ghw:maploj:message:question:%s", id), hashMap);
            } else {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
        }
        User loginUser = userFeignClient.getLoginUser(request);
        if(!Objects.equals(loginUser.getId(), question.getUserId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限");
        }
        return ResultUtils.success(question);
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        questionPage.setTotal(questionPage.getRecords().size());
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        question.setJudgeConfig(JSONUtil.toJsonStr(questionEditRequest.getJudgeConfig()));
        question.setJudgeCase(JSONUtil.toJsonStr(questionEditRequest.getJudgeCase()));
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        User loginUser = userFeignClient.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        RLock rLock = redisson.getLock("com:ghw:maploj:edit:question");
        try {
            if(rLock.tryLock(0, -1, TimeUnit.SECONDS)){
                // 更新数据库
                boolean result = questionService.updateById(question);
                // 删暖存
                stringRedisTemplate.opsForHash().delete(String.format("com:ghw:maploj:message:question:%s", id));
                return ResultUtils.success(result);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rLock.unlock();
        }
        return ResultUtils.error(false, "系统繁忙，请稍后再试");

//        boolean result = questionService.updateById(question);
//        return ResultUtils.success(result);
    }

    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/question_submit/do")
    public BaseResponse<Long> doSubmitQuestion(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录后才能做题
        final User loginUser = userFeignClient.getLoginUser(request);
        long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(result);
    }
    // 查询提交信息接口
    // 根据用户id和题目id查询
    // 仅管理员可以看见所有用户提交的代码和答案
    // 一个普通用户只能查看除代码，答案外的信息
    /**
     * 分页获取提交列表（仅管理员，普通用户只能看非答案和代码的公开信息）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/question_submit/list/page")
    // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionQueryRequest, HttpServletRequest request) {
        int current = questionQueryRequest.getCurrent();
        int pageSize = questionQueryRequest.getPageSize();
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, pageSize),
                questionSubmitService.getQuerySubmitWrapper(questionQueryRequest));
        questionSubmitPage.setTotal(questionSubmitPage.getRecords().size());
        User loginUser = userFeignClient.getLoginUser(request);
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }

    @PostMapping("/question/comment")
    public BaseResponse<List<QuestionComment>> listQuestionComment(long questionId, HttpServletRequest request){
        QueryWrapper<QuestionComment> queryCommentWrapper = questionCommentService.getQueryCommentWrapper(questionId);
//        List<QuestionComment> questionComments = questionCommentService.listByIds(Arrays.asList(questionId));
        List<QuestionComment> list = questionCommentService.list(queryCommentWrapper);
        if(list.isEmpty()){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该题没有评论");
        }
        User loginUser = userFeignClient.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "请登录后查看评论");
        }
        return ResultUtils.success(list);
    }

    @Deprecated
    private Question getQuestionById(long id) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(String.format("com:ghw:maploj:message:question:%s", id));
        if(entries.isEmpty()){
            return null;
        }
        Question question = new Question();
        question.setId(Long.parseLong(entries.get("id").toString()));
        question.setTitle(entries.get("title").toString());
        question.setContent(entries.get("content").toString());
        question.setTags(entries.get("tags").toString());
        question.setAnswer(entries.get("answer").toString());
        question.setSubmitNum(Integer.getInteger(entries.get("submitNum").toString()));
        question.setAcceptNum(Integer.getInteger(entries.get("acceptNum").toString()));
        question.setJudgeConfig(entries.get("judgeConfig").toString());
        question.setJudgeCase(entries.get("judgeCase").toString());
        question.setThumbNum(Integer.getInteger(entries.get("thumbNum").toString()));
        question.setFavourNum(Integer.getInteger(entries.get("favourNum").toString()));
        question.setUserId(Long.parseLong(entries.get("userId").toString()));
        String createTime = entries.get("createTime").toString().substring(0, 10);
        LocalDate parse = LocalDate.parse(createTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        question.setCreateTime(Date.from(parse.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        String updateTime = entries.get("updateTime").toString().substring(0, 10);
        LocalDate reparse = LocalDate.parse(updateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        question.setUpdateTime(Date.from(reparse.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        question.setIsDelete(Integer.getInteger(entries.get("isDelete").toString()));
        return question;
    }
}