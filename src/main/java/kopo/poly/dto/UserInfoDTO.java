package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record UserInfoDTO(

        String userId,
        String password,
        String email,
        String nickname,
        String regDt,
        String existsYn,
        int authNumber,
        int from,
        int to
){
}
