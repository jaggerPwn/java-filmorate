package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Data
@Builder
public class Mpa {
    @NonNull
    private final int id;
    @Nullable
    private String name;


    public Mpa(int id, String name) {
        this.id = id;
        if(name != null) this.name = name;
        else if (id == 1) this.name ="G";
        else if (id == 2) this.name ="PG";
        else if (id == 3) this.name ="PG13";
        else if (id == 4) this.name ="R";
        else if (id == 5) this.name ="NC17";
        else this.name = "";
    }
}

