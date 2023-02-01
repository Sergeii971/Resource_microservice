package com.os.course.util;

import com.os.course.model.dto.Mp3FileDto;
import com.os.course.model.exception.IncorrectParameterValueException;
import com.os.course.model.exception.ServerErrorException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Objects;

@Component
public class Mp3FileUtil {
    public String createFileDownloadLink(Long fileId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/resources/")
                .path(String.valueOf(fileId))
                .toUriString();
    }

    public byte[] createMp3Range(Mp3FileDto mp3FileDto, String rangeHeader) {
        String[] audioRange = parseRangeHeader(rangeHeader);

        if (Objects.isNull(audioRange) || validateRanges(audioRange, mp3FileDto.getData().length)) {
            throw new IncorrectParameterValueException(Constant.INCORRECT_RANGE_HEADER_VALUE);
        }
        int beginIndex = Integer.parseInt(audioRange[Constant.RANGE_BEGIN_ARRAY_INDEX]);
        int endIndex = Integer.parseInt(audioRange[Constant.RANGE_END_ARRAY_INDEX]);
        int rangeLength = endIndex - beginIndex + 1;

        return createCuttingAudio(mp3FileDto, rangeLength, beginIndex);
    }

    private byte[] createCuttingAudio(Mp3FileDto mp3FileDto, int rangeLength, int beginIndex) {
        byte [] result = new byte[rangeLength];

        try(ByteArrayInputStream buffer = new ByteArrayInputStream(mp3FileDto.getData(), beginIndex, rangeLength)) {
            buffer.read(result);
        } catch (Exception e) {
            throw new ServerErrorException(Constant.PARSING_FILE_EXCEPTION_MESSAGE);
        }
        return result;
    }

    public String[] parseRangeHeader(String rangeHeader) {
        String[] result = null;
        if (rangeHeader.contains(Constant.RANGE_HEADER_PARAMETER_VALUE_KEY)) {
            result = rangeHeader.substring(Constant.RANGE_HEADER_PARAMETER_VALUE_KEY.length())
                    .split(Constant.RANGE_SEPARATOR);
        }
        return result;
    }

    public boolean validateRanges(String[] ranges, int audioFileLength) {
        return Arrays.stream(ranges)
                .filter(range -> range.matches(Constant.NUMBER_REGEX) && Integer.parseInt(range) < audioFileLength)
                .count() == Constant.RANGES_VALUE_COUNT;
    }
}
