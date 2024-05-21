package kopo.poly.persistance.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IChat2Mapper {


    int insertUser(String userId) throws Exception;

    int deleteUser(String roomName, String userId) throws Exception;

}
