import User.User;
import User.DataGenForUser;
import org.junit.Test;
import User.UserRequest;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {
    @Test
    public void checkCreateNewUniqueUser(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        userRequest.createUser(user).then()
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }
    @Test
    public void checkCreateAllreadyExistUserReturnForbidden(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        userRequest.createUser(user);
        userRequest.createUser(user).then()
                .assertThat().body("message", equalTo("User already exists"))
                .and()
                .statusCode(403);
    }
    @Test
    public void checkCreateUserWithoutEmailFieldReturnForbidden(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(null, dataGenForUser.generatePassword(), dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        userRequest.createUser(user).then()
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
    }
    @Test
    public void checkCreateUserWithoutPasswordFieldReturnForbidden(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), null, dataGenForUser.generateName());
        UserRequest userRequest = new UserRequest();
        userRequest.createUser(user).then()
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
    }
    @Test
    public void checkCreateUserWithoutNameFieldReturnForbidden(){
        DataGenForUser dataGenForUser = new DataGenForUser();
        User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), null);
        UserRequest userRequest = new UserRequest();
        userRequest.createUser(user).then()
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
    }
}
