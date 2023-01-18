package order;

import User.User;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderRequest {
    private final String uri = "https://stellarburgers.nomoreparties.site/";
    private final String orderApi = "api/orders";
    public Response createOrder(Order order, String accessTkn){
        return given().log().all()
                .header("Content-type", "application/json")
                .auth().oauth2(accessTkn.replace("Bearer ", ""))
                .when()
                .baseUri(uri)
                .body(order)
                .when()
                .post(orderApi);
    }
    public Response getUserOrder(String accessTkn){
        return given().log().all()
                .header("Content-type", "application/json")
                .auth().oauth2(accessTkn.replace("Bearer ", ""))
                .when()
                .baseUri(uri)
                .when()
                .get(orderApi);
    }
    public OrderResponse getUserOrderList(String accessTkn){
        return given().log().all()
                .header("Content-type", "application/json")
                .auth().oauth2(accessTkn.replace("Bearer ", ""))
                .when()
                .baseUri(uri)
                .when()
                .get(orderApi)
                .body()
                .as(OrderResponse.class);
    }
    public int checkUserOrdersId(OrderResponse orderResponse){
        List<OrderData> orderDataList = orderResponse.getOrders();
        OrderData orderDataItem;
        int idCount = 0;
        for (OrderData orderData : orderDataList) {
            orderDataItem = orderData;
            if (orderDataItem.get_id() != null) {
                idCount++;
            }
        }
        return idCount;
    }
}
