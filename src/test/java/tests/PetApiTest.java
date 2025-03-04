package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import models.Pet;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PetApiTest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void testCreatePet() {
        Pet pet = new Pet("Fluffy", "available");

        given()
            .contentType(ContentType.JSON)
            .body(pet)
        .when()
            .post("/api/pets")
        .then()
            .statusCode(201)
            .body("name", equalTo("Fluffy"))
            .body("status", equalTo("available"))
            .body("id", notNullValue());
    }

    @Test
    public void testGetPet() {
        given()
            .pathParam("id", 1)
        .when()
            .get("/api/pets/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("name", notNullValue())
            .body("status", notNullValue());
    }

    @Test
    public void testUpdatePet() {
        Pet updatedPet = new Pet("Updated Fluffy", "sold");

        given()
            .contentType(ContentType.JSON)
            .body(updatedPet)
            .pathParam("id", 1)
        .when()
            .put("/api/pets/{id}")
        .then()
            .statusCode(200)
            .body("name", equalTo("Updated Fluffy"))
            .body("status", equalTo("sold"));
    }

    @Test
    public void testDeletePet() {
        given()
            .pathParam("id", 1)
        .when()
            .delete("/api/pets/{id}")
        .then()
            .statusCode(204);
    }
}
