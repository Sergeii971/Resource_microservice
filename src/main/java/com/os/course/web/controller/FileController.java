package com.os.course.web.controller;

import com.os.course.model.dto.DeletedFilesDto;
import com.os.course.model.dto.Mp3FileDto;
import com.os.course.model.dto.Mp3FileInformationDto;
import com.os.course.service.FileService;
import com.os.course.service.KafkaService;
import com.os.course.util.Constant;
import com.os.course.util.Mp3FileUtil;
import com.os.course.util.security.AuthorizationHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/resources")
public class FileController {

    private final FileService fileService;
    private final Mp3FileUtil mp3FileUtil;

    private final KafkaService kafkaService;

    private final AuthorizationHeader authHeader;

    @Autowired
    public FileController(FileService fileService, Mp3FileUtil mp3FileUtil, KafkaService kafkaService, AuthorizationHeader authHeader) {
        this.fileService = fileService;
        this.mp3FileUtil = mp3FileUtil;
        this.kafkaService = kafkaService;
        this.authHeader = authHeader;
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mp3FileInformationDto uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader(value = "Authorization") String authorizationHeader) {
        authHeader.setAuthorizationHeader(authorizationHeader);
        Mp3FileInformationDto mp3FileInformationDto = fileService.save(file);
        List<String> param  = new ArrayList<>();
        param.add(String.valueOf(mp3FileInformationDto.getId()));
        param.add(authorizationHeader);
        kafkaService.sendMp3MetaData(param);
        return mp3FileInformationDto;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
    produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> getFile(@RequestHeader(value = "Range", required = false)
                                              String rangeHeader, @PathVariable(value = "id")Long id,
                                          @RequestHeader(value = "Authorization") String authorizationHeader) {
        authHeader.setAuthorizationHeader(authorizationHeader);
        Mp3FileDto mp3FileDto = fileService.getFileBy(id);
        return Objects.isNull(rangeHeader) ?
                ResponseEntity.status(200)
                        .contentType(MediaType.valueOf(mp3FileDto.getContentType()))
                        .body(mp3FileDto.getData()) :
                ResponseEntity.status(206)
                        .contentType(MediaType.valueOf(mp3FileDto.getContentType()))
                        .headers(createHeadersForRangeRequest(mp3FileDto, rangeHeader))
                        .body(mp3FileUtil.createMp3Range(mp3FileDto, rangeHeader));
    }

    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DeletedFilesDto removeFiles(@RequestParam(value = "id") @Max(200) String ids,
                                       @RequestHeader(value = "Authorization") String authorizationHeader) {
        authHeader.setAuthorizationHeader(authorizationHeader);
        return fileService.delete(ids);
    }

    private HttpHeaders createHeadersForRangeRequest(Mp3FileDto mp3FileDto, String rangeHeader) {
        HttpHeaders httpHeaders = new HttpHeaders();
        String[] audioRange = mp3FileUtil.parseRangeHeader(rangeHeader);

        int beginIndex = Integer.parseInt(audioRange[Constant.RANGE_BEGIN_ARRAY_INDEX]);
        int endIndex = Integer.parseInt(audioRange[Constant.RANGE_END_ARRAY_INDEX]);
        int rangeLength = endIndex - beginIndex + 1;

        httpHeaders.add("Content-Type", Constant.AUDIO_FILE_CONTENT_TYPE);
        httpHeaders.add("Content-Length", String.valueOf(rangeLength));
        httpHeaders.add("Content-Range",   beginIndex + Constant.RANGE_SEPARATOR + endIndex
                + "/" + mp3FileDto.getData().length);
        return httpHeaders;
    }
}
