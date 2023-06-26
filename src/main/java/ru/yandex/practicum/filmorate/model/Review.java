package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Review {
    private Long reviewId;
    @NotBlank
    private String content;
    @JsonProperty(value = "isPositive")
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    private int useful;

    public Map<String, ?> reviewToMap() {
        Map<String, Object> temp = new HashMap<>();
        temp.put("id", reviewId);
        temp.put("content", content);
        temp.put("ispositive", isPositive);
        temp.put("userid", userId);
        temp.put("filmid", filmId);
        return temp;
    }
}