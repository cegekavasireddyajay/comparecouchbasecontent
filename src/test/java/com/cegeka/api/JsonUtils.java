package com.cegeka.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
            JsonNode sorted = deepSort(node);
            return M.writerWithDefaultPrettyPrinter().writeValueAsString(sorted);
        } catch (Exception e) {
            return s == null ? "" : s.trim();
        }
    }

    private static JsonNode deepSort(JsonNode node) {
        if (node == null) return null;
        if (node.isArray()) {
            ArrayNode array = (ArrayNode) node;
            // First, sort children recursively
            List<JsonNode> items = new ArrayList<>();
            array.forEach(child -> items.add(deepSort(child)));

            // If elements are objects and any contain a requestNumber field, sort by it (desc)
            boolean hasRequestNumber = items.stream().allMatch(JsonNode::isObject)
                    && items.stream().anyMatch(n -> n.hasNonNull("requestNumber"));
            if (hasRequestNumber) {
                items.sort(requestNumberComparator().reversed());
            }

            ArrayNode out = M.createArrayNode();
            items.forEach(out::add);
            return out;
        } else if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            // Recurse into all fields
            obj.fieldNames().forEachRemaining(fn -> obj.set(fn, deepSort(obj.get(fn))));
            return obj;
        } else {
            return node; // primitives
        }
    }

    private static Comparator<JsonNode> requestNumberComparator() {
        return (a, b) -> {
            var ra = a.get("requestNumber");
            var rb = b.get("requestNumber");
            // Handle nulls: nulls last
            if (ra == null || ra.isNull()) return (rb == null || rb.isNull()) ? 0 : 1;
            if (rb == null || rb.isNull()) return -1;
            String sa = ra.asText("");
            String sb = rb.asText("");
            // Prefer numeric comparison when possible
            try {
                long la = Long.parseLong(sa.replaceAll("[^0-9]", ""));
                long lb = Long.parseLong(sb.replaceAll("[^0-9]", ""));
                return Long.compare(la, lb);
            } catch (Exception ignore) {
                return sa.compareTo(sb);
            }
        };
    }

    public static void assertEquivalent(String a, String b) throws Exception {
        var na = normalize(a);
        var nb = normalize(b);
        if (isJson(na) && isJson(nb)) {
            // Use LENIENT to ignore insignificant ordering differences after normalization
            JSONAssert.assertEquals(na, nb, JSONCompareMode.LENIENT);
        } else {
            if (!na.equals(nb)) {
                throw new AssertionError("Bodies differ.\nA:\n" + na + "\n\nB:\n" + nb);
            }
        }
    }
}