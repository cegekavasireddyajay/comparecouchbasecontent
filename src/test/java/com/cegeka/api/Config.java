package com.cegeka.api;

import java.util.Map;

public class Config {
    // Base URLs to compare (can be two versions or two envs)
    public static final String ENVIRONMENT = System.getProperty("environment", "tst");
    public static final String BASE_A = System.getProperty("baseA", System.getenv().getOrDefault("BASE_A",
            "http://ingress"+ENVIRONMENT+":8082/capi"));
    public static final String NEW_BASE_B = System.getProperty("baseB", System.getenv().getOrDefault("BASE_B",
            "http://ingressdev"+""+":8195/capi"));

    // Auth (if needed)
    public static final String BEARER_TOKEN = System.getProperty("token", System.getenv("API_TOKEN"));

    // Default path/query values (override via -DmasterId=... etc.)
    public static final String MASTER_ID = System.getProperty("masterId", "autotest_de");
    public static final String DOCTOR_NUMBER = System.getProperty("doctorNumber", "608488");
    public static final String VIO_NUMBER = System.getProperty("vioNumber", "100004191");
    public static final String ANALYSIS_NUMBER = System.getProperty("analysisNumber", "24976");
    public static final String REQUEST_NUMBER = System.getProperty("requestNumber", "50116671");
    public static final String FILE_IDENTIFIER = System.getProperty("fileIdentifier", "FILE001");
    public static final String PATIENT_ID = System.getProperty("patientId", "1779500");
    public static final String INVOICE_DOCUMENT_ID = System.getProperty("invoiceDocumentId", "INV001");

    // doctorContext defaults
    public static Map<String, String> context() {
        return Map.of(
                "language", System.getProperty("language", "de"),
                "masterId", MASTER_ID,
                "userShortId", System.getProperty("userShortId", "u1"),
                "myId", System.getProperty("myId", "me"),
                "patientId", PATIENT_ID
        );
    }
}