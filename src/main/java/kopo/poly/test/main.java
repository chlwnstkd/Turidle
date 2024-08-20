package kopo.poly.test;

// Google Cloud 클라이언트 라이브러리를 가져옵니다
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import java.util.List;

public class main {

    /** Speech API를 사용하여 오디오 파일을 텍스트로 변환하는 예제입니다. */
    public static void main(String[] args) throws Exception {
        // 클라이언트를 생성합니다
        try (SpeechClient speechClient = SpeechClient.create()) {

            // 텍스트로 변환할 오디오 파일의 경로입니다
            String gcsUri = "gs://cloud-samples-data/speech/brooklyn_bridge.raw";

            // 동기식 음성 인식 요청을 구성합니다
            RecognitionConfig config =
                    RecognitionConfig.newBuilder()
                            .setEncoding(AudioEncoding.LINEAR16) // 오디오 인코딩 형식을 설정합니다
                            .setSampleRateHertz(16000) // 샘플링 속도를 설정합니다 (16kHz)
                            .setLanguageCode("en-US") // 언어 코드를 설정합니다 (미국 영어)
                            .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUri).build();

            // 오디오 파일에 대한 음성 인식을 수행합니다
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult result : results) {
                // 주어진 음성의 청크에 대해 여러 개의 대체 텍스트가 있을 수 있습니다. 여기서는 가장 가능성이 높은 첫 번째 텍스트만 사용합니다.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript()); // 텍스트로 변환된 내용을 출력합니다
            }
        }
    }
}
