import User.DataGenForUser;
import User.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import User.UserRequest;
import static org.hamcrest.CoreMatchers.equalTo;

public class LoginUserTest {
    DataGenForUser dataGenForUser = new DataGenForUser();
    User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
    UserRequest userRequest = new UserRequest();

    @Before
    public void setUp() throws InterruptedException {
        Thread.sleep(2000);
    }
    @Test
    @DisplayName("Логин пользователя с корректными данными")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkLoginCorrectUser() {
        userRequest.createUser(user);
        Response response = userRequest.loginUser(user);
        response.then()
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        String accessTkn = userRequest.getUserAccessTkn(response);
        userRequest.deleteUser(accessTkn);
    }
    @Test
    @DisplayName("Логин пользователя с неверной почтой")
    @Description("Проверяем тело ответа и статускод 401")
    public void checkLoginUserWithWrongEmail(){
        Response response = userRequest.createUser(user);
        User userWithInvalidEmail = new User(dataGenForUser.generateEmail(), user.getPassword());
        userRequest.loginUser(userWithInvalidEmail).then()
                .assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
        String accessTkn = userRequest.getUserAccessTkn(response);
        userRequest.deleteUser(accessTkn);
    }
    @Test
    @DisplayName("Логин пользователя с неверным паролем")
    @Description("Проверяем тело ответа и статускод 401")
    public void checkLoginUserWithWrongPassword(){
        Response response = userRequest.createUser(user);
        User userWithInvalidEmail = new User(user.getEmail(), dataGenForUser.generatePassword());
        userRequest.loginUser(userWithInvalidEmail).then()
                .assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
        String accessTkn = userRequest.getUserAccessTkn(response);
        userRequest.deleteUser(accessTkn);
    }
    @Test
    @DisplayName("Логин пользователя без почты")
    @Description("Проверяем тело ответа и статускод 401")
    public void checkLoginUserWithoutEmailField(){
        Response response = userRequest.createUser(user);
        User userWithInvalidEmail = new User(null, user.getPassword());
        userRequest.loginUser(userWithInvalidEmail).then()
                .assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
        String accessTkn = userRequest.getUserAccessTkn(response);
        userRequest.deleteUser(accessTkn);
    }
    @Test
    @DisplayName("Логин пользователя без пароля")
    @Description("Проверяем тело ответа и статускод 401")
    public void checkLoginUserWithoutPasswordField(){
        Response response = userRequest.createUser(user);
        User userWithInvalidEmail = new User(user.getEmail(), null);
        userRequest.loginUser(userWithInvalidEmail).then()
                .assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
        String accessTkn = userRequest.getUserAccessTkn(response);
        userRequest.deleteUser(accessTkn);
    }
}
