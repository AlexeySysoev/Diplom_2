import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import order.Order;
import order.OrderRequest;
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
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrderTest {
    private DataGenForUser dataGenForUser = new DataGenForUser();
    private UserRequest userRequest = new UserRequest();
    private User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
    private OrderRequest orderRequest = new OrderRequest();
    private List<String> ingredients = new ArrayList<>(); //Лист с ингредиентами

    @Before
    public void setUp() {
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("61c0c5a71d1f82001bdaaa6f");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией пользователя и ингредиентами")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkCreateOrderContainIngredientsWithAuthReturnOk() throws InterruptedException {
        userRequest.createUser(user);
        Response responseUser = userRequest.loginUser(user);
        userRequest.setAccessTkn(responseUser);
        Order order = new Order(ingredients);
        Response response = orderRequest.createOrder(order, userRequest.getAccessTkn());
        response.then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с авторизацией пользователя без ингредиентов")
    @Description("Проверяем тело ответа и статус код 400")
    public void checkCreateOrderEmptyIngredientsWithAuthReturnOk() throws InterruptedException {
        userRequest.createUser(user);
        Response responseUser = userRequest.loginUser(user);
        userRequest.setAccessTkn(responseUser);
        List<String> emptyIngredients = new ArrayList<>();
        Order order = new Order(emptyIngredients);
        Response response = orderRequest.createOrder(order, userRequest.getAccessTkn());
        response.then()
                .assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией пользователя c неверным хешем ингредиентов")
    @Description("Проверяем тело ответа и статус код 500")
    @Issue("Bug-004, приходит 400")
    public void checkCreateOrderInvalidIngredientsWithAuthReturnServerError() throws InterruptedException {
        userRequest.createUser(user);
        Response responseUser = userRequest.loginUser(user);
        userRequest.setAccessTkn(responseUser);
        Order order = new Order(ingredients);
        Response response = orderRequest.createOrder(order, userRequest.getAccessTkn());
        response.then().assertThat().statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа без авторизации пользователя")
    @Description("Проверяем редирект на api/login (ожидаем статус код серии 300)")
    @Issue("Bug-005")
    public void checkCreateOrderWithoutAuthMadeRedirectToLogin() throws InterruptedException {
        Response responseUser = userRequest.createUser(user);
        userRequest.setAccessTkn(responseUser);
        Order order = new Order(ingredients);
        Response response = orderRequest.createOrder(order, userRequest.getAccessTkn());
        int statusCode = response.then().extract().statusCode();
        Assert.assertTrue(statusCode >= 300 && statusCode < 400);
    }

    @After
    public void deleteUser() throws InterruptedException {
        userRequest.deleteUser(userRequest.getAccessTkn());
    }
}
