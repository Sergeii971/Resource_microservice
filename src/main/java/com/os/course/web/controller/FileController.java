package com.os.course.web.controller;

import com.os.course.model.dto.DeletedFilesDto;
import com.os.course.model.dto.Mp3FileDto;
import com.os.course.model.dto.Mp3FileInformationDto;
import com.os.course.service.FileService;
import com.os.course.util.Constant;
import com.os.course.util.Mp3FileUtil;
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

import javax.validation.constraints.Max;

@RestController
@RequestMapping("/v1/resources")
public class FileController {

    private final FileService fileService;
    private final Mp3FileUtil mp3FileUtil;

    @Autowired
    public FileController(FileService fileService, Mp3FileUtil mp3FileUtil) {
        this.fileService = fileService;
        this.mp3FileUtil = mp3FileUtil;
    }

    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mp3FileInformationDto> uploadFile(@RequestParam("file") MultipartFile file) {
        Mp3FileInformationDto mp3FileInformationDto = fileService.save(file);

        mp3FileInformationDto.setUrl(mp3FileUtil.createFileDownloadLink(mp3FileInformationDto.getId()));

        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mp3FileInformationDto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        Mp3FileDto mp3FileDto = fileService.getFileBy(id);

        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + mp3FileDto.getName() + "\"")
                .contentType(MediaType.valueOf(mp3FileDto.getContentType()))
                .body(mp3FileDto.getData());
    }

    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DeletedFilesDto removeFiles(@RequestParam(value = "id") @Max(200) String ids) {
        return fileService.delete(ids);
    }









    //    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<String> getFile(@PathVariable String id) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body("hello world");
//    }
}
