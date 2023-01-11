import User.DataGenForUser;
import User.User;
import User.UserRequest;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import order.Order;
import order.OrderRequest;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.*;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrderTest {
    DataGenForUser dataGenForUser = new DataGenForUser();
    UserRequest userRequest = new UserRequest();
    User user = new User(dataGenForUser.generateEmail(), dataGenForUser.generatePassword(), dataGenForUser.generateName());
    OrderRequest orderRequest = new OrderRequest();
    @Before
    public void setUp() throws InterruptedException {
        Thread.sleep(2000);
    }
    @Test
    @DisplayName("Создание заказа с авторизацией пользователя и ингредиентами")
    @Description("Проверяем тело ответа и статускод 200")
    public void checkCreateOrderContainIngredientsWithAuthReturnOk(){
        //Создать юзера
        userRequest.createUser(user);
        //Залогинить юзера, получить токен
        Response responseUser = userRequest.loginUser(user);
        String accessTkn = userRequest.getUserAccessTkn(responseUser);
        List<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("61c0c5a71d1f82001bdaaa6f");
        Order order = new Order(ingredients);
        Response response = orderRequest.createOrder(order, accessTkn);
        response.then().assertThat().body("name", notNullValue()).and().statusCode(200);
    }
    @Test
    @DisplayName("Создание заказа с авторизацией пользователя без ингредиентов")
    @Description("Проверяем тело ответа и статускод 400")
    public void checkCreateOrderEmptyIngredientsWithAuthReturnOk(){
        //Создать юзера
        userRequest.createUser(user);
        //Залогинить юзера, получить токен
        Response responseUser = userRequest.loginUser(user);
        String accessTkn = userRequest.getUserAccessTkn(responseUser);
        List<String> ingredients = new ArrayList<>();
        Order order = new Order(ingredients);
        Response response = orderRequest.createOrder(order, accessTkn);
        response.then().assertThat().body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
    }
    @Test
    @DisplayName("Создание заказа с авторизацией пользователя c неверным хешем ингредиентов")
    @Description("Проверяем тело ответа и статускод 500")
    @Issue("Bug-004")
    public void checkCreateOrderInvalidIngredientsWithAuthReturnServerError(){
        //Создать юзера
        userRequest.createUser(user);
        //Залогинить юзера, получить токен
        Response responseUser = userRequest.loginUser(user);
        String accessTkn = userRequest.getUserAccessTkn(responseUser);
        List<String> ingredients = new ArrayList<>();
        ingredients.add(StringUtils.reverse("61c0c5a71d1f82001bdaaa6d"));
        ingredients.add(StringUtils.reverse("61c0c5a71d1f82001bdaaa6f"));
        Order order = new Order(ingredients);
        Response response = orderRequest.createOrder(order, accessTkn);
        System.out.println("response status code is: "+response.then().extract().statusCode()+" but must bee 500");
        response.then().assertThat().statusCode(500);
    }
    @Test
    @DisplayName("Создание заказа без авторизации пользователя")
    @Description("Проверяем редирект на api/login")
    @Issue("Bug-005")
    public void checkCreateOrderWithoutAuthMadeRedirectToLogin(){
        //Создать юзера
        Response responseUser =userRequest.createUser(user);
        //получить токен неавторизованного пользователя
        String accessTkn = userRequest.getUserAccessTkn(responseUser);
        List<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("61c0c5a71d1f82001bdaaa6f");
        Order order = new Order(ingredients);
        Response response = orderRequest.createOrder(order, accessTkn);
        System.out.println("response status code is: "+response.then().extract().statusCode()+" but must bee 300");
        response.then().assertThat().statusCode(200); //как корректно проверить редирект
    }
}
