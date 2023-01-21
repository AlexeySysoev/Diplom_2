package order;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import specs.Specs;

import java.util.List;


public class OrderRequest extends Specs {
    private final String uri = "https://stellarburgers.nomoreparties.site/";
    private final String orderApi = "api/orders";

    @Step("Создание заказа пользователя")
    public Response createOrder(Order order, String accessTkn) throws InterruptedException {
        return startSpec()
                .auth().oauth2(accessTkn.replace("Bearer ", ""))
                .when()
                .baseUri(uri)
                .body(order)
                .when()
                .post(orderApi);
    }

    @Step("Получение заказа пользователя")
    public Response getUserOrder(String accessTkn) throws InterruptedException {
        return startSpec()
                .auth().oauth2(accessTkn.replace("Bearer ", ""))
                .when()
                .baseUri(uri)
                .when()
                .get(orderApi);
    }

    @Step("Получение списка заказов пользователя")
    public OrderResponse getUserOrderList(String accessTkn) throws InterruptedException {
        return startSpec()
                .auth().oauth2(accessTkn.replace("Bearer ", ""))
                .when()
                .baseUri(uri)
                .when()
                .get(orderApi)
                .body()
                .as(OrderResponse.class);
    }

    @Step("Проверка id заказов пользователя")
    public int checkUserOrdersId(OrderResponse orderResponse) {
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
