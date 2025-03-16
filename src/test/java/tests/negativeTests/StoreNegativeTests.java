package tests.negativeTests;

import config.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class StoreNegativeTests extends BaseTest {

    @Test(priority = 1, description = "Запрос заказа с несуществующим ID")
    public void testGetOrderByInvalidId() {
        Response response = given()
                .when()
                .get("/store/order/invalidStoreID")
                .then()
                .statusCode(404)
                .extract().response();

        System.out.println("Ответ API при попытке получения информации о заказе с несуществующим ID:");
        response.prettyPrint();

        String errorMessage = response.jsonPath().getString("message");
        Assert.assertTrue(errorMessage.contains("NumberFormatException"), "Некорректное сообщение об ошибке!");

        System.out.println("Ошибка при попытке получения информации о заказе с несуществующим ID! Статус-код: "
                + response.getStatusCode());

    }

    @Test(priority = 2, description = "Удаление несуществующего заказа")
    public void testDeleteNonExistentOrder() {
        String nonExistentOrderId = "99999999";
        Response response = given()
                .when()
                .delete("/store/order/" + nonExistentOrderId)
                .then()
                .extract().response();

        System.out.println("Ответ API при попытке удаления несуществующего заказа:");
        response.prettyPrint();

        int statusCode = response.getStatusCode();
        String errorMessage = response.jsonPath().getString("message");


        Assert.assertEquals(statusCode, 404, "Ожидался статус 404 при удалении несуществующего заказа!");

        Assert.assertEquals(errorMessage, "Order Not Found", "Ожидалось сообщение: 'Order Not Found', " +
                "но получено: " + errorMessage);


        System.out.println("При получении информации о несуществующем пользователе статус-код: " + statusCode +
                ". Ошибка: " + errorMessage);
    }
}
