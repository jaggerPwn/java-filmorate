package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {


    private final Map<Long, User> users = new HashMap<>(); // для хранения пользователей
    private Long idUser = 1L;

    @Override
    public User saveUser(User user) {
        user.setId(idUser);
        users.put(user.getId(), user);
        idUser++;
        log.debug("User c ID {} создан", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {

        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("User c ID {} успешно обновлён", user.getId());
            return user;
        } else {
            log.debug("User c ID {} не обновлён", user.getId());
            throw new EntityNotFoundException("Нет такого ID");
        }
    }

    @Override
    public List<User> readAllUsers() {
        log.debug("Получен список всех Users");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        if (users.containsKey(id)) {
            log.debug("User c ID {} получен", id);
            return users.get(id);
        } else {
            log.debug("User c ID {} не получен", id);
            throw new EntityNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public void userAddFriend(Long userId, Long friendId) {

    }

    @Override
    public void userDeleteFriend(Long userId, Long friendId) {

    }

    @Override
    public List<User> getAllFriendByUserId(Long id) {
        return null;
    }
}
