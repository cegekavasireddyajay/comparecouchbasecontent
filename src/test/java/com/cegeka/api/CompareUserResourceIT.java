package com.cegeka.api;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

import static com.cegeka.api.EndpointsDataProvider.defaultQuery;

public class CompareUserResourceIT {

    @Test(dataProvider = "userResourceGetEndpoints", dataProviderClass = EndpointsDataProvider.class)
    public void compare_user_resource_get_endpoints(String name, String path, Map<String, ?> pathParams) throws Exception {
        System.out.println("------- User Resource ----- "  + name);

        var query = defaultQuery();

        Response a = HttpClient.get(Config.BASE_A, path, pathParams, query);
        Response b = HttpClient.get(Config.NEW_BASE_B, path, pathParams, query);

        Assert.assertEquals(a.statusCode(), b.statusCode(), name + ": status code differs");
        Assert.assertEquals(a.statusCode(), 200, name + ": expected 200 OK");

        String bodyA = a.asString();
        String bodyB = b.asString();
        System.out.println("bodyB = " + bodyB);

        JsonUtils.assertEquivalent(bodyA, bodyB);
    }
}
