package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
public class UserService {

    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Map<String, String> addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null) {
            throw new ValidationException404("Id " + userId + " не найден в списке пользователей");
        } else if (friend == null) {
            throw new ValidationException404("Id " + friendId + " не найден в списке пользователей");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return Map.of("Success", "Now friends " + userId + " : " + friendId);
    }

    public Map<String, String> deleteFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null) {
            throw new ValidationException404("Id " + userId + " не найден в списке пользователей");
        } else if (friend == null) {
            throw new ValidationException404("Id " + friendId + " не найден в списке пользователей");
        }

        if (!user.getFriends().contains(friendId) && !friend.getFriends().contains(userId))
            throw new ValidationException400(MessageFormat.format("{0} и {1} не друзья, удаление невозможно",
                    user.getName(), friend.getName()));
        else {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
            return Map.of("Success", "Not friends anymore " + userId + " : " + friendId);
        }

    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public Set<User> getUserFriends(int userId) {
        Set<Integer> friends = getUserStorage().getUserById(userId).getFriends();
        Set<User> userFriends = new TreeSet<>((o1, o2) -> {
            if (o1.getId() > o2.getId()) return 1;
            else if (o2.getId() > o1.getId()) return -1;
            return 0;
        });
        friends.stream()
                .map(friend -> getUserStorage().getUserById(friend)).forEach(userFriends::add);
        return userFriends;
    }

    public Set<User> getCommonFriends(int userId, int friendId) {
        Set<User> userFriends = getUserFriends(userId);
        Set<User> friendFriends = getUserFriends(friendId);
        Set<User> intersection = new HashSet<>(userFriends);
        intersection.retainAll(friendFriends);
        return intersection;
    }
}
//    Создайте UserService, который будет отвечать за такие операции с пользователями,
//    как добавление в друзья, удаление из друзей, вывод списка общих друзей.
//    Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
//    То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.

//    Есть много способов хранить информацию о том, что два пользователя являются друзьями.
//    Например, можно создать свойство friends в классе пользователя, которое будет содержать список его друзей.
//    Вы можете использовать такое решение или придумать своё.
//    Для того чтобы обеспечить уникальность значения
//    (мы не можем добавить одного человека в друзья дважды), проще всего использовать для хранения Set<Long> c  id друзей.
//    Таким же образом можно обеспечить условие «один пользователь — один лайк» для оценки фильмов.

//    Переделайте код в контроллерах, сервисах и хранилищах под использование внедрения зависимостей.
//    Используйте аннотации @Service, @Component, @Autowired. Внедряйте зависимости через конструкторы классов.
//    Классы-сервисы должны иметь доступ к классам-хранилищам. Убедитесь, что сервисы зависят от интерфейсов классов-хранилищ, а не их реализаций.
//    Таким образом в будущем будет проще добавлять и использовать новые реализации с другим типом хранения данных.
//    Сервисы должны быть внедрены в соответствующие контроллеры.