package com.cegeka.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class HttpClient {

    static {
        RestAssured.useRelaxedHTTPSValidation();
    }

    public static Response get(String base, String path, Map<String, ?> pathParams, Map<String, ?> queryParams) {
        var spec = given().baseUri(base)
                .pathParams(pathParams)
                .queryParams(queryParams)
                .accept("*/*").log().uri();

        if (Config.BEARER_TOKEN != null && !Config.BEARER_TOKEN.isBlank()) {
            spec.header("Authorization", "Bearer " + Config.BEARER_TOKEN);
        }

        return spec.when().get(path).then().extract().response();
    }

    public static Response put(String base, String path, Map<String, ?> pathParams, Map<String, ?> queryParams, Object body) {
        var spec = given().baseUri(base)
                .pathParams(pathParams)
                .queryParams(queryParams)
                .contentType("application/json")
                .accept("*/*");

        if (Config.BEARER_TOKEN != null && !Config.BEARER_TOKEN.isBlank()) {
            spec.header("Authorization", "Bearer " + Config.BEARER_TOKEN);
        }

        return spec.body(body).when().put(path).then().extract().response();
    }
}
