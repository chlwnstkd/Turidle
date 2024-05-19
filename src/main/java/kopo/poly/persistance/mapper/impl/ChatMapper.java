package kopo.poly.persistance.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import kopo.poly.dto.ChatDTO;
import kopo.poly.persistance.mapper.AbstractMongoDBComon;
import kopo.poly.persistance.mapper.IChatMapper;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
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

    @Override
    public List<ChatDTO> getMessage(String roomName) throws Exception {
        log.info(this.getClass().getName() + ".getSingerSong Start!");

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<ChatDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(roomName);

        Document projection = new Document();
        projection.append("date", "$date");
        projection.append("msg", "$msg");
        projection.append("userId", "$userId");
        projection.append("name", "$name");
        projection.append("roomName", "$roomName");

        projection.append("_id", 0);

        // MongoDB의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다
        FindIterable<Document> rs = col.find(new Document()).projection(projection);


        for(Document doc : rs) {
            String date = doc.getString("date");
            String msg = doc.getString("msg");
            String userId = doc.getString("userId");
            String name = doc.getString("name");

            log.info("date : " + date + "/ msg : " + msg + "/ userId"
                    + userId + "/ name" + name + "/ roomName" + roomName);

            ChatDTO rDTO = ChatDTO.builder(
            ).date(date
            ).msg(msg
            ).userId(userId
            ).name(name
            ).roomName(roomName).build();

            rList.add(rDTO);
        }

        log.info(this.getClass().getName() + ".getSingerSong End!");

        return rList;
    }
}
