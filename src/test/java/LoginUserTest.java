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
    @Before
    public void setUp() throws InterruptedException {
        Thread.sleep(2000);
    }
    @Test
    @DisplayName("Логин пользователя с корректными данными")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkLoginCorrectUser(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
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
        DataGenForUser dataGenForUser = new DataGenForUser();
        String password = dataGenForUser.generatePassword();
        User user = new User(dataGenForUser.generateEmail(), password, dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        Response response = userRequest.createUser(user);
        User userWithInvalidEmail = new User(dataGenForUser.generateEmail(), password);
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
        DataGenForUser dataGenForUser = new DataGenForUser();
        String email = dataGenForUser.generateEmail();
        User user = new User(email, dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        Response response = userRequest.createUser(user);
        User userWithInvalidEmail = new User(email, dataGenForUser.generatePassword());
        userRequest.loginUser(userWithInvalidEmail).then()
                .assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
        String accessTkn = userRequest.getUserAccessTkn(response);
        userRequest.deleteUser(accessTkn);
    }
    //логин без почты
    @Test
    @DisplayName("Логин пользователя без почты")
    @Description("Проверяем тело ответа и статускод 401")
    public void checkLoginUserWithoutEmailField(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        String password = dataGenForUser.generatePassword();
        User user = new User(dataGenForUser.generateEmail(), password, dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        Response response = userRequest.createUser(user);
        User userWithInvalidEmail = new User(null, password);
        userRequest.loginUser(userWithInvalidEmail).then()
                .assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
        String accessTkn = userRequest.getUserAccessTkn(response);
        userRequest.deleteUser(accessTkn);
    }
    //логин без пароля
    @Test
    @DisplayName("Логин пользователя без пароля")
    @Description("Проверяем тело ответа и статускод 401")
    public void checkLoginUserWithoutPasswordField(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        String email = dataGenForUser.generateEmail();
        User user = new User(email, dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        Response response = userRequest.createUser(user);
        User userWithInvalidEmail = new User(email, null);
        userRequest.loginUser(userWithInvalidEmail).then()
                .assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
        String accessTkn = userRequest.getUserAccessTkn(response);
        userRequest.deleteUser(accessTkn);
    }
}
