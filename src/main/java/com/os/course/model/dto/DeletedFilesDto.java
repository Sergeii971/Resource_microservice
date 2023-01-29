package com.os.course.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeletedFilesDto {
    @JsonProperty(value = "ids")
    private List<Long> ids;
}
