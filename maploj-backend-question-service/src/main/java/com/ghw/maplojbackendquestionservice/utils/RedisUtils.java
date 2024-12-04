package com.ghw.maplojbackendquestionservice.utils;

import com.ghw.maplojbackendmodel.model.entity.Question;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class RedisUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 题目信息存入Redis
     * @param question
     * @return
     */
    public HashMap<String, Object> getHashMap(Question question) {
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
     * 从Redis中取出题目信息
     * @param id
     * @return
     */
    public Question getQuestionById(long id) {
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
