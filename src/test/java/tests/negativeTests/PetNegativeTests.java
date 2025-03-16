package tests.negativeTests;

import config.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PetNegativeTests extends BaseTest {

    @Test(priority = 1, description = "Попытка получения информации о питомце с некорректным ID")
    public void testGetPetByInvalidId() {
        Response invalidPetResponse = given()
                .when()
                .get("/pet/invalidPet")
                .then()
                .statusCode(404)
                .extract().response();

        System.out.println("Ответ API при попытке получения информации о питомце с некорректным ID:");
        invalidPetResponse.prettyPrint();

        int invalidStatusCode = invalidPetResponse.getStatusCode();

        Assert.assertEquals(invalidStatusCode, 404, "Ожидался статус 404, но получен: "
                + invalidStatusCode);

        String errorMessage = invalidPetResponse.jsonPath().getString("message");
        Assert.assertTrue(errorMessage.contains("NumberFormatException"), "Некорректное сообщение об ошибке!");

        System.out.println("При попытке попытке получения информации о питомце с некорректным ID статус-код: "
                + invalidStatusCode);
    }

    @Test(priority = 2, description = "Попытка обновления питомца без данных")
    public void testUpdatePetWithoutBody() {
        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .put("/pet")
                .then()
                .statusCode(405)
                .extract().response();

        System.out.println("Ответ API при попытке обновления питомца без данных:");
        response.prettyPrint();

        Assert.assertEquals(response.getStatusCode(), 405, "Ожидался статус 405, но получен: "
                + response.getStatusCode());

        String errorMessage = response.jsonPath().getString("message");
        Assert.assertEquals(errorMessage, "no data", "Некорректное сообщение об ошибке!");

        System.out.println("При попытке обновить питомца без данных статус-код: " + response.getStatusCode() + ". Ошибка: "
                + errorMessage);
    }

    @Test(priority = 3, description = "Попытка удаления несуществующего питомца (с несуществующим ID)")
    public void testDeletePetByInvalidId() {
        Response response = given()
                .when()
                .delete("/pet/invalidPet")
                .then()
                .statusCode(404)
                .extract().response();

        System.out.println("Ответ API при попытке удаления несуществующего питомца:");
        response.prettyPrint();

        Assert.assertEquals(response.getStatusCode(), 404, "Ожидался статус 404, но получен: "
                + response.getStatusCode());
        String errorMessage = response.jsonPath().getString("message");
        Assert.assertTrue(errorMessage.contains("NumberFormatException"), "Некорректное сообщение об ошибке!");
        System.out.println("При попытке удалить  несуществующего питомца статус-код: " + response.getStatusCode());
    }

    @Test(priority = 4, description = "Попытка обновить питомца с некорректными данными")
    public void testUpdatePetWithInvalidData() {
        String invalidPetJson = "{\"id\": \"abc\", \"name\": 123, \"status\": null}";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(invalidPetJson)
                .when()
                .put("/pet")
                .then()
                .statusCode(500)
                .extract().response();

        System.out.println("Ответ API при попытке обновления питомца с некорректными данными:");
        response.prettyPrint();

        Assert.assertEquals(response.getStatusCode(), 500, "Ожидался статус 500, но получен: "
                + response.getStatusCode());
        String errorMessage = response.jsonPath().getString("message");
        Assert.assertEquals(errorMessage, "something bad happened", "Некорректное сообщение об ошибке!");

        System.out.println("При попытке обновить питомца с некорректными данными статус-код: " + response.getStatusCode() +
                ". Ошибка: " + errorMessage);
    }
}
