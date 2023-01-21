import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import user.DataGenForUser;
import user.User;
import user.UserRequest;

import static org.hamcrest.CoreMatchers.equalTo;

public class LoginUserTest {
    DataGenForUser dataGenForUser = new DataGenForUser();
    User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
    UserRequest userRequest = new UserRequest();

    @Test
    @DisplayName("Логин пользователя с корректными данными")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkLoginCorrectUser() throws InterruptedException {
        userRequest.createUser(user);
        Response response = userRequest.loginUser(user);
        userRequest.setAccessTkn(response);
        response.then()
                .assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Логин пользователя с неверной почтой")
    @Description("Проверяем тело ответа и статускод 401")
    public void checkLoginUserWithWrongEmail() throws InterruptedException {
        Response response = userRequest.createUser(user);
        userRequest.setAccessTkn(response);
        User userWithInvalidEmail = new User(dataGenForUser.generateEmail(), user.getPassword());
        userRequest.loginUser(userWithInvalidEmail).then()
                .assertThat().statusCode(401)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин пользователя с неверным паролем")
    @Description("Проверяем тело ответа и статускод 401")
    public void checkLoginUserWithWrongPassword() throws InterruptedException {
        Response response = userRequest.createUser(user);
        userRequest.setAccessTkn(response);
        User userWithInvalidEmail = new User(user.getEmail(), dataGenForUser.generatePassword());
        userRequest.loginUser(userWithInvalidEmail).then()
                .assertThat().statusCode(401)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин пользователя без почты")
    @Description("Проверяем тело ответа и статускод 401")
    public void checkLoginUserWithoutEmailField() throws InterruptedException {
        Response response = userRequest.createUser(user);
        userRequest.setAccessTkn(response);
        User userWithInvalidEmail = new User(null, user.getPassword());
        userRequest.loginUser(userWithInvalidEmail).then()
                .assertThat().statusCode(401)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин пользователя без пароля")
    @Description("Проверяем тело ответа и статускод 401")
    public void checkLoginUserWithoutPasswordField() throws InterruptedException {
        Response response = userRequest.createUser(user);
        userRequest.setAccessTkn(response);
        User userWithInvalidEmail = new User(user.getEmail(), null);
        userRequest.loginUser(userWithInvalidEmail).then()
                .assertThat().statusCode(401)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void deleteUser() throws InterruptedException {
        userRequest.deleteUser(userRequest.getAccessTkn());
    }
}
