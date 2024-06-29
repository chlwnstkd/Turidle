package kopo.poly.service;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "clovaApiService", url = "https://clovastudio.stream.ntruss.com")
public interface IClovaApiService {

    @RequestLine("POST /testapp/v1/chat-completions/HCX-DASH-001")
    List<String> prompt(@RequestBody ObjectNode requestData);

}


