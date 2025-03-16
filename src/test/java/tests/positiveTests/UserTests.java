package tests.positiveTests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import config.BaseTest;
import io.restassured.response.Response;
import models.User;
import org.testng.Assert;
import org.testng.annotations.Test;
import config.RetryAnalyzer;

import static io.restassured.RestAssured.given;


public class UserTests extends BaseTest {

    private User user;
    private String message;

    @Test(priority = 1, description = "Создание нового пользователя", retryAnalyzer = RetryAnalyzer.class)
    public void testCreateUser() {

        JsonObject userJsonObject = testData.getAsJsonObject("user");
        user = new Gson().fromJson(userJsonObject, User.class);

        Response response = given()
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .post("/user")
                .then()
                .statusCode(200)
                .extract().response();

        System.out.println("Ответ API при создании нового пользователя:");
        response.prettyPrint();

        if (response.getStatusCode() == 200) {
            String message = response.jsonPath().getString("message");
            this.message = message;

            Assert.assertNotNull(message, "Поле 'message' отсутствует в ответе!");
            Assert.assertEquals(response.statusCode(), 200, "Не удалось создать пользователя.");

            System.out.println("Созданный ID пользователя: " + message);
            System.out.println("Статус код ответа: " + response.getStatusCode());
        } else {
            System.out.println("Ошибка: Не удалось создать пользователя. Статус-код: " + response.getStatusCode());
        }
    }

    @Test(priority = 2, description = "Получение информации о пользователе", dependsOnMethods = "testCreateUser",
            retryAnalyzer = RetryAnalyzer.class)
    public void testGetUserByUsername() {

        System.out.println("Получаем информацию о пользователе с именем: " + user.getUsername());

        Response response = given()
                .when()
                .get("/user/" + user.getUsername())
                .then()
                .statusCode(200)
                .extract().response();

        if (response.getStatusCode() == 200) {
            User retrievedUser = response.as(User.class);
            Assert.assertNotNull(retrievedUser, "Ответ должен содержать объект пользователя!");
            Assert.assertEquals(retrievedUser.getUsername(), user.getUsername(), "Ошибка: Имя пользователя не совпадает! Ожидалось: "
                    + user.getUsername() + ", но получено: " + retrievedUser.getUsername());

            System.out.println("Пользователь успешно найден!");
            response.prettyPrint();

        } else {
            System.out.println("Ошибка: Пользователь не найден или введены некорректные данные.");
            System.out.println("Статус-код: " + response.getStatusCode());
        }
    }

    @Test(priority = 3, description = "Обновление информации о пользователе", dependsOnMethods = "testCreateUser")
    public void testUpdateUser() {

        user.setFirstName("UpdatedFirstName");
        user.setLastName("UpdatedLastName");
        user.setEmail("updatedEmail@example.com");

        System.out.println("Обновляем информацию о пользователе с именем: " + user.getUsername());

        Response response = given()
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .put("/user/" + user.getUsername())
                .then()
                .statusCode(200)
                .extract().response();

        System.out.println("Ответ от API при обновлении: ");
        response.prettyPrint();

        if (response.getStatusCode() == 200) {

            String messageSuccess = response.path("message");
            Assert.assertNotNull(messageSuccess, "Сообщение об успешном обновлении отсутствует.");
            Assert.assertEquals(messageSuccess, this.message);
            System.out.println("Данные пользователя успешно обновлены!");
        } else {
            System.out.println("Ошибка: Не удалось обновить пользователя. Статус-код: " + response.getStatusCode());
        }
    }


    @Test(priority = 4, description = "Удаление пользователя", dependsOnMethods = "testCreateUser")
    public void testDeleteUser() {

        System.out.println("Удаляем информацию о пользователе с именем: " + user.getUsername());

        Response responseToDelete = given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/user/" + user.getUsername())
                .then()
                .extract().response();

        int statusCode = responseToDelete.getStatusCode();

        if (statusCode == 200) {
            System.out.println("Пользователь успешно удален.");
        } else if (statusCode == 404) {
            System.out.println("Пользователь не найден.");
        } else {
            System.out.println("Неожиданный статус ответа: " + statusCode);
        }
        Assert.assertTrue(statusCode == 200 || statusCode == 404,
                "Ожидаемый статус-код: 200 или 404, но получен: " + statusCode);
    }
}
