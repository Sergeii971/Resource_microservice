package com.os.course.service.impl;

import com.os.course.model.dto.Mp3FileDto;
import com.os.course.model.entity.Mp3File;
import com.os.course.persistent.repository.FileRepository;
import com.os.course.service.FileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
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
    public void save(MultipartFile file) throws IOException {
        Mp3File mp3File = new Mp3File();

        mp3File.setName(StringUtils.cleanPath(file.getOriginalFilename()));
        mp3File.setContentType(file.getContentType());
        mp3File.setData(file.getBytes());
        mp3File.setSize(file.getSize());

        fileRepository.save(mp3File);
    }

    @Override
    public void update(Mp3File mp3File) {
        fileRepository.save(mp3File);
    }

    @Override
    public Mp3FileDto getFileBy(Long id) {
        return fileRepository.findById(id)
                .map(file -> modelMapper.map(file, Mp3FileDto.class))
                .orElseThrow(() -> new RuntimeException());
    }

    @Override
    public List<Mp3FileDto> getAllFiles() {
        return fileRepository.findAll()
                .stream()
                .map(file -> modelMapper.map(file, Mp3FileDto.class))
                .collect(Collectors.toList());
    }
}
