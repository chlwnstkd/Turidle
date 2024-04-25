package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record DictDTO(

        String word, //단어
        String definition, // 설명
        String cat,// 전문분야
        String pos, // 품사
        String targetCode // 식별자
) {
}
