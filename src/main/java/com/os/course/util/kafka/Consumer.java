package com.os.course.util.kafka;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.os.course.model.dto.StorageDto;
import com.os.course.model.dto.StorageType;
import com.os.course.model.exception.KafkaCustomException;
import com.os.course.service.FileService;
import com.os.course.util.MicroserviceUtil;
import com.os.course.util.security.AuthorizationHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class Consumer {
    private final FileService fileService;

    private final MicroserviceUtil microserviceUtil;

    private static final String s3StorageUpdatingTopic = "s3StorageUpdatingTopic";

    private final ObjectMapper objectMapper;

    private final AmazonS3 amazonS3;

    private final AuthorizationHeader authorizationHeader;

    @Autowired
    public Consumer(FileService fileService, MicroserviceUtil microserviceUtil, ObjectMapper objectMapper, AmazonS3 amazonS3, AuthorizationHeader authorizationHeader) {
        this.fileService = fileService;
        this.microserviceUtil = microserviceUtil;
        this.objectMapper = objectMapper;
        this.amazonS3 = amazonS3;
        this.authorizationHeader = authorizationHeader;
    }


    @KafkaListener(topics = s3StorageUpdatingTopic)
    @Retryable(value = {RuntimeException.class},
            backoff = @Backoff(value = 3000L),
            maxAttempts = 5)
    public void consumeIdOfUploadingFile(String message) {
        try {
            List<String> param = Arrays.asList(objectMapper.readValue(message, String[].class));
            long resourceId = Long.parseLong(param.get(0));
            authorizationHeader.setAuthorizationHeader(param.get(1));
            changeS3BucketDocument(resourceId);
        } catch (IOException e) {
            throw new KafkaCustomException(e.getMessage());
        }
    }

    private void changeS3BucketDocument(long id) throws IOException {
        List<StorageDto> stagingData = microserviceUtil.getStorageData();
        amazonS3.copyObject(microserviceUtil.parseStorageTypeObject(stagingData, StorageType.STAGING).getBucketName(), String.valueOf(id),
                microserviceUtil.parseStorageTypeObject(stagingData, StorageType.PERMANENT).getBucketName(), String.valueOf(id));
        fileService.updateStatus(id, StorageType.PERMANENT);
        log.info("resource with id=" + id + " was successfully updated");
    }
}
