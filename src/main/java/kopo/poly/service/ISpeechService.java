package kopo.poly.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ISpeechService {

    String streamingMicRecognize() throws Exception;

}
