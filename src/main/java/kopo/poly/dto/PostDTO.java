package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostDTO {
    private String postNumber;
    private String title;
    private String contents;
    private String readCount;
    private String regDt;
    private String chgDt;
    private String customerId;
    private String image;
    private String countComment;
}
