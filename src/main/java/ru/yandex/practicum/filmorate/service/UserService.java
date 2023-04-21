package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Map;

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

        return Map.of("Friends", userId + " : " + friendId);
    }


    public UserStorage getUserStorage() {
        return userStorage;
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