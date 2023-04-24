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
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InMemoryUserServiceTests {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setupMockMvc() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
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
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1/friends/2"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getByIdTest() throws Exception {
        createThreeFriends();
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/users/9999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getUserFriends() throws Exception {
        createThreeFriends();
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/2"));
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/friends"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        MvcResult mvcResult = mockMvc.perform(get("/users/1/friends"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<User> list = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assert.assertEquals(list.get(0).getName(), "friend adipisicing");
    }

    @Test
    public void getCommonFriends() throws Exception {
        createThreeFriends();
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/2"));
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/3"));
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/friends/3"));
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/friends/common/3"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        MvcResult mvcResult = mockMvc.perform(get("/users/1/friends/common/2")).andReturn();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<User> list = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assert.assertEquals(list.get(0).getId(), 3);
    }


    @Test
    public void secondHasTwoFriends() throws Exception {
        createThreeFriends();
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/2"));
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/3"));
        mockMvc.perform(MockMvcRequestBuilders.put("/users/2/friends/3"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        MvcResult mvcResult1 = mockMvc.perform(get("/users/1")).andReturn();
        User user0 = mapper.readValue(mvcResult1.getResponse().getContentAsString(), User.class);
        MvcResult mvcResult3 = mockMvc.perform(get("/users/3")).andReturn();
        User user2 = mapper.readValue(mvcResult3.getResponse().getContentAsString(), User.class);
        MvcResult mvcResult4 = mockMvc.perform(get("/users/2/friends")).andReturn();
        Assert.assertEquals(user0.getEmail(), "mail@yandex.ru");
        Assert.assertEquals(user0.getName(), "est adipisicing");
        Assert.assertEquals(user0.getLogin(), "doloreUpdate");
        Assert.assertEquals(user0.getBirthday().toString(), "1976-09-20");
        Assert.assertEquals(user2.getId(), 3);
        Assert.assertEquals(user2.getEmail(), "friend@common.ru");
        Assert.assertEquals(user2.getName(), "common");
        Assert.assertEquals(user2.getLogin(), "common");
        Assert.assertEquals(user2.getBirthday().toString(), "2000-08-20");
    }


    private void createThreeFriends() throws Exception {
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
