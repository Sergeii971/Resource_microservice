package com.os.course.model.dto;

import lombok.Data;

@Data
public class StorageDto implements BaseDto {
    private long id;
    private StorageType storageType;
    private String bucketName;

    private String path;

    public StorageDto() {
    }

    public StorageDto(long id, StorageType storageType, String bucketName, String path) {
        this.id = id;
        this.storageType = storageType;
        this.bucketName = bucketName;
        this.path = path;
    }
}