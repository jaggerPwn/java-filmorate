package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

    public Map<String, Object> userToMap() {
        Map<String, Object> temp = new HashMap<>();
        temp.put("email", email);
        temp.put("login", login);
        temp.put("name", name);
        temp.put("birthday", birthday);
        return temp;
    }

}
