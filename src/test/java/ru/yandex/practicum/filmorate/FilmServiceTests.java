package ru.yandex.practicum.filmorate;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.filmorate.model.Film;

import java.text.MessageFormat;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FilmServiceTests {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void filmAddAndDelete() throws Exception {
        createThreeUsers();
        createTwoFilms();
        mockMvc.perform(MockMvcRequestBuilders.put("/films/2/like/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/films/2/like/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getPopularFilms() throws Exception {
        createThreeUsers();
        int i = 0;
        while (i < 10) {
            createTwoFilms();
            i++;
        }
        likeFilm(mockMvc, 1, 1);
        likeFilm(mockMvc, 2, 1);
        likeFilm(mockMvc, 3, 1);
        likeFilm(mockMvc, 1, 2);
        likeFilm(mockMvc, 2, 2);
        likeFilm(mockMvc, 1, 3);
        mockMvc.perform(MockMvcRequestBuilders.get("/films/popular?count=2"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/films/popular?count=2"))
                .andReturn();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<Film> list = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assert.assertEquals(list.get(1).getName(), "New film");
        Assert.assertEquals(list.size(), 2);

        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/films/popular"))
                .andReturn();
        list = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assert.assertEquals(list.size(), 10);
        Assert.assertEquals(list.get(0).getLikes(), 3);
        Assert.assertEquals(list.get(2).getLikes(), 1);
        Assert.assertEquals(list.get(3).getLikes(), 0);

    }

    private void likeFilm(MockMvc mockMvc, int filmId, int userId) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(MessageFormat.format("/films/{0}/like/{1}", filmId, userId)));
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
                "  \"rate\": 4\n" +
                "}";

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()));
    }


    private void createThreeUsers() throws Exception {
        String jsonStr = "{\n" +
                "    \"id\": 1,\n" +
                "    \"email\": \"mail@yandex.ru\",\n" +
                "    \"login\": \"doloreUpdate\",\n" +
                "    \"name\": \"est adipisicing\",\n" +
                "    \"birthday\": \"1976-09-20\",\n" +
                "    \"friends\": []\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()));

        jsonStr = "{\n" +
                "  \"login\": \"friend\",\n" +
                "  \"name\": \"friend adipisicing\",\n" +
                "  \"email\": \"friend@mail.ru\",\n" +
                "  \"birthday\": \"1976-08-20\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()));
        jsonStr = "{\n" +
                "  \"login\": \"common\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"friend@common.ru\",\n" +
                "  \"birthday\": \"2000-08-20\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()));
    }

}
