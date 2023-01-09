import User.DataGenForUser;
import User.User;
import User.UserRequest;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeUserDataTest {
    @Test
    public void checkChangeUserEmailWithAuthReturnOk(){
        //Создать юзера
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        userRequest.createUser(user);
        //Залогинить юзера, получить токен
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
    public void checkChangeUserPasswordWithAuthReturnOk(){
        //Создать юзера
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        userRequest.createUser(user);
        //Залогинить юзера, получить токен
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
    //Баг  - приходит 200 ОК
    public void checkChangeUserEmailWithoutAuthReturnUnauthorized(){
        //Создать юзера
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        Response response = userRequest.createUser(user);
        //получить токен
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
    //Баг  - приходит 200 ОК
    public void checkChangeUserPasswordWithoutAuthReturnUnauthorized(){
        //Создать юзера
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        Response response = userRequest.createUser(user);
        //получить токен
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
    public void checkChangeUserAlreadyExistEmailReturnForbidden(){
        //Создать юзера
        DataGenForUser dataGenForUser = new DataGenForUser();
        String email = dataGenForUser.generateEmail();
        User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        User oldUser = new User(email, dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        userRequest.createUser(oldUser);
        userRequest.createUser(user);
        //получить токен
        Response response = userRequest.loginUser(user);
        String accessTkn = userRequest.getUserAccessTkn(response);
        //изменить данные юзера
        User newEmailUser = new User(email, null, null);
        //проверить что данные не изменились 403
        Response response_patch = userRequest.changeUserData(newEmailUser, accessTkn);
        //System.out.println(response_patch.toString());
        response_patch.then()
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
    }
}
