package com.os.course.util.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.os.course.model.exception.KafkaCustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Producer {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public Producer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> void sendMessage(T t, String topicName) {
        try {
            String objectAsMessage = objectMapper.writeValueAsString(t);

            kafkaTemplate.send(topicName, objectAsMessage,  objectAsMessage);
            log.info("message produced {}", objectAsMessage);
        } catch (JsonProcessingException e) {
            throw new KafkaCustomException(e.getMessage());
        }
    }
}
