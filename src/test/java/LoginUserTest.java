import User.DataGenForUser;
import User.User;
import org.junit.Test;
import User.UserRequest;
import static org.hamcrest.CoreMatchers.equalTo;

public class LoginUserTest {
    @Test
    public void checkLoginCorrectUser(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        userRequest.createUser(user);
        userRequest.loginUser(user).then()
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }
    @Test
    public void checkLoginUserWithWrongEmail(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        String password = dataGenForUser.generatePassword();
        User user = new User(dataGenForUser.generateEmail(), password, dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        userRequest.createUser(user);
        User userWithInvalidEmail = new User(dataGenForUser.generateEmail(), password);
        userRequest.loginUser(userWithInvalidEmail).then()
                .assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
    }
    @Test
    public void checkLoginUserWithWrongPassword(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        String email = dataGenForUser.generateEmail();
        User user = new User(email, dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        userRequest.createUser(user);
        User userWithInvalidEmail = new User(email, dataGenForUser.generatePassword());
        userRequest.loginUser(userWithInvalidEmail).then()
                .assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
    }
}
