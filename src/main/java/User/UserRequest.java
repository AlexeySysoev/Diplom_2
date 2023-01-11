package User;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
public class UserRequest {
    private final String uri = "https://stellarburgers.nomoreparties.site/";
    private final String register = "api/auth/register";
    private final String login = "api/auth/login";
    private final String userData = "api/auth/user";
    public Response createUser(User user){
        return given().log().all()
                .header("Content-type", "application/json")
                .baseUri(uri)
                .body(user)
                .when()
                .post(register);
    }
    public Response loginUser(User user){
        return given().log().all()
                .header("Content-type", "application/json")
                .baseUri(uri)
                .body(user)
                .when()
                .post(login);
    }

    public String getUserAccessTkn(Response response){
        return response.then().extract()
                .path("accessToken");
    }
    public Response getUserData(String accessTkn){
        return given().log().all()
                .header("Authorization", accessTkn)
                .baseUri(uri)
                .when()
                .get(userData);
    }
    public Response changeUserData(User user, String accessTkn){
        return given().log().all()
                .header("Content-type", "application/json")
                .auth().oauth2(accessTkn.replace("Bearer ", ""))
                .when()
                .baseUri(uri)
                .body(user)
                .when()
                .patch(userData);
    }
    public void deleteUser(String accessTkn){
        given().log().all()
                .header("Content-type", "application/json")
                .auth().oauth2(accessTkn.replace("Bearer ", ""))
                .when()
                .baseUri(uri)
                .delete(userData)
                .then()
                .assertThat().statusCode(202);
    }
    public void deleteInvalidUser(Response response){
        UserRequest userRequest = new UserRequest();
        if (response.then().extract().statusCode() == 200 || response.then().extract().statusCode()==201){
            userRequest.deleteUser(userRequest.getUserAccessTkn(response));
        }
    }
}
