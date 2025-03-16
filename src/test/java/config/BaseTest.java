package config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

import java.io.FileReader;


public class BaseTest {
    public static JsonObject testData;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        RestAssured.filters(new AllureRestAssured());

        Gson gson = new Gson();
        try {
            testData = gson.fromJson(new FileReader("src/test/resources/testdata.json"), JsonObject.class);
            if (testData == null || !testData.has("pet")) {
                throw new RuntimeException("Ошибка загрузки testdata.json! Проверь файл.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить testdata.json: " + e.getMessage());
        }

        System.out.println("Test data загружена: " + testData);
    }
}

