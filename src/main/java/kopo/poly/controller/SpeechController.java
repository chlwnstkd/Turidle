package kopo.poly.controller;


import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.protobuf.ByteString;
import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.service.ISpeechService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.sound.sampled.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class SpeechController {

    private final ISpeechService speechService;


    @GetMapping("/stt")
    public void startRecognition() {
        speechService.testTranscribeWavFile();

    }
}
