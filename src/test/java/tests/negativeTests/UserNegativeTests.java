package tests.negativeTests;

import config.BaseTest;
import io.restassured.response.Response;
import models.User;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class UserNegativeTests extends BaseTest {

    @Test(priority = 1, description = "Получение информации о несуществующем пользователе")
    public void testGetNonExistingUser() {
        String invalidUsername = "invalidUser123";

        Response response = given()
                .when()
                .get("/user/" + invalidUsername)
                .then()
                .statusCode(404)
                .extract().response();

        System.out.println("Ответ API при попытке получения информации о несуществующем пользователе");
        response.prettyPrint();

        if (response.getStatusCode() == 404) {
            String errorMessage = response.jsonPath().getString("message");
            String errorType = response.jsonPath().getString("type");

            Assert.assertEquals(errorMessage, "User not found",
                    "Ожидалось сообщение: 'User not found', но получено: " + errorMessage);
            Assert.assertEquals(errorType, "error",
                    "Ожидалось сообщение: 'error', но получено: " + errorMessage);

            System.out.println("При получении информации о несуществующем пользователе " +
                    "ошибка: " + errorMessage + ", Тип ошибки: " + errorType);
        } else {
            System.out.println("При получении информации о несуществующем пользователе ошибка: " +
                    "Ожидался статус 404, но получен: " + response.getStatusCode());
        }
    }

    @Test(priority = 2, description = "Попытка обновления пользователя с неверным именем")
    public void testUpdateNonExistingUser() {
        User user = new User();
        user.setUsername("nonExistentUser");
        user.setFirstName("NewName");

        Response response = given()
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .put("/user/" + user.getUsername())
                .then()
                .extract().response();

        System.out.println("Ответ API при попытке обновления пользователя с неверным именем");
        response.prettyPrint();

        int statusCode = response.getStatusCode();
        System.out.println("Полученный статус-код: " + statusCode);

        if (statusCode == 404) {
            System.out.println("Пользователь не найден при обновлении.");
        } else if (statusCode == 200) {
            String message = response.jsonPath().getString("message");
            System.out.println("Получен неожиданный ответ при попытке обновления пользователя " +
                    "с неверным именем: Статус 200, сообщение: " + message);
        } else {
            System.out.println("Ошибка: Ожидался статус 404, но получен: " + statusCode);
        }
    }
}
