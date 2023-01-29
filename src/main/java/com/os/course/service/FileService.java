package com.os.course.service;

import com.os.course.model.dto.DeletedFilesDto;
import com.os.course.model.dto.Mp3FileDto;
import com.os.course.model.dto.Mp3FileInformationDto;
import com.os.course.model.entity.Mp3File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FileService {
    Mp3FileInformationDto save(MultipartFile file);

    DeletedFilesDto delete(String ids);

    Mp3FileDto getFileBy(Long id);

    List<Mp3FileDto> getAllFiles();
}
