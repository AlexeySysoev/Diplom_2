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
    public String getUserAccessTkn(User user){
        Response response = given().log().all()
                .header("Content-type", "application/json")
                .baseUri(uri)
                .body(user)
                .when()
                .post(login);
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
                .header("Authorization", accessTkn)
                .baseUri(uri)
                .body(user)
                .when()
                .patch(userData);
    }
}
