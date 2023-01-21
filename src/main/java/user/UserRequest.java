package user;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import specs.Specs;


public class UserRequest extends Specs {
    private final String uri = "https://stellarburgers.nomoreparties.site/";
    private final String register = "api/auth/register";
    private final String login = "api/auth/login";
    private final String userData = "api/auth/user";
    private String accessTkn;

    @Step("Получение токена пользователя")
    public String getAccessTkn() {
        return accessTkn;
    }

    @Step("Извлечение токена пользователя")
    public void setAccessTkn(Response response) {
        this.accessTkn = response.then().extract()
                .path("accessToken");
    }

    @Step("Регистрация пользователя в системе")
    public Response createUser(User user) throws InterruptedException {
        return startSpec()
                .baseUri(uri)
                .body(user)
                .when()
                .post(register);
    }

    @Step("Авторизация пользователя в системе")
    public Response loginUser(User user) throws InterruptedException {
        return startSpec()
                .baseUri(uri)
                .body(user)
                .when()
                .post(login);
    }

    @Step("Получение токена пользователя")
    public String getUserAccessTkn(Response response) {
        return response.then().extract()
                .path("accessToken");
    }

    @Step("Изменить учетные данные пользователя")
    public Response changeUserData(User user, String accessTkn) throws InterruptedException {
        return startSpec()
                .auth().oauth2(accessTkn.replace("Bearer ", ""))
                .when()
                .baseUri(uri)
                .body(user)
                .when()
                .patch(userData);
    }

    @Step("Удаление пользователя из системы")
    public void deleteUser(String accessTkn) throws InterruptedException {
        if (accessTkn != null) {
            startSpec()
                    .auth().oauth2(accessTkn.replace("Bearer ", ""))
                    .when()
                    .baseUri(uri)
                    .delete(userData)
                    .then()
                    .assertThat().statusCode(202);
        }
    }
}
