package com.os.course.model.dto;

import lombok.Data;

@Data
public class StorageDto implements BaseDto {
    private long id;
    private StorageType storageType;
    private String bucketName;

    private String path;
}