package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.exception.ValidationException404;

@Data
@Builder
public class Genres {
    @NonNull
    private final int id;
    @Nullable
    private final String name;


    public Genres(int id, String name) {
        this.id = id;
        if(name != null) this.name = name;
        else if (id == 1) this.name ="COMEDY";
        else if (id == 2) this.name ="DRAMA";
        else if (id == 3) this.name ="CARTOON";
        else if (id == 4) this.name ="THRILLER";
        else if (id == 5) this.name ="DOCUMENTARY";
        else if (id == 6) this.name ="ACTION";
        else this.name = "";
    }
}
