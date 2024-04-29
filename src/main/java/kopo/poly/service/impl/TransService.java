package kopo.poly.service.impl;

import kopo.poly.dto.BasketDTO;
import kopo.poly.persistance.mapper.IBasketMapper;
import kopo.poly.service.IBasketService;
import kopo.poly.service.ITransService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TransService implements ITransService {

    /* 번역 코드 */

    @Override
    public String getTrans(String text) throws Exception {

        log.info(this.getClass().getName() + ".deleteBasket start!");

//            String apiKey = "YOUR_OPENAI_API_KEY";
//            String apiUrl = "https://api.openai.com/v1/language/translate";
//            String targetDialect = "jeolla";
//
//            String requestBody = "{\"text\":\"" + inputText + "\",\"to\":\"" + targetDialect + "\"}";
//
//            CloseableHttpClient httpClient = HttpClients.createDefault();
//            HttpPost httpPost = new HttpPost(apiUrl);
//                httpPost.addHeader(HttpHeaders.AUTHORIZATION,"Bearer "+apiKey);
//                httpPost.addHeader(HttpHeaders.CONTENT_TYPE,"application/json");
//                httpPost.setEntity(new
//
//            StringEntity(requestBody));
//
//            CloseableHttpResponse response = httpClient.execute(httpPost);
//            HttpEntity entity = response.getEntity();
//            String convertedText = null;
//                if(entity !=null)
//
//            {
//                convertedText = EntityUtils.toString(entity);
//            }
//
//                EntityUtils.consume(entity);
//                response.close();
//                httpClient.close();
//
//                return convertedText;
        return null;


    }
}
