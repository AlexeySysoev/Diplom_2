package specs;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class Specs {
    private final int DELAY = 500;

    public RequestSpecification startSpec() throws InterruptedException {
        Thread.sleep(DELAY);
        return given().log().all()
                .contentType(ContentType.JSON);
    }
}

