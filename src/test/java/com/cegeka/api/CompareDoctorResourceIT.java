package com.cegeka.api;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.cegeka.api.EndpointsDataProvider.defaultQuery;

public class CompareDoctorResourceIT {

    @Test(dataProvider = "doctorResourceGetEndpoints", dataProviderClass = EndpointsDataProvider.class)
    public void compare_doctor_resource_get_endpoints(String name, String path, Map<String, ?> pathParams) throws Exception {
        System.out.println("------- Doctor Resource ----- " + name);
        var query = defaultQuery();

        Response a = HttpClient.get(Config.BASE_A, path, pathParams, query);
        Response b = HttpClient.get(Config.NEW_BASE_B, path, pathParams, query);

        // Status code must match and be 200 to compare bodies meaningfully
        Assert.assertEquals(a.statusCode(), b.statusCode(), name + ": status code differs");
        //Assert.assertEquals(a.statusCode(), 200, name + ": expected 200 OK");

        String bodyA = a.asString();
        String bodyB = b.asString();
        System.out.println("Respone Content = " + bodyA);
        //System.out.println("Response Content = " + bodyB);

        JsonUtils.assertEquivalent(bodyA, bodyB);
    }

    @Test
    public void compare_each_order_details_between_envs_using_doctor_getOrders() throws Exception {
        System.out.println("------- Doctor Resource ----- getOrders -> iterate requestNumbers -> getOrder compare");
        var query = defaultQuery();

        // 1) Call getOrders on BASE_A
        String getOrdersPath = "/public/v2/users/{masterId}/doctors/{doctorNumber}/orders";
        Map<String, Object> baseParams = new HashMap<>();
        baseParams.put("masterId", Config.MASTER_ID);
        baseParams.put("doctorNumber", Config.DOCTOR_NUMBER);

        Response ordersA = HttpClient.get(Config.BASE_A, getOrdersPath, baseParams, query);
        Assert.assertEquals(ordersA.statusCode(), 200, "getOrders (BASE_A) expected 200 OK");
        String ordersBodyA = ordersA.asString();

        // 2) Collect requestNumbers from the response
        Set<String> requestNumbers = extractRequestNumbers(ordersBodyA);
        System.out.println("Collected requestNumbers count = " + requestNumbers.size());
        if (requestNumbers.isEmpty()) {
            Assert.fail("No requestNumbers found in getOrders response from BASE_A");
        }

        // 3) For each requestNumber, call getOrder on both bases and compare
        String getOrderPath = "/public/v2/users/{masterId}/doctors/{doctorNumber}/orders/{requestNumber}";
        int compared = 0;
        for (String rn : requestNumbers) {
            Map<String, Object> pp = new HashMap<>(baseParams);
            pp.put("requestNumber", rn);

            Response a = HttpClient.get(Config.BASE_A, getOrderPath, pp, query);
            Response b = HttpClient.get(Config.NEW_BASE_B, getOrderPath, pp, query);

            String name = "getOrder:" + rn;
            Assert.assertEquals(a.statusCode(), b.statusCode(), name + ": status code differs");

            if (a.statusCode() == 200) {
                String ba = a.asString();
                String bb = b.asString();
                try {
                    JsonUtils.assertEquivalent(ba, bb);
                } catch (AssertionError err) {
                    System.out.println("Difference for requestNumber=" + rn);
                    System.out.println("A=" + ba);
                    System.out.println("B=" + bb);
                    throw err;
                }
            } else {
                System.out.println(name + " skipped body compare due to non-200 status: " + a.statusCode());
            }
            compared++;
        }
        System.out.println("Compared getOrder for requestNumbers: " + compared);
    }

    private static Set<String> extractRequestNumbers(String json) throws Exception {
        Set<String> out = new LinkedHashSet<>();
        if (!JsonUtils.isJson(json)) return out;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        collectRequestNumbers(root, out);
        return out;
    }

    private static void collectRequestNumbers(JsonNode node, Set<String> out) {
        if (node == null) return;
        if (node.isObject()) {
            if (node.has("requestNumber") && !node.get("requestNumber").isNull()) {
                out.add(node.get("requestNumber").asText());
            }
            node.fields().forEachRemaining(e -> collectRequestNumbers(e.getValue(), out));
        } else if (node.isArray()) {
            node.forEach(child -> collectRequestNumbers(child, out));
        }
    }
}
