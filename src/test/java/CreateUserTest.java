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
    @Before
    public void setUp() throws InterruptedException {
        Thread.sleep(2000); //Спасаемся от кода 429
    }
    @Test
    @DisplayName("Создаем нового пользователя с уникальными данными")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkCreateNewUniqueUser(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
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
    public void checkCreateAlreadyExistUserReturnForbidden(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        Response response = userRequest.createUser(user);//делаем первого юзера
        Response response1 = userRequest.createUser(user);//пытаемся сделать юзера еще раз
        response1.then() //проверяем ответ
                .assertThat().body("message", equalTo("User already exists"))
                .and()
                .statusCode(403);
        String accessTkn = userRequest.getUserAccessTkn(response);
        userRequest.deleteUser(accessTkn);
        userRequest.deleteInvalidUser(response1);
    }
    @Test
    @DisplayName("Попытка создать нового пользователя без почты")
    @Description("Проверяем тело ответа и статускод 403")
    public void checkCreateUserWithoutEmailFieldReturnForbidden(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(null, dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        Response response = userRequest.createUser(user);
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
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), null, dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
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
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), null);
        UserRequest userRequest = new UserRequest();
        Response response = userRequest.createUser(user);
        response.then()
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
        userRequest.deleteInvalidUser(response);
    }
}
