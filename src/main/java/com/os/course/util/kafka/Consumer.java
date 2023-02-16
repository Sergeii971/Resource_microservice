package com.os.course.util.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.os.course.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Consumer {
    private final FileService fileService;

    private final ObjectMapper objectMapper;

    @Autowired
    public Consumer(FileService fileService, ObjectMapper objectMapper) {
        this.fileService = fileService;
        this.objectMapper = objectMapper;
    }

}
