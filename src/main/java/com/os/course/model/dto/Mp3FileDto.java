package com.os.course.model.dto;


import lombok.Data;

@Data
public class Mp3FileDto {
    private byte[] data;
    private String contentType;
}
