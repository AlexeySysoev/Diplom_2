import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import order.Order;
import order.OrderRequest;
import order.OrderResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import user.DataGenForUser;
import user.User;
import user.UserRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrderTest {
    DataGenForUser dataGenForUser = new DataGenForUser();
    UserRequest userRequest = new UserRequest();
    User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
    OrderRequest orderRequest = new OrderRequest();
    List<String> ingredients = new ArrayList<>(); //Лист с ингридиентами

    @Before
    public void setUp() {
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("61c0c5a71d1f82001bdaaa6f");
    }

    @Test
    @DisplayName("Получение списка заказов пользователя с авторизацией")
    @Description("Проверяем id заказов и статус код 200")
    public void getOrderListWithAuthUserReturnOrders() throws InterruptedException {
        Order order = new Order(ingredients);
        userRequest.createUser(user);
        Response responseUser = userRequest.loginUser(user);
        userRequest.setAccessTkn(responseUser);
        orderRequest.createOrder(order, userRequest.getAccessTkn());
        OrderResponse orderResponse = orderRequest.getUserOrderList(userRequest.getAccessTkn());
        int statusCode = orderRequest.getUserOrder(userRequest.getAccessTkn()).statusCode();
        Assert.assertTrue(statusCode == 200
                && orderRequest.checkUserOrdersId(orderResponse) > 0);
    }

    @Test
    @DisplayName("Получение списка заказов пользователя без авторизации")
    @Description("Проверяем тело ответа и статус код 401")
    public void getOrderListWithoutAuthUserReturnUnauthorized() throws InterruptedException {
        String accessTkn = "";
        Response response = orderRequest.getUserOrder(accessTkn);
        response.then().assertThat().body("message", equalTo("You should be authorised"))
                .and().statusCode(401);
    }

    @After
    public void deleteUser() throws InterruptedException {
        userRequest.deleteUser(userRequest.getAccessTkn());
    }
}
