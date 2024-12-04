package com.ghw.maplojbackendquestionservice.job;

import com.ghw.maplojbackendmodel.model.entity.Question;
import com.ghw.maplojbackendquestionservice.service.QuestionService;
import com.ghw.maplojbackendquestionservice.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务
 */
@Component
@Slf4j
public class QuestionBuildJob {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private QuestionService questionService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 缓存预热
     */
    @Scheduled(cron = "0 30 5 * * *")
    public void doCacherecommend(){
        RLock rLock = redissonClient.getLock("com:ghw:doCache:question");
        try {
            if(rLock.tryLock(0, -1, TimeUnit.SECONDS)){
                List<Question> questionlist = questionService.list();
                for (Question question : questionlist) {
                    HashMap<String, Object> hashMap = redisUtils.getHashMap(question);
                    stringRedisTemplate.opsForHash().putAll(String.format("com:ghw:maploj:message:question:%s", question.getId()), hashMap);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 定时删婚存
     */
    @Scheduled(cron = "0 59 23 * * *")
    public void deleteCacherecommend(){
        RLock rLock = redissonClient.getLock("com:ghw:deleteCache:question");
        try {
            if(rLock.tryLock(0, -1, TimeUnit.SECONDS)){
                // 设置SCAN命令的选项，包括匹配的模式
                ScanOptions options = ScanOptions.scanOptions().match("com:ghw:maploj:message:question:" + "*").count(100).build();

                // 使用SCAN命令遍历匹配前缀的键
                Set<String> keys = stringRedisTemplate.keys("com:ghw:maploj:message:question:" + "*");

                // 如果找到键，使用DEL命令删除
                if (keys != null) {
                    stringRedisTemplate.delete(keys);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rLock.unlock();
        }
    }

}
