package com.group3.apiserver.dto.sender.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.group3.apiserver.entity.ReviewEntity;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewListDTO {
    public ReviewListDTO() {
        this.reviews = new LinkedList<>();
    }
    private List<ReviewDetailDTO> reviews;
    public void addReviewDTO(ReviewEntity review, String userName) {
        ReviewDetailDTO reviewDetailDTO = new ReviewDetailDTO();
        reviewDetailDTO.setUserName(userName);
        reviewDetailDTO.setStars(review.getStars());
        reviewDetailDTO.setContent(review.getContent());
        reviewDetailDTO.setCommentDate(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                        Double.valueOf(review.getCommentDate())));
        this.reviews.add(reviewDetailDTO);
    }
    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class ReviewDetailDTO {
        @JsonProperty("author")
        private String userName;
        @JsonProperty("rate")
        private Integer stars;
        private String content;
        @JsonProperty("datetime")
        private String commentDate;
    }
}
