package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Director {
    private Long id;
    private String name;

    public Map<String, Object> directorToMap() {
        Map<String, Object> temp = new HashMap<>();
        temp.put("name", name);
        return temp;
    }
}
