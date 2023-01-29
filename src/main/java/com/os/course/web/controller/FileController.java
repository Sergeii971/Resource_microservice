package com.os.course.web.controller;

import com.os.course.model.dto.Mp3FileDto;
import com.os.course.model.entity.Mp3File;
import com.os.course.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("/v1/resources/mp3")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileService.save(file);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("ok");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Could not upload the file: %s!", file.getOriginalFilename()));
        }
    }

//    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<String> getFile(@PathVariable String id) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body("hello world");
//    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        Mp3FileDto mp3FileDto = fileService.getFileBy(id);

        String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/resources/mp3")
                .path(String.valueOf(mp3FileDto.getId()))
                .toUriString();
        mp3FileDto.setUrl(downloadURL);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + mp3FileDto.getName() + "\"")
                .contentType(MediaType.valueOf(mp3FileDto.getContentType()))
                .body(mp3FileDto.getData());
    }

//    @GetMapping
//    public List<FileResponse> list() {
//        return fileService.getAllFiles()
//                .stream()
//                .map(this::mapToFileResponse)
//                .collect(Collectors.toList());
//    }

//    private FileResponse mapToFileResponse(FileEntity fileEntity) {
//        String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/files/")
//                .path(fileEntity.getId())
//                .toUriString();
//        FileResponse fileResponse = new FileResponse();
//        fileResponse.setId(fileEntity.getId());
//        fileResponse.setName(fileEntity.getName());
//        fileResponse.setContentType(fileEntity.getContentType());
//        fileResponse.setSize(fileEntity.getSize());
//        fileResponse.setUrl(downloadURL);
//
//        return fileResponse;
//    }

}
