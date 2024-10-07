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


    @PostMapping("/stt")
    public String startRecognition(@RequestBody Map<String, Object> audioFormat) {


        System.out.println("샘플 레이트: " + audioFormat.get("sampleRate"));
        System.out.println("샘플 비트 크기: " + audioFormat.get("sampleSizeInBits"));
        System.out.println("채널 수: " + audioFormat.get("channels"));
        System.out.println("signed: " + audioFormat.get("signed"));
        System.out.println("bigEndian: " + audioFormat.get("bigEndian"));

        // 요청에서 각각의 파라미터를 가져오기
        int sampleRate = (Integer) audioFormat.get("sampleRate");
        int sampleSizeInBits = (Integer) audioFormat.get("sampleSizeInBits");
        int channels = (Integer) audioFormat.get("channels");
        boolean signed = (Boolean) audioFormat.get("signed");
        boolean bigEndian = (Boolean) audioFormat.get("bigEndian");

        String[] format = {String.valueOf(sampleRate), String.valueOf(sampleSizeInBits),
                String.valueOf(channels), String.valueOf(signed),String.valueOf(bigEndian)};

        try {
            return speechService.streamingMicRecognize(format);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error starting speech recognition: " + e.getMessage();
        }
    }
}
