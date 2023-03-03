package com.os.course.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.os.course.Main;
import com.os.course.model.dto.DeletedFilesDto;
import com.os.course.model.dto.Mp3FileInformationDto;
import com.os.course.model.entity.Mp3File;
import com.os.course.persistent.repository.FileRepository;
import com.os.course.service.FileService;
import com.os.course.service.configuration.ServiceConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {Main.class, ServiceConfiguration.class})
class FileServiceImplTest {

    @Autowired
    private FileService Mp3FileService;
    @MockBean
    private FileRepository repository;
    @MockBean
    private AmazonS3 amazonS3;

    @Value("${bucket.name}")

    public String BUCKET_NAME = "myBucket";


    @Test
    public void givenMultipartFile_whenSaveMp3FileDto_thenReturnMp3FileInformationDto() throws IOException {
        Long id  = 1L;
        String name = "audio/mpeg";
        MockMultipartFile file = new MockMultipartFile("file", "hello.mp3",
                "audio/mpeg", "Hello, World!".getBytes());

        Mp3File savingObject = new Mp3File();
        savingObject.setId(id);
        savingObject.setContentType(name);

        Mockito.when(repository.save(Mockito.any())).thenReturn(savingObject);

        String tempFileName = UUID.randomUUID() + file.getName();
        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + tempFileName);
        file.transferTo(tempFile);

        Mockito.when(amazonS3.putObject(BUCKET_NAME,  String.valueOf(id), tempFile)).thenReturn(new PutObjectResult());

        Mp3FileInformationDto actual = Mp3FileService.save(file);
        Mp3FileInformationDto expected = new Mp3FileInformationDto();
        expected.setId(id);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void givenIdsToDelete_whenDelete_thenReturnDeletedMp3FileDto() {
        Long id  = 1L;

        Mp3File Mp3File = new Mp3File();
        Mp3File.setId(id);
        Mp3File.setDeleted(false);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(Mp3File));

        Mp3File savingObject = new Mp3File();
        savingObject.setId(id);
        savingObject.setDeleted(true);

        Mockito.when(repository.save(Mockito.any())).thenReturn(savingObject);

        Mockito.doNothing().when(amazonS3).deleteObject(Mockito.anyString(), Mockito.anyString());
        DeletedFilesDto actual = Mp3FileService.delete("1");
        DeletedFilesDto expected = new DeletedFilesDto();
        expected.getIds().add(id);

        Assertions.assertEquals(expected, actual);
    }
}