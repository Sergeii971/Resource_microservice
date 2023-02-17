package com.os.course.service.impl;

import com.os.course.service.KafkaService;
import com.os.course.util.kafka.Producer;
import com.os.course.util.kafka.TopicName;
import lombok.extern.slf4j.Slf4j;
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
    public  void sendMp3MetaData(Long id) {
        producer.sendMessage(id, topicName.getUploadingTopicName());
    }

}
