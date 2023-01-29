package com.os.course.service.impl;

import com.os.course.model.dto.DeletedFilesDto;
import com.os.course.model.dto.Mp3FileDto;
import com.os.course.model.dto.Mp3FileInformationDto;
import com.os.course.model.entity.Mp3File;
import com.os.course.model.exception.FileNotFoundException;
import com.os.course.model.exception.MultipartFileException;
import com.os.course.persistent.repository.FileRepository;
import com.os.course.service.FileService;
import com.os.course.util.Constant;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public FileServiceImpl(FileRepository fileRepository, ModelMapper modelMapper) {
        this.fileRepository = fileRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Mp3FileInformationDto save(MultipartFile file) {
        Mp3File mp3File = new Mp3File();

        try {
            mp3File.setName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
            mp3File.setContentType(file.getContentType());
            mp3File.setData(file.getBytes());
            mp3File.setSize(file.getSize());
        } catch (Exception e) {
            throw new MultipartFileException(Constant.PARSING_FILE_EXCEPTION_MESSAGE);
        }

        return modelMapper.map(fileRepository.save(mp3File), Mp3FileInformationDto.class);
    }

    @Override
    public DeletedFilesDto delete(String ids) {
        DeletedFilesDto deletedFilesDto = new DeletedFilesDto();

        deletedFilesDto.setIds(Arrays.stream(ids.split(Constant.COMMA_SEPARATOR))
                .filter(id -> id.matches("[0-9]"))
                .map(id -> fileRepository.findById(Long.parseLong(id)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(file -> file.setDeleted(true))
                .map(fileRepository::save)
                .map(Mp3File::getId)
                .collect(Collectors.toList()));

        return deletedFilesDto;
    }

    @Override
    public Mp3FileDto getFileBy(Long id) {
        return fileRepository.findById(id)
                .map(file -> modelMapper.map(file, Mp3FileDto.class))
                .orElseThrow(() -> new FileNotFoundException(Constant.FILE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Mp3FileDto> getAllFiles() {
        return fileRepository.findAll()
                .stream()
                .map(file -> modelMapper.map(file, Mp3FileDto.class))
                .collect(Collectors.toList());
    }
}
