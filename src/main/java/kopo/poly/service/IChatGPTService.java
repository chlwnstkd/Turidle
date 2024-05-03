package kopo.poly.service;

import kopo.poly.dto.ChatCompletionDTO;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface IChatGPTService {
        Map<String, Object> prompt(ChatCompletionDTO chatCompletionDto);

}
