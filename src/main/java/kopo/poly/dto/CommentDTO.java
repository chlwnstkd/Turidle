package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record CommentDTO (
         String commentNumber,
         String userId,
         String nickname,
         String postNumber,
         String regDt,
         String contents,
         int from,
         int to
){
}
