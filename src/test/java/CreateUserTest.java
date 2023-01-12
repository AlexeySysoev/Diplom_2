import User.User;
import User.DataGenForUser;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import User.UserRequest;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {
    DataGenForUser dataGenForUser = new DataGenForUser();
    User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
    UserRequest userRequest = new UserRequest();
    @Before
    public void setUp() throws InterruptedException {
        Thread.sleep(2000); //Спасаемся от кода 429
    }
    @Test
    @DisplayName("Создаем нового пользователя с уникальными данными")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkCreateNewUniqueUser(){

        Response response = userRequest.createUser(user);
        response.then()
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        String accessTkn = userRequest.getUserAccessTkn(response);
        userRequest.deleteUser(accessTkn);
    }
    @Test
    @DisplayName("Попытка создать нового пользователя с уже использующейся почтой")
    @Description("Проверяем тело ответа и статускод 403")
    public void checkCreateAlreadyExistUserReturnForbidden() throws InterruptedException {
        Response response = userRequest.createUser(user);//делаем первого юзера
        Thread.sleep(2000);
        User badUser = new User(user.getEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        Response response_badUser = userRequest.createUser(badUser);//пытаемся сделать юзера еще раз
        Thread.sleep(2000);
        response_badUser.then() //проверяем ответ
                .assertThat().body("message", equalTo("User already exists"))
                .and()
                .statusCode(403);
        String accessTkn = userRequest.getUserAccessTkn(response);
        userRequest.deleteUser(accessTkn);
        Thread.sleep(2000);
        userRequest.deleteInvalidUser(response_badUser);
    }
    @Test
    @DisplayName("Попытка создать нового пользователя без почты")
    @Description("Проверяем тело ответа и статускод 403")
    public void checkCreateUserWithoutEmailFieldReturnForbidden() throws InterruptedException {
        user.setEmail(null);
        Response response = userRequest.createUser(user);
        Thread.sleep(2000);
        response.then()
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
        userRequest.deleteInvalidUser(response);
    }
    @Test
    @DisplayName("Попытка создать нового пользователя без пароля")
    @Description("Проверяем тело ответа и статускод 403")
    public void checkCreateUserWithoutPasswordFieldReturnForbidden(){
        user.setPassword(null);
        Response response = userRequest.createUser(user);
        response.then()
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
        userRequest.deleteInvalidUser(response);
    }
    @Test
    @DisplayName("Попытка создать нового пользователя без имени")
    @Description("Проверяем тело ответа и статускод 403")
    public void checkCreateUserWithoutNameFieldReturnForbidden(){
        user.setName(null);
        Response response = userRequest.createUser(user);
        response.then()
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
        userRequest.deleteInvalidUser(response);
    }
}
