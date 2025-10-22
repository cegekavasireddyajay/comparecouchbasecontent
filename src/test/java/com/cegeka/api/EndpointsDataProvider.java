package com.cegeka.api;

import org.testng.annotations.DataProvider;

import java.util.HashMap;
import java.util.Map;

public class EndpointsDataProvider {

    @DataProvider(name = "doctorResourceGetEndpoints")
    public static Object[][] doctorResourceGetEndpoints() {
        var basePathParams = Map.of(
                "masterId", Config.MASTER_ID,
                "doctorNumber", Config.DOCTOR_NUMBER
        );

        return new Object[][]{
                {"getDoctorDetails", "/public/v2/users/{masterId}/doctors/{doctorNumber}", basePathParams},
                {"getPatientAnalysisHistory_1", "/public/v2/users/{masterId}/doctors/{doctorNumber}/patients/{vioNumber}/results/{analysisNumber}", with(basePathParams, Map.of("vioNumber", Config.VIO_NUMBER, "analysisNumber", Config.ANALYSIS_NUMBER))},
                {"getPatientAnalysisHistoryGraphData_1", "/public/v2/users/{masterId}/doctors/{doctorNumber}/patients/{vioNumber}/results/{analysisNumber}/graph-data", with(basePathParams, Map.of("vioNumber", Config.VIO_NUMBER, "analysisNumber", Config.ANALYSIS_NUMBER))},
                {"getBriefSummariesForPatient", "/public/v2/users/{masterId}/doctors/{doctorNumber}/patients/{vioNumber}/orders", with(basePathParams, Map.of("vioNumber", Config.VIO_NUMBER))},
                {"getOrders", "/public/v2/users/{masterId}/doctors/{doctorNumber}/orders", basePathParams},
                {"getOrder", "/public/v2/users/{masterId}/doctors/{doctorNumber}/orders/{requestNumber}", with(basePathParams, Map.of("requestNumber", Config.REQUEST_NUMBER))},
                //{"getOrderReport", "/public/v2/users/{masterId}/doctors/{doctorNumber}/orders/{requestNumber}/report", with(basePathParams, Map.of("requestNumber", Config.REQUEST_NUMBER))},
                {"getPathologyReport", "/public/v2/users/{masterId}/doctors/{doctorNumber}/orders/{requestNumber}/pdfResult/{fileIdentifier}", with(basePathParams, Map.of("requestNumber", Config.REQUEST_NUMBER, "fileIdentifier", Config.FILE_IDENTIFIER))},
                {"demoFeatureToggle", "/public/v2/users/{masterId}/doctors/{doctorNumber}/demo", basePathParams}
        };
    }

