package com.os.course.util.kafka;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.os.course.model.dto.StorageDto;
import com.os.course.model.dto.StorageType;
import com.os.course.model.exception.KafkaProducingException;
import com.os.course.service.FileService;
import com.os.course.util.MicroserviceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class Consumer {
    private final FileService fileService;

    private final MicroserviceUtil microserviceUtil;

    private static final String s3StorageUpdatingTopic = "s3StorageUpdatingTopic";

    private final ObjectMapper objectMapper;

    private final AmazonS3 amazonS3;

    @Autowired
    public Consumer(FileService fileService, MicroserviceUtil microserviceUtil, ObjectMapper objectMapper, AmazonS3 amazonS3) {
        this.fileService = fileService;
        this.microserviceUtil = microserviceUtil;
        this.objectMapper = objectMapper;
        this.amazonS3 = amazonS3;
    }


    @KafkaListener(topics = s3StorageUpdatingTopic)
    @Retryable(value = {RuntimeException.class},
            backoff = @Backoff(value = 3000L),
            maxAttempts = 5)
    public void consumeIdOfUploadingFile(String message) {
        try {
            Long resourceId = objectMapper.readValue(message, Long.class);
            changeS3BucketDocument(resourceId);
        } catch (IOException e) {
            throw new KafkaProducingException(e.getMessage());
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
