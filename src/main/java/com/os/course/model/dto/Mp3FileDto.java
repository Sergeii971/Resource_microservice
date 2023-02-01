package com.os.course.model.dto;


import lombok.Data;

@Data
public class Mp3FileDto {
    private long id;
    private byte[] data;
    private String contentType;
    private int size;
}
