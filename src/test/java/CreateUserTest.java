import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import user.DataGenForUser;
import user.User;
import user.UserRequest;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {
    DataGenForUser dataGenForUser = new DataGenForUser();
    User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
    UserRequest userRequest = new UserRequest();

    @Test
    @DisplayName("Создаем нового пользователя с уникальными данными")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkCreateNewUniqueUser() throws InterruptedException {
        Response response = userRequest.createUser(user);
        userRequest.setAccessTkn(response);
        response.then()
                .assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Попытка создать нового пользователя с уже использующейся почтой")
    @Description("Проверяем тело ответа и статускод 403")
    public void checkCreateAlreadyExistUserReturnForbidden() throws InterruptedException {
        Response response = userRequest.createUser(user);//делаем первого юзера
        userRequest.setAccessTkn(response);
        User badUser = new User(user.getEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        Response responseBadUser = userRequest.createUser(badUser);//пытаемся сделать юзера еще раз
        responseBadUser.then() //проверяем ответ
                .assertThat().statusCode(403)
                .and()
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Попытка создать нового пользователя без почты")
    @Description("Проверяем тело ответа и статускод 403")
    public void checkCreateUserWithoutEmailFieldReturnForbidden() throws InterruptedException {
        user.setEmail(null);
        Response response = userRequest.createUser(user);
        response.then()
                .assertThat().statusCode(403)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Попытка создать нового пользователя без пароля")
    @Description("Проверяем тело ответа и статускод 403")
    public void checkCreateUserWithoutPasswordFieldReturnForbidden() throws InterruptedException {
        user.setPassword(null);
        Response response = userRequest.createUser(user);
        response.then()
                .assertThat().statusCode(403)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Попытка создать нового пользователя без имени")
    @Description("Проверяем тело ответа и статускод 403")
    public void checkCreateUserWithoutNameFieldReturnForbidden() throws InterruptedException {
        user.setName(null);
        Response response = userRequest.createUser(user);
        response.then()
                .assertThat().statusCode(403)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void deleteUser() throws InterruptedException {
        userRequest.deleteUser(userRequest.getAccessTkn());
    }
}
