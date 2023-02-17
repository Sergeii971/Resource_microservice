package com.os.course.service;

import com.os.course.model.dto.DeletedFilesDto;
import com.os.course.model.dto.Mp3FileDto;
import com.os.course.model.dto.Mp3FileInformationDto;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    Mp3FileInformationDto save(MultipartFile file);

    DeletedFilesDto delete(String ids);

    Mp3FileDto getFileBy(Long id);
}
