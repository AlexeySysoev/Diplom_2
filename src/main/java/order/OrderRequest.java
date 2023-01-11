package order;

import User.User;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderRequest {
    private final String uri = "https://stellarburgers.nomoreparties.site/";
    private final String createOrder = "api/orders";
    public Response createOrder(Order order, String accessTkn){
        return given().log().all()
                .header("Content-type", "application/json")
                .auth().oauth2(accessTkn.replace("Bearer ", ""))
                .when()
                .baseUri(uri)
                .body(order)
                .when()
                .post(createOrder);

    }
}
