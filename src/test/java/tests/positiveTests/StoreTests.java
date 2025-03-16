package tests.positiveTests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import config.BaseTest;
import models.Order;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import config.RetryAnalyzer;

public class StoreTests extends BaseTest {
    private Order order;
    private String orderId;

    @Test(priority = 1, description = "Создание нового заказа", retryAnalyzer = RetryAnalyzer.class)
    public void testCreateOrder() {
        JsonObject orderJsonObject = testData.getAsJsonObject("order");
        order = new Gson().fromJson(orderJsonObject, Order.class);

        Response response = given()
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post("/store/order")
                .then()
                .statusCode(200)
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "Ошибка при создании заказа. " +
                "Ожидался статус 200, но получен статус: " + response.getStatusCode());

        System.out.println("Ответ API при создании нового заказа:");
        response.prettyPrint();

        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            orderId = response.jsonPath().getString("id");
            Assert.assertNotNull(orderId, "Поле 'id' отсутствует в ответе!");
            System.out.println("Созданный ID заказа: " + orderId);
            System.out.println("Статус-код ответа: " + statusCode);

            Response getResponse = given()
                    .when()
                    .get("/store/order/" + order.getId())
                    .then()
                    .statusCode(200)
                    .extract().response();

            int getStatusCode = getResponse.getStatusCode();

            if (getStatusCode == 200) {
                Order retrievedOrder = getResponse.as(Order.class);

                Assert.assertEquals(retrievedOrder.getId(), order.getId(), "ID заказов не совпадают!");
                System.out.println("Проверка создания заказа с ID : " + retrievedOrder.getId() + ". Успешно создан.");
            } else {
                System.out.println("Ошибка при получении созданного заказа. Статус-код: " + getStatusCode);
                Assert.assertEquals(getStatusCode, 200, "Ошибка при получении созданного заказа. " +
                        "Ожидался статус 200, но получен статус: " + getStatusCode);
            }
        } else {
            System.out.println("Ошибка: Не удалось создать заказ. Статус-код: " + statusCode);
            System.out.println("Ответ сервера: " + response.getBody().asString());
            Assert.assertEquals(statusCode, 200, "Ошибка при создании заказа. " +
                    "Ожидался статус 200, но получен статус: " + statusCode);
        }
    }

    @Test(priority = 2, description = "Получение информации о заказе по ID", dependsOnMethods = "testCreateOrder",
            retryAnalyzer = RetryAnalyzer.class)
    public void testGetOrderById() {

        Response response = given()
                .when()
                .get("/store/order/" + orderId)
                .then()
                .statusCode(200)
                .extract().response();

        int statusCode = response.getStatusCode();

        if (statusCode == 200) {
            Order retrievedOrder = response.as(Order.class);
            Assert.assertNotNull(retrievedOrder, "Ошибка: Не удалось получить заказ. Ответ пустой.");
            Assert.assertEquals(retrievedOrder.getId(), order.getId(), "Ошибка: ID заказов не совпадают! " +
                    "Ожидался: " + orderId + ", но получен: " + retrievedOrder.getId());
            System.out.println("Успешно получен заказ с ID: " + retrievedOrder.getId());
            response.prettyPrint();

        } else {
            System.out.println("Ошибка: Не удалось получить заказ. Ожидался статус 200, но получен статус: "
                    + statusCode);
        }
    }

    @Test(priority = 3, description = "Удаление заказа", dependsOnMethods = "testCreateOrder")
    public void testDeleteOrder() {

        Response responseToDelete = given()
                .when()
                .delete("/store/order/" + orderId)
                .then()
                .extract().response();

        int statusCode = responseToDelete.getStatusCode();

        if (statusCode == 200) {
            System.out.println("Заказ успешно удален.");
        } else if (statusCode == 404) {
            System.out.println("Заказ не найден.");
        } else {
            System.out.println("Ошибка: Неожиданный статус ответа: " + statusCode);
            return;
        }

        System.out.println("Статус-код ответа: " + statusCode);
        Assert.assertTrue((statusCode == 200 || statusCode == 404),
                "Ожидаемый статус-код: 200 или 404, но получен: " + statusCode);
    }

    @Test(priority = 4, description = "Получение информации о запасах магазина")
    public void testGetInventory() {
        Response response = given()
                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200)
                .extract().response();

        int statusCode = response.statusCode();
        if (statusCode == 200) {
            Assert.assertNotNull(response.jsonPath().getMap(""), "Информация о запасах отсутствует!");

            System.out.println("Ответ API при получении информации о запасах магазина:");
            response.prettyPrint();
        } else {
            System.out.println("Ошибка: Не удалось получить информацию о запасах магазина. " +
                    "Ожидался статус 200, но получен статус: " + statusCode);
        }
    }
}
