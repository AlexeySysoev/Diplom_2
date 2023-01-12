import User.DataGenForUser;
import User.User;
import User.UserRequest;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import order.Order;
import order.OrderRequest;
import order.OrderResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
    public void setUp() throws InterruptedException {
        Thread.sleep(2000);
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("61c0c5a71d1f82001bdaaa6f");
    }
    @Test
    @DisplayName("Получение списка заказов пользователя с авторизацией")
    @Description("Проверяем id заказов и статус код 200")
    public void getOrderListWithAuthUserReturnOrders(){
        Order order = new Order(ingredients);
        userRequest.createUser(user);
        Response responseUser = userRequest.loginUser(user);
        String accessTkn = userRequest.getUserAccessTkn(responseUser);
        orderRequest.createOrder(order, accessTkn);
        OrderResponse orderResponse = orderRequest.getUserOrderList(accessTkn);
        int statusCode = orderRequest.getUserOrder(accessTkn).statusCode();
        Assert.assertTrue(orderRequest.checkUserOrdersId(orderResponse) > 0
                            && statusCode == 200);
        userRequest.deleteUser(accessTkn);
    }
    @Test
    @DisplayName("Получение списка заказов пользователя без авторизации")
    @Description("Проверяем тело ответа и статус код 401")
    public void getOrderListWithoutAuthUserReturnUnauthorized(){
        String accessTkn = "";
        Response response = orderRequest.getUserOrder(accessTkn);
        response.then().assertThat().body("message", equalTo("You should be authorised"))
                .and().statusCode(401);
    }
}
