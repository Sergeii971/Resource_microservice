package com.os.course.service;

import com.os.course.model.dto.DeletedFilesDto;
import com.os.course.model.dto.Mp3FileDto;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    long save(MultipartFile file);

    DeletedFilesDto delete(String ids);

    Mp3FileDto getFileBy(Long id);
}
