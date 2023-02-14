package com.os.course.util;

public class Constant {
    public static final String FILE_NOT_FOUND_EXCEPTION_MESSAGE = "file not found";
    public static final String PARSING_FILE_EXCEPTION_MESSAGE = "error while parsing file";

    public static final String INCORRECT_RANGE_HEADER_VALUE = "incorrect range";

    public static final String INCORRECT_EXTENSION = "incorrect extension";

    public static final String RANGE_HEADER_PARAMETER_VALUE_KEY = "bytes=";

    public static final String RANGE_SEPARATOR = "-";

    public static final String NUMBER_REGEX = "[0-9]";

    public static final int RANGES_VALUE_COUNT = 2;

    public static final String COMMA_SEPARATOR = ",";

    public static final int RANGE_BEGIN_ARRAY_INDEX = 0;

    public static final int RANGE_END_ARRAY_INDEX = 1;

    public static final String AUDIO_FILE_CONTENT_TYPE = "audio/mpeg";

    public static final String BUCKET_NAME = "mybucket";

    public static final String UPLOADING_MP3_TOPIC_NAME = "uploadingMp3";

    private Constant() {
    }
}
