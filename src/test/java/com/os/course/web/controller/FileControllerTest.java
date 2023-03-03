package com.os.course.web.controller;

import com.os.course.Main;
import com.os.course.model.dto.DeletedFilesDto;
import com.os.course.service.FileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
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

    private Long savingId;

    @BeforeEach
    public void setUp() {
        MockMultipartFile file = new MockMultipartFile("file", "hello.mp3", "audio/mpeg",
                "Hello, World!".getBytes()
        );
        savingId = fileService.save(file).getId();

    }

    @Test
    void uploadFile() throws Exception {
       MockMultipartFile file = new MockMultipartFile("file", "hello.mp3", "audio/mpeg",
                "Hello, World!".getBytes()
        );
        mockMvc.perform(multipart("/v1/resources").file(file))
                .andExpect(status().isCreated());
    }

    @Test
    void getFile() {
        ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/v1/resources/" + savingId,
                byte[].class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void removeFiles() {
        ResponseEntity<DeletedFilesDto> responseEntity = restTemplate.exchange("http://localhost:" + port + "/v1/resources?id=" + savingId,
                HttpMethod.DELETE, HttpEntity.EMPTY, DeletedFilesDto.class);
        Assertions.assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }
}