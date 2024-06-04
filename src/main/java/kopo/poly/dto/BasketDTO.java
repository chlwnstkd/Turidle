package kopo.poly.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record BasketDTO(
        String userId,
        String word,
        String targetCode,
        String definition,
        String pos,
        int from,
        int to
) {
}
