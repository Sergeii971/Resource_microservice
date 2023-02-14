package com.os.course.util.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class TopicName {
    @Value("${topic.uploadingMp3.name}")
    private  String uploadingTopicName;
}
