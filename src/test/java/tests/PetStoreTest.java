
package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import utils.TestBase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PetStoreTest extends TestBase {

    @Test
    public void testGetPetById() {
        given()
                .pathParam("petId", 1)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    public void testCreatePet() {
        String petJson = "{ \"id\": 12345, \"name\": \"Bobby\", \"status\": \"available\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(petJson)
                .when()
                .post("/pet");

        response.then().statusCode(200);
        response.then().body("name", equalTo("Bobby"));
    }

    @Test
    public void testDeletePet() {
        int petId = 12345;

        given()
                .pathParam("petId", petId)
                .when()
                .delete("/pet/{petId}")
                .then()
                .statusCode(200);
    }
}
