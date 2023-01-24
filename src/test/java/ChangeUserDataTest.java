import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import user.DataGenForUser;
import user.User;
import user.UserRequest;

import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeUserDataTest {
    private DataGenForUser dataGenForUser = new DataGenForUser();
    private UserRequest userRequest = new UserRequest();
    private User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());

    @Test
    @DisplayName("Меняем почту пользователя с логином в системе")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkChangeUserEmailWithAuthReturnOk() throws InterruptedException {
        userRequest.createUser(user);
        Response response = userRequest.loginUser(user);
        userRequest.setAccessTkn(response);
        User newEmailUser = new User(dataGenForUser.generateEmail(), null, null);
        userRequest.changeUserData(newEmailUser, userRequest.getAccessTkn()).then()
                .assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Меняем пароль пользователя с логином в системе")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkChangeUserPasswordWithAuthReturnOk() throws InterruptedException {
        userRequest.createUser(user);
        Response response = userRequest.loginUser(user);
        userRequest.setAccessTkn(response);
        User newPasswordUser = new User(null, dataGenForUser.generatePassword(), null);
        Response responsePatch = userRequest.changeUserData(newPasswordUser, userRequest.getAccessTkn());
        responsePatch.then()
                .assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Меняем имя пользователя с логином в системе")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkChangeUserNameWithAuthReturnOk() throws InterruptedException {
        userRequest.createUser(user);
        Response response = userRequest.loginUser(user);
        userRequest.setAccessTkn(response);
        User newPasswordUser = new User(null, null, dataGenForUser.generateName());
        Response responsePatch = userRequest.changeUserData(newPasswordUser, userRequest.getAccessTkn());
        responsePatch.then()
                .assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Попытка сменить почту пользователя без логина в системе")
    @Description("Проверяем тело ответа и статускод 401")
    @Issue("BUG-001, Баг  - приходит 200 ОК")
    public void checkChangeUserEmailWithoutAuthReturnUnauthorized() throws InterruptedException {
        Response response = userRequest.createUser(user);
        userRequest.setAccessTkn(response);
        User newEmailUser = new User(dataGenForUser.generateEmail(), null, null);
        Response responsePatch = userRequest.changeUserData(newEmailUser, userRequest.getAccessTkn());
        responsePatch.then()
                .assertThat().statusCode(401)
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Попытка сменить пароль пользователя без логина в системе")
    @Description("Проверяем тело ответа и статускод 401")
    @Issue("BUG-002, Баг  - приходит 200 ОК")
    public void checkChangeUserPasswordWithoutAuthReturnUnauthorized() throws InterruptedException {
        Response response = userRequest.createUser(user);
        userRequest.setAccessTkn(response);
        User newPasswordUser = new User(null, dataGenForUser.generatePassword(), null);
        Response responsePatch = userRequest.changeUserData(newPasswordUser, userRequest.getAccessTkn());
        responsePatch.then()
                .assertThat().statusCode(401)
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Попытка сменить имя пользователя без логина в системе")
    @Description("Проверяем тело ответа и статускод 401")
    @Issue("BUG-003, Баг  - приходит 200 ОК")
    public void checkChangeUserNameWithoutAuthReturnUnauthorized() throws InterruptedException {
        Response response = userRequest.createUser(user);
        userRequest.setAccessTkn(response);
        User newPasswordUser = new User(null, null, dataGenForUser.generateName());
        Response responsePatch = userRequest.changeUserData(newPasswordUser, userRequest.getAccessTkn());
        responsePatch.then()
                .assertThat().statusCode(401)
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Попытка сменить почту пользователя на уже существующую в системе")
    @Description("Проверяем тело ответа и статускод 403")
    public void checkChangeUserAlreadyExistEmailReturnForbidden() throws InterruptedException {
        User oldUser = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        userRequest.createUser(oldUser);
        userRequest.createUser(user);
        Response response = userRequest.loginUser(user);
        userRequest.setAccessTkn(response);
        User newEmailUser = new User(oldUser.getEmail(), null, null);//формируем JSON с полем почты
        Response responsePatch = userRequest.changeUserData(newEmailUser, userRequest.getAccessTkn());
        responsePatch.then()
                .assertThat().statusCode(403)
                .and()
                .body("success", equalTo(false));
    }

    @After
    public void deleteUser() throws InterruptedException {
        userRequest.deleteUser(userRequest.getAccessTkn());
    }
}
