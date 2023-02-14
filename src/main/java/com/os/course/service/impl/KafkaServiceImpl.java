package com.os.course.service.impl;

import com.os.course.service.KafkaService;
import com.os.course.util.Constant;
import com.os.course.util.kafka.Producer;
import com.os.course.util.kafka.TopicName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaServiceImpl implements KafkaService {

    private final Producer producer;

    private final TopicName topicName;

    public KafkaServiceImpl(Producer producer, TopicName topicName) {
        this.producer = producer;
        this.topicName = topicName;
    }

    @Override
    public void sendResourceId(long resourceId) {
        producer.sendMessage(resourceId, topicName.getUploadingTopicName());
    }







//    public void sendMessage(String message) {
//
//        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("nnn", message);
//        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
//
//            @Override
//            public void onSuccess(SendResult<String, String> result) {
//                System.out.println("Sent message=[" + message +
//                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
//            }
//            @Override
//            public void onFailure(Throwable ex) {
//                System.out.println("Unable to send message=["
//                        + message + "] due to : " + ex.getMessage());
//            }
//        });
//    }
}
