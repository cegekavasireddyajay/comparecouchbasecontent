package com.cegeka.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class JsonUtils {
    private static final ObjectMapper M = new ObjectMapper();

    public static boolean isJson(String s) {
        if (s == null) return false;
        var t = s.trim();
        if (t.isEmpty()) return false;
        if (!(t.startsWith("{") || t.startsWith("["))) return false;
        try {
            M.readTree(t);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String normalize(String s) {
        if (!isJson(s)) return s == null ? "" : s.trim();
        try {
            JsonNode node = M.readTree(s);
            return M.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (Exception e) {
            return s.trim();
        }
    }

    public static void assertEquivalent(String a, String b) throws Exception {
        var na = normalize(a);
        var nb = normalize(b);
        if (isJson(na) && isJson(nb)) {
            // Strict by default: order and values must match. Change to LENIENT if order in arrays can vary.
            JSONAssert.assertEquals(na, nb, JSONCompareMode.LENIENT);
        } else {
            if (!na.equals(nb)) {
                throw new AssertionError("Bodies differ.\nA:\n" + na + "\n\nB:\n" + nb);
            }
        }
    }
}