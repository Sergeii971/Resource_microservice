package com.os.course.util;

import com.os.course.model.dto.Mp3FileInformationDto;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class Mp3FileUtil {
    public String createFileDownloadLink(Long fileId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/resources")
                .path(String.valueOf(fileId))
                .toUriString();
    }
}
