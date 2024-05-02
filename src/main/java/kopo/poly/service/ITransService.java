package kopo.poly.service;

import kopo.poly.dto.BasketDTO;

import java.util.List;
import java.util.Map;

public interface ITransService {
    String getTrans(String text, String targetDialect, String sourceDialect) throws Exception; // 번역
}
