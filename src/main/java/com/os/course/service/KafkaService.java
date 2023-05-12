package com.os.course.service;

import java.util.List;

public interface KafkaService {
    void sendMp3MetaData(Long id);

    void sendMp3MetaData(List<String> param);
}
