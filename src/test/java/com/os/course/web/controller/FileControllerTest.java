package com.os.course.web.controller;

import com.os.course.Main;
import com.os.course.model.dto.DeletedFilesDto;
import com.os.course.model.dto.StorageDto;
import com.os.course.model.dto.StorageType;
import com.os.course.service.FileService;
import com.os.course.util.MicroserviceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Main.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureMockMvc
class FileControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileService fileService;

    @MockBean
    private MicroserviceUtil microserviceUtil;

    private Long savingId;

    @BeforeEach
    public void setUp() {
        MockMultipartFile file = new MockMultipartFile("file", "hello.mp3", "audio/mpeg",
                "Hello, World!".getBytes()
        );
        Mockito.when(microserviceUtil.getStorageData(Mockito.any())).thenReturn(new StorageDto(2, StorageType.PERMANENT, "permanentbucket", ""));
        savingId = fileService.save(file).getId();

    }

    @Test
    void uploadFile() throws Exception {
        Mockito.when(microserviceUtil.getStorageData(Mockito.any())).thenReturn(new StorageDto(1, StorageType.STAGING, "stagingbucket", ""));
        MockMultipartFile file = new MockMultipartFile("file", "hello.mp3", "audio/mpeg",
                "Hello, World!".getBytes()
        );
        mockMvc.perform(multipart("/v1/resources").file(file).header("Authorization", "qwerty"))
                .andExpect(status().isCreated());
    }

    @Test
    void getFile() {
        Mockito.when(microserviceUtil.getStorageData(Mockito.any())).thenReturn(new StorageDto(2, StorageType.PERMANENT, "permanentbucket", ""));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "qwerty");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange("http://localhost:" + port + "/v1/resources/" + savingId, HttpMethod.GET, requestEntity,
                byte[].class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void removeFiles() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "qwerty");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        Mockito.when(microserviceUtil.getStorageData(Mockito.any())).thenReturn(new StorageDto(2, StorageType.PERMANENT, "permanentbucket", ""));
        ResponseEntity<DeletedFilesDto> responseEntity = restTemplate.exchange("http://localhost:" + port + "/v1/resources?id=" + savingId,
                HttpMethod.DELETE, requestEntity, DeletedFilesDto.class);
        Assertions.assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }
}