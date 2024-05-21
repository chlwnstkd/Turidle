package kopo.poly.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface IClovaService {
        String prompt(String text) throws Exception;

}
