package tests.positiveTests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import config.BaseTest;
import models.Pet;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import config.RetryAnalyzer;

import static io.restassured.RestAssured.given;

public class PetTests extends BaseTest {
    private Pet pet;

    @Test(priority = 1, description = "Создание нового питомца", retryAnalyzer = RetryAnalyzer.class)
    public void testCreatePet() {
        JsonObject petJsonObject = testData.getAsJsonObject("pet");
        pet = new Gson().fromJson(petJsonObject, Pet.class);

        Response response = given()
                .header("Content-Type", "application/json")
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .extract().response();

        int statusCode = response.getStatusCode();

        if (statusCode == 200) {
            Integer responseId = response.path("id");
            String responseName = response.path("name");

            Assert.assertEquals(responseId, pet.getId(), "ID питомца не совпадает!");
            Assert.assertEquals(responseName, pet.getName(), "Имя питомца не совпадает!");

            System.out.println("Создан питомец с именем : " + pet.getName());
            System.out.println("ID созданного питомца: " + pet.getId());
            System.out.println("Статус код ответа: " + response.getStatusCode() + ". Питомец успешно создан! ");

        } else {
            System.out.println("Ошибка: Не удалось создать питомца. Статус-код: " + response.getStatusCode());
            Assert.assertEquals(response.getStatusCode(), 200, "Ожидался статус 200, но получен: "
                    + response.getStatusCode());
        }
        System.out.println("Ответ API при создании нового питомца:");
        response.prettyPrint();
    }

    @Test(priority = 2, description = "Получение информации о питомце по ID", dependsOnMethods = "testCreatePet",
            retryAnalyzer = RetryAnalyzer.class)
    public void testGetPetById() throws InterruptedException {

        System.out.println("Получаем информацию о питомце по ID : " + pet.getId());

        Thread.sleep(3000);
        Response response = given()
                .when()
                .get("/pet/" + pet.getId())
                .then()
                .statusCode(200)
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "Ожидался статус 200, но получен: "
                + response.getStatusCode());

        System.out.println("Информация о питомце успешно получена.");
        response.prettyPrint();
    }


    @Test(priority = 3, description = "Обновление информации о питомце", dependsOnMethods = "testCreatePet")
    public void testUpdatePet() {

        pet.setName("UpdatedPetName");
        pet.setStatus("sold");

        Response response = given()
                .header("Content-Type", "application/json")
                .body(pet)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .extract().response();

        System.out.println("Ответ API при обновлении питомца:");
        response.prettyPrint();
        System.out.println("Статус-код: " + response.getStatusCode() + ". Питомец успешно обновлен! ");
    }

    @Test(priority = 4, description = "Удаление питомца", dependsOnMethods = "testCreatePet")
    public void testDeletePet() {

        Response responseToDelete = given()
                .when()
                .delete("/pet/" + pet.getId())
                .then()
                .extract().response();

        int statusCode = responseToDelete.getStatusCode();

        System.out.println("Ответ API при удалении питомца:");
        responseToDelete.prettyPrint();

        System.out.println("Статус-код ответа: " + responseToDelete.getStatusCode());

        if (statusCode == 200) {
            System.out.println("Питомец успешно удален.");
        } else if (statusCode == 404) {
            System.out.println("Питомец не найден.");
        } else {
            System.out.println("Неожиданный статус ответа: " + statusCode);
        }
        Assert.assertTrue(statusCode == 200 || statusCode == 404,
                "Ожидаемый статус-код: 200 (удалено) или 404 (не найден), но получен: " + statusCode);
    }
}




