package com.os.course.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.os.course.model.dto.DeletedFilesDto;
import com.os.course.model.dto.Mp3FileDto;
import com.os.course.model.dto.Mp3FileInformationDto;
import com.os.course.model.entity.Mp3File;
import com.os.course.model.exception.FileNotFoundException;
import com.os.course.model.exception.IncorrectParameterValueException;
import com.os.course.model.exception.ServerErrorException;
import com.os.course.persistent.repository.FileRepository;
import com.os.course.service.FileService;
import com.os.course.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final ModelMapper modelMapper;

    private final AmazonS3 amazonS3;

    @Autowired
    public FileServiceImpl(FileRepository fileRepository, ModelMapper modelMapper, AmazonS3 amazonS3) {
        this.fileRepository = fileRepository;
        this.modelMapper = modelMapper;
        this.amazonS3 = amazonS3;
    }

    @Override
    @Transactional
    public Mp3FileInformationDto save(MultipartFile file) {
        if (!Constant.AUDIO_FILE_CONTENT_TYPE.equals(file.getContentType())) {
            throw new IncorrectParameterValueException(Constant.INCORRECT_EXTENSION);
        }
        Mp3File mp3File = new Mp3File();
        try {
            mp3File.setContentType(file.getContentType());
            mp3File = fileRepository.save(mp3File);
            uploadDocument(file, mp3File.getId());
        } catch (Exception e) {
            throw new IncorrectParameterValueException(Constant.PARSING_FILE_EXCEPTION_MESSAGE);
        }
        return modelMapper.map(mp3File, Mp3FileInformationDto.class);
    }

    @Override
    @Transactional
    public DeletedFilesDto delete(String ids) {
        DeletedFilesDto deletedFilesDto = new DeletedFilesDto();

        deletedFilesDto.setIds(Arrays.stream(ids.split(Constant.COMMA_SEPARATOR))
                .filter(id -> id.matches(Constant.NUMBER_REGEX))
                .map(id -> fileRepository.findById(Long.parseLong(id)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(mp3File -> !mp3File.isDeleted())
                .peek(file -> file.setDeleted(true))
                .map(fileRepository::save)
                .peek(mp3File -> amazonS3.deleteObject(Constant.BUCKET_NAME, String.valueOf(mp3File.getId())))
                .map(Mp3File::getId)
                .collect(Collectors.toList()));

        return deletedFilesDto;
    }

    @Override
    public Mp3FileDto getFileBy(Long id) {
        Mp3FileDto mp3FileDto = fileRepository.findById(id)
                .map(file -> modelMapper.map(file, Mp3FileDto.class))
                .orElseThrow(() -> new FileNotFoundException(Constant.FILE_NOT_FOUND_EXCEPTION_MESSAGE));

        S3Object s3Object = amazonS3.getObject(Constant.BUCKET_NAME, String.valueOf(id));
        try(S3ObjectInputStream stream = s3Object.getObjectContent()) {
            mp3FileDto.setData(IOUtils.toByteArray(stream));
        } catch (IOException e) {
            throw new ServerErrorException(Constant.PARSING_FILE_EXCEPTION_MESSAGE);
        }
        return mp3FileDto;
    }


    private void uploadDocument(MultipartFile file, long id) throws IOException {
        String tempFileName = UUID.randomUUID() + file.getName();
        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + tempFileName);
        file.transferTo(tempFile);
        amazonS3.putObject(Constant.BUCKET_NAME,  String.valueOf(id), tempFile);
        tempFile.delete();
    }
}