    @DataProvider(name = "userResourceGetEndpoints")
    public static Object[][] userResourceGetEndpoints() {
        var basePathParams = Map.of("masterId", Config.MASTER_ID);

        return new Object[][]{
                {"getSelectedFilters", "/public/v2/users/{masterId}/selected-filters", basePathParams},
                {"getCurrentUser", "/public/v2/users/{masterId}", basePathParams},
                {"getPrescribingDoctors", "/public/v2/users/{masterId}/prescribing-doctors", basePathParams},
                {"getPatientAnalysisHistory", "/public/v2/users/{masterId}/patients/{vioNumber}/results/{analysisNumber}", with(basePathParams, Map.of("vioNumber", Config.VIO_NUMBER, "analysisNumber", Config.ANALYSIS_NUMBER))},
                {"getPatientAnalysisHistoryGraphData", "/public/v2/users/{masterId}/patients/{vioNumber}/results/{analysisNumber}/graph-data", with(basePathParams, Map.of("vioNumber", Config.VIO_NUMBER, "analysisNumber", Config.ANALYSIS_NUMBER))},
                {"getBriefSummaries", "/public/v2/users/{masterId}/patients/{vioNumber}/orders", with(basePathParams, Map.of("vioNumber", Config.VIO_NUMBER))},
                {"getAllOrders", "/public/v2/users/{masterId}/orders", basePathParams},
                {"getLanguage", "/public/v2/users/{masterId}/language", basePathParams},
                {"getDoctors", "/public/v2/users/{masterId}/doctors", basePathParams},
                {"getUserId", "/public/v2/users/current/userid", Map.of()} // no masterId in path
        };
    }
    @DataProvider(name = "patientResourceGetEndpoints")
    public static Object[][] patientResourceGetEndpoints() {
        var pathParamsPatientId = Map.of("patientId", Config.PATIENT_ID);

        return new Object[][]{
                // PatientResource — granting doctors (uses {patientid})
                {"getGrantingDoctors", "/public/v1/patients/{patientid}/granting-doctors", Map.of("patientid", Config.PATIENT_ID)},

                // PatientResource — analysis history and graph (note double patients segment in spec)
                {"getPatientAnalysisHistory", "/public/v1/patients/{patientId}/patients/{vioNumber}/results/{analysisNumber}",
                        with(pathParamsPatientId, Map.of("vioNumber", Config.VIO_NUMBER, "analysisNumber", Config.ANALYSIS_NUMBER))},
                {"getPatientAnalysisHistoryGraphData", "/public/v1/patients/{patientId}/patients/{vioNumber}/results/{analysisNumber}/graph-data",
                        with(pathParamsPatientId, Map.of("vioNumber", Config.VIO_NUMBER, "analysisNumber", Config.ANALYSIS_NUMBER))},

                // PatientResource — orders by vioNumber and all orders for patient
                {"getOrdersByVioNumber", "/public/v1/patients/{patientId}/patients/{vioNumber}/orders",
                        with(pathParamsPatientId, Map.of("vioNumber", Config.VIO_NUMBER))},
                {"getAllOrders", "/public/v1/patients/{patientId}/orders", pathParamsPatientId},

                // PatientResource — order details, report, pdf, invoices list
                {"getOrder", "/public/v1/patients/{patientId}/orders/{requestNumber}",
                        with(pathParamsPatientId, Map.of("requestNumber", Config.REQUEST_NUMBER))},
                {"getOrderReport", "/public/v1/patients/{patientId}/orders/{requestNumber}/report",
                        with(pathParamsPatientId, Map.of("requestNumber", Config.REQUEST_NUMBER))},
                {"getPathologyReport", "/public/v1/patients/{patientId}/orders/{requestNumber}/pdfResult/{fileIdentifier}",
                        with(pathParamsPatientId, Map.of("requestNumber", Config.REQUEST_NUMBER, "fileIdentifier", Config.FILE_IDENTIFIER))},
                {"getOrderInvoices", "/public/v1/patients/{patientId}/orders/{requestNumber}/invoices",
                        with(pathParamsPatientId, Map.of("requestNumber", Config.REQUEST_NUMBER))},

                // PatientResource — invoice PDF
                {"getOrderInvoiceAsPdf", "/public/v1/patients/{patientId}/invoices/{invoiceDocumentId}",
                        with(pathParamsPatientId, Map.of("invoiceDocumentId", Config.INVOICE_DOCUMENT_ID))},

                // PatientResource — per doctor orders
                {"getOrders", "/public/v1/patients/{patientId}/doctors/{doctorNumber}/orders",
                        with(pathParamsPatientId, Map.of("doctorNumber", Config.DOCTOR_NUMBER))},

                // PatientResource — feature toggle demo (uses {patientid})
                {"demoFeatureToggle", "/public/v1/patients/demo/{patientid}", Map.of("patientid", Config.PATIENT_ID)},

                // PatientResource — current patient id (no path params)
                {"getPatientId", "/public/v1/patients/current/patientid", Map.of()}
        };
    }

    private static Map<String, String> toFlatQuery(Map<String, String> doctorContext) {
        // doctorContext is an object in the spec; API expects query parameters like doctorContext.language etc.
        // If your API expects flattened parameters, adjust keys here accordingly.
        var q = new HashMap<String, String>();
        doctorContext.forEach((k, v) -> q.put("" + k, v));
        return q;
    }

    public static Map<String, String> defaultQuery() {
        return toFlatQuery(Config.context());
    }

    private static Map<String, Object> with(Map<String, ?> a, Map<String, ?> b) {
        var m = new HashMap<String, Object>();
        m.putAll(a);
        m.putAll(b);
        return m;
    }


}