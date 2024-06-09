package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record PostDTO(
         String postNumber,
         String title,
         String contents,
         String readCount,
         String regDt,
         String regId,
         String nickname,
         int from,
         int to
) {
}
