package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentDTO {
    private String commentNumber;
    private String postNumber;
    private String customerId;
    private String regDt;
    private String contents;
}
