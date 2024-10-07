package kopo.poly.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record ReportDTO(

        String userId,
        String reportId,
        String reason,
        String regDt,

        int from,
        int to

){
}
