package ru.yandex.practicum.filmorate;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
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

@RunWith(SpringRunner.class)
@SpringBootTest

public class UserControllerTests {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setupMockMvc() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/users"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/films"));
    }

    @AfterEach
    public void setup() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/users"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/films"));
    }

    @Test
    public void userSuccessfullyReturns() throws Exception {
        String jsonStr = "{\n" + "  \"login\": \"dolore\",\n"
                + "  \"name\": \"Nick Name\",\n" + "  \"email\": \"mail@mail.ru\",\n"
                + "  \"birthday\": \"1946-08-20\"\n" + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()))
        //               .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
        //              .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr.getBytes())).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        JsonElement jsonElement = JsonParser.parseString(contentAsString);
        JsonObject object = jsonElement.getAsJsonObject();
        String login = object.get("login").getAsString();
        Assert.assertEquals(login, "dolore");
    }

    @Test
    public void userNotRegistered() throws Exception {
        String jsonStr = "{\n" +
                "  \"login\": \"dolore ullamco\",\n" +
                "  \"email\": \"yandex@mail.ru\",\n" +
                "  \"birthday\": \"2446-08-20\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void userNotRegisteredEmail() throws Exception {
        String jsonStr = "{\n" +
                "  \"login\": \"dolore ullamco\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"mail.ru\",\n" +
                "  \"birthday\": \"1980-08-20\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        jsonStr = "{\n" +
                "  \"login\": \"dolore ullamco\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"@mail.ru\",\n" +
                "  \"birthday\": \"1980-08-20\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        jsonStr = "{\n" +
                "  \"login\": \"dolore ullamco\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"ddd@mail\",\n" +
                "  \"birthday\": \"1980-08-20\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void userNotRegisteredBirthday() throws Exception {
        String jsonStr = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"test@mail.ru\",\n" +
                "  \"birthday\": \"2446-08-20\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonStr.getBytes()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void userRegisteredEmptyName() throws Exception {
        String jsonStr = "{\n" +
                "  \"login\": \"common\",\n" +
                "  \"email\": \"friend@common.ru\",\n" +
                "  \"birthday\": \"2000-08-20\"\n" +
                "}";

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr.getBytes())).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        JsonElement jsonElement = JsonParser.parseString(contentAsString);
        JsonObject object = jsonElement.getAsJsonObject();
        String login = object.get("name").getAsString();
        Assert.assertEquals(login, "common");
    }

}
