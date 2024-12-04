package com.ghw.maplojbackendjudgeservice;

import com.ghw.maplojbackendcommon.constant.MessageQueueConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitRabbitMQ {

    public static void doInit(){
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("localhost");
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(MessageQueueConstant.EXCHANGE_NAME, "direct");

            // 创建队列
            channel.queueDeclare(MessageQueueConstant.QUEUE_NAME, true, false, false, null);
            channel.queueBind(MessageQueueConstant.QUEUE_NAME, MessageQueueConstant.EXCHANGE_NAME, "my_question_queue");
            log.info("消息队列启动成功");
        } catch (Exception e){
            log.error("消息队列启动失败");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        doInit();
    }
}
