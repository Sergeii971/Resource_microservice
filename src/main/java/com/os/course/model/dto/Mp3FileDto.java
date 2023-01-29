package com.os.course.model.dto;


import lombok.Data;

@Data
public class Mp3FileDto {
    private Long id;

    private String name;
    private String contentType;
    private Long size;
    private byte[] data;
    private String url;
}
