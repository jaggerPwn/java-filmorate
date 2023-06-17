package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
public class DbFilmServiceTests {
    private MockMvc mockMvc;
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void tearDown() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/users"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/films"));
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules().setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    @AfterEach
    public void setup() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/films"));
    }

    @Test
    public void filmAddAndDeleteLike() throws Exception {
        createThreeUsers();
        createTwoFilms();
        mockMvc.perform(MockMvcRequestBuilders.put("/films/2/like/1"));
        mockMvc.perform(MockMvcRequestBuilders.put("/films/2/like/2"));
        mockMvc.perform(MockMvcRequestBuilders.put("/films/1/like/1"));
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/films/2"));
        perform.andExpect(MockMvcResultMatchers.status().isOk());
        MvcResult mvcResult = perform.andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        Film film;
        film = objectMapper.readValue(contentAsString, Film.class);
        List<Film> filmList = getPopularFilms(objectMapper);
        Assertions.assertEquals(filmList.size(), 1);
        Assertions.assertEquals(filmList.get(0).getId(), 2);

        mockMvc.perform(MockMvcRequestBuilders.delete("/films/2/like/1"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/films/2/like/2"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/films/1/like/1"));

        filmList = getPopularFilms(objectMapper);
        Assertions.assertEquals(filmList.size(), 0);
    }

    @Test
    public void mpaTest() throws Exception {
        createThreeUsers();
        createTwoFilms();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/mpa/1")).andReturn();
        Mpa mpa = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Mpa.class);
        Assertions.assertEquals(mpa.getId(), 1);
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/films/2"));
        perform.andExpect(MockMvcResultMatchers.status().isOk());
        mvcResult = perform.andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Film film = objectMapper.readValue(contentAsString, Film.class);
        Assertions.assertEquals(film.getMpa().getId(), 3);
    }

    @Test
    public void genreTest() throws Exception {
        createThreeUsers();
        createTwoFilms();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/genres/1")).andReturn();
        Genres genres = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Genres.class);
        Assertions.assertEquals(genres.getId(), 1);
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/films/2"));
        perform.andExpect(MockMvcResultMatchers.status().isOk());
        mvcResult = perform.andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Film film = objectMapper.readValue(contentAsString, Film.class);
        List<Genres> genresList = (List<Genres>) film.getGenres();
        Assertions.assertEquals(genresList.get(0).getId(), 1);
    }


    private List<Film> getPopularFilms(ObjectMapper objectMapper) throws Exception {
        MvcResult mvcResult;
        ResultActions perform;
        String contentAsString;
        perform = mockMvc.perform(MockMvcRequestBuilders.get("/films/popular?count=1"));
        perform.andExpect(MockMvcResultMatchers.status().isOk());
        mvcResult = perform.andReturn();

        contentAsString = mvcResult.getResponse().getContentAsString();
        List<Film> filmList = objectMapper.readValue(contentAsString, new TypeReference<>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        return filmList;
    }

    private void createTwoFilms() throws Exception {
        String jsonStr = "{\n" +
                "  \"name\": \"nisi eiusmod\",\n" +
                "  \"description\": \"adipisicing\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()));
        jsonStr = "{\n" +
                "  \"name\": \"New film\",\n" +
                "  \"releaseDate\": \"1999-04-30\",\n" +
                "  \"description\": \"New film about friends\",\n" +
                "  \"duration\": 120,\n" +
                "  \"rate\": 4,\n" +
                "  \"mpa\": { \"id\": 3},\n" +
                "  \"genres\": [{ \"id\": 1}]\n" +
                "}";

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()));
    }


    private void createThreeUsers() throws Exception {
        String jsonStr =
                "{\n" +
                        "    \"id\": 1,\n" +
                        "    \"email\": \"mail@mail.ru\",\n" +
                        "    \"login\": \"dolore\",\n" +
                        "    \"name\": \"Nick Name\",\n" +
                        "    \"birthday\": \"1946-08-20\",\n" +
                        "    \"friends\": {}\n" +
                        "}";


        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr.getBytes()));

        jsonStr = "{\n" +
                "  \"login\": \"friend\",\n" +
                "  \"name\": \"friend adipisicing\",\n" +
                "  \"email\": \"friend@mail.ru\",\n" +
                "  \"birthday\": \"1976-08-20\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr.getBytes()));

        jsonStr = "{\n" +
                "  \"login\": \"common\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"friend@common.ru\",\n" +
                "  \"birthday\": \"2000-08-20\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr.getBytes()));
    }

}
