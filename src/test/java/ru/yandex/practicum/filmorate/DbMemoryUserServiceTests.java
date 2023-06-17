package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest


public class DbMemoryUserServiceTests {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void tearDown() throws Exception {
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
    public void userAddFriend() throws Exception {
        createThreeFriends();
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/2"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void userDeleteFriend() throws Exception {
        createThreeFriends();
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/2"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1/friends/2"))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void getByIdTest() throws Exception {
        createThreeFriends();
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/users/9999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    private void createThreeFriends() throws Exception {
        String jsonStr = "{\n" +
                "    \"id\": 1,\n" +
                "    \"email\": \"mail@yandex.ru\",\n" +
                "    \"login\": \"doloreUpdate\",\n" +
                "    \"name\": \"est adipisicing\",\n" +
                "    \"birthday\": \"1976-09-20\",\n" +
                "    \"friends\": {}\n" +
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
