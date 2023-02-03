package com.os.course.model.dto;


import lombok.Data;

@Data
public class Mp3FileDto implements BaseDto {
    private long id;
    private byte[] data;
    private String contentType;
    private int size;
}
