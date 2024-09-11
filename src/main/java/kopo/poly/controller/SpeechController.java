package kopo.poly.controller;


import kopo.poly.service.impl.SpeechService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpeechController {

    private final SpeechService speechService;

    public SpeechController(SpeechService speechService) {
        this.speechService = speechService;
    }

    @PostMapping("/stt")
    public String startRecognition() {


        try {
            return speechService.streamingMicRecognize();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error starting speech recognition: " + e.getMessage();
        }
    }
}
