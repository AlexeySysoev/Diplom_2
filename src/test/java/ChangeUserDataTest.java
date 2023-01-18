import User.DataGenForUser;
import User.User;
import User.UserRequest;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeUserDataTest {
    DataGenForUser dataGenForUser = new DataGenForUser();
    UserRequest userRequest = new UserRequest();
    User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
    @Before
    public void setUp() throws InterruptedException {
       Thread.sleep(2000);
    }
    @Test
    @DisplayName("Меняем почту пользователя с логином в системе")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkChangeUserEmailWithAuthReturnOk(){
        userRequest.createUser(user);
        Response response = userRequest.loginUser(user);
        String accessTkn = userRequest.getUserAccessTkn(response);
        //изменить данные юзера, проверить что данные изменились
        User newEmailUser = new User(dataGenForUser.generateEmail(), null, null);
        userRequest.changeUserData(newEmailUser, accessTkn).then()
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }
    @Test
    @DisplayName("Меняем пароль пользователя с логином в системе")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkChangeUserPasswordWithAuthReturnOk(){
        userRequest.createUser(user);
        Response response = userRequest.loginUser(user);
        String accessTkn = userRequest.getUserAccessTkn(response);
        User newPasswordUser = new User(null, dataGenForUser.generatePassword(), null);
        //изменить данные юзера, проверить что данные изменились
        Response response_patch = userRequest.changeUserData(newPasswordUser, accessTkn);
        response_patch.then()
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }
    @Test
    @DisplayName("Меняем имя пользователя с логином в системе")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkChangeUserNameWithAuthReturnOk(){
        userRequest.createUser(user);
        Response response = userRequest.loginUser(user);
        String accessTkn = userRequest.getUserAccessTkn(response);
        User newPasswordUser = new User(null, null, dataGenForUser.generateName());
        //изменить данные юзера, проверить что данные изменились
        Response response_patch = userRequest.changeUserData(newPasswordUser, accessTkn);
        response_patch.then()
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }
    @Test
    @DisplayName("Попытка сменить почту пользователя без логина в системе")
    @Description("Проверяем тело ответа и статускод 401")
    @Issue("BUG-001, Баг  - приходит 200 ОК")
    public void checkChangeUserEmailWithoutAuthReturnUnauthorized(){
        Response response = userRequest.createUser(user);
        String accessTkn = userRequest.getUserAccessTkn(response);
        //изменить данные юзера
        User newEmailUser = new User(dataGenForUser.generateEmail(), null, null);
        //проверить что данные не изменились 401
        Response response_patch = userRequest.changeUserData(newEmailUser, accessTkn);
        response_patch.then()
                .assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }
    @Test
    @DisplayName("Попытка сменить пароль пользователя без логина в системе")
    @Description("Проверяем тело ответа и статускод 401")
    @Issue("BUG-002, Баг  - приходит 200 ОК")
    public void checkChangeUserPasswordWithoutAuthReturnUnauthorized(){
        Response response = userRequest.createUser(user);
        String accessTkn = userRequest.getUserAccessTkn(response);
        //изменить данные юзера
        User newPasswordUser = new User(null, dataGenForUser.generatePassword(), null);
        //проверить что данные не изменились 401
        Response response_patch = userRequest.changeUserData(newPasswordUser, accessTkn);
        response_patch.then()
                .assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }
    @Test
    @DisplayName("Попытка сменить имя пользователя без логина в системе")
    @Description("Проверяем тело ответа и статускод 401")
    @Issue("BUG-003, Баг  - приходит 200 ОК")
    public void checkChangeUserNameWithoutAuthReturnUnauthorized(){
        Response response = userRequest.createUser(user);
        String accessTkn = userRequest.getUserAccessTkn(response);
        //изменить данные юзера
        User newPasswordUser = new User(null, null, dataGenForUser.generateName());
        //проверить что данные не изменились
        Response response_patch = userRequest.changeUserData(newPasswordUser, accessTkn);
        response_patch.then()
                .assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }
    @Test
    @DisplayName("Попытка сменить почту пользователя на уже существующую в системе")
    @Description("Проверяем тело ответа и статускод 403")
    public void checkChangeUserAlreadyExistEmailReturnForbidden(){
        User oldUser = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        userRequest.createUser(oldUser);
        userRequest.createUser(user);
        Response response = userRequest.loginUser(user);
        String accessTkn = userRequest.getUserAccessTkn(response);//получить токен
        //изменить данные юзера
        User newEmailUser = new User(oldUser.getEmail(), null, null);//формируем JSON с полем почты
        //проверить что данные не изменились
        Response response_patch = userRequest.changeUserData(newEmailUser, accessTkn);
        response_patch.then()
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
    }
}
