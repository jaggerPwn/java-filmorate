package ru.yandex.practicum.filmorate;


import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest


public class DbFilmServiceTests {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @BeforeEach
    public void tearDown() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/users"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/films"));
    }

    @AfterEach
    public void setup() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/users"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/films"));
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
