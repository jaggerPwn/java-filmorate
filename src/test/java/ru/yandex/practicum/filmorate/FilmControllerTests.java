package ru.yandex.practicum.filmorate;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig({TransferServiceConfig.class})

public class FilmControllerTests {
    @Autowired
    private MockMvc mockMvc;

    public void filmSuccessfullyReturns() throws Exception {
        String jsonStr = "{\n" +
                "  \"name\": \"nisi eiusmod\",\n" +
                "  \"description\": \"adipisicing\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(get("/films"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MvcResult mvcResult = mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr.getBytes())).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        JsonElement jsonElement = JsonParser.parseString(contentAsString);
        JsonObject object = jsonElement.getAsJsonObject();
        String login = object.get("name").getAsString();
        Assert.assertEquals(login, "nisi eiusmod");
    }

    @Test
    public void filmUpdated() throws Exception {
        filmSuccessfullyReturns();
        String jsonStr = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Film Updated\",\n" +
                "  \"releaseDate\": \"1989-04-17\",\n" +
                "  \"description\": \"New film update decription\",\n" +
                "  \"duration\": 190,\n" +
                "  \"rate\": 4\n" +
                "}";
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStr.getBytes()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr.getBytes())).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        JsonElement jsonElement = JsonParser.parseString(contentAsString);
        JsonArray array = jsonElement.getAsJsonArray();
        HashMap<Integer, User> users = new HashMap<>();
        for (JsonElement element : array) {
            User user = User.builder().build();
            int id = element.getAsJsonObject().get("id").getAsInt();
            user.setId(id);
            user.setName(element.getAsJsonObject().get("name").getAsString());
            users.put(id, user);
        }
        Assert.assertEquals(users.get(1).getName(), "Film Updated");

    }

    @Test
    public void filmNotRegisteredDueName() throws Exception {
        String jsonStr = "{\n" +
                "  \"name\": \"\",\n" +
                "  \"description\": \"Description\",\n" +
                "  \"releaseDate\": \"1900-03-25\",\n" +
                "  \"duration\": 200\n" +
                "}";

        testPostToExpect4xxError(jsonStr);
    }

    @Test
    public void filmNotRegisteredDueBigDescription() throws Exception {
        String jsonStr = "{\n" +
                "  \"name\": \"Film name\",\n" +
                "  \"description\": \"Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.\",\n" +
                "    \"releaseDate\": \"1900-03-25\",\n" +
                "  \"duration\": 200\n" +
                "}";

        testPostToExpect4xxError(jsonStr);
    }

    @Test
    public void filmNotRegisteredOldDate() throws Exception {
        String jsonStr = "{\n" +
                "  \"name\": \"Name\",\n" +
                "  \"description\": \"Description\",\n" +
                "  \"releaseDate\": \"1890-03-25\",\n" +
                "  \"duration\": 200\n" +
                "}";

        testPostToExpect4xxError(jsonStr);
    }

    @Test
    public void filmUpdateUnknown() throws Exception {
        String jsonStr = "{\n" +
                "  \"name\": \"Name\",\n" +
                "  \"description\": \"Description\",\n" +
                "  \"releaseDate\": \"1890-03-25\",\n" +
                "  \"duration\": 200\n" +
                "}";

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    public void filmNotRegisteredZeroORNegativeDuration() throws Exception {
        String jsonStr = "{\n" +
                "  \"name\": \"Name\",\n" +
                "  \"description\": \"Descrition\",\n" +
                "  \"releaseDate\": \"1980-03-25\",\n" +
                "  \"duration\": -200\n" +
                "}";

        testPostToExpect4xxError(jsonStr);
        jsonStr = "{\n" +
                "  \"name\": \"Name\",\n" +
                "  \"description\": \"Descrition\",\n" +
                "  \"releaseDate\": \"1980-03-25\",\n" +
                "  \"duration\": 0\n" +
                "}";
        testPostToExpect4xxError(jsonStr);
    }

    private void testPostToExpect4xxError(String jsonStr) throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
