package kopo.poly.persistance.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import kopo.poly.dto.ChatDTO;
import kopo.poly.persistance.mapper.AbstractMongoDBComon;
import kopo.poly.persistance.mapper.IChatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatMapper extends AbstractMongoDBComon implements IChatMapper {
    private final MongoTemplate mongodb;

    @Override
    public int saveMessage(ChatDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".saveMessage Start!");

        int res = 0;

        String colNm = pDTO.roomName();

        // 데이터를 저장할 컬렉션 생성
        super.createCollection(mongodb, colNm);

        // 저장할 컬렉션 객체 생성
        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // DTO를 Map 데이터타입으로 변경 한 뒤, 변경된 Map 데이터타입을 Document로 변경하기
        col.insertOne(new Document(new ObjectMapper().convertValue(pDTO, Map.class)));

        res = 1;

        log.info(this.getClass().getName() + ".saveMessage End!");

        return res;
    }
}
