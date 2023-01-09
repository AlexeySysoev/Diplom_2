import User.DataGenForUser;
import User.User;
import User.UserRequest;
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

        //Залогинить юзера
        //получить токен
        String accessTkn = userRequest.getUserAccessTkn(user);
        User newEmailUser = new User(dataGenForUser.generateEmail(), null, null);
        //изменить данные юзера
        userRequest.changeUserData(newEmailUser, accessTkn).then()
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        //проверить что данные изменились
    }
    public void checkChangeUserDataWithoutAuthReturnUnauthorized(){
        //Создать юзера
        //получить токен
        //изменить данные юзера
        //проверить что данные не изменились 401
    }
}
