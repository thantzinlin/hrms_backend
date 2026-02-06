package com.hrms.util;

public class ResponseConstants {
     public static final String SUCCESS_CODE = "200";
     public static final String SUCCESS_MESSAGE = "Request processed successfully";

     // Error Responses
     public static final String ERROR_CODE = "500";
     public static final String ERROR_MESSAGE = "An unexpected error occurred.";

     // Unauthorized Access
     public static final String UNAUTHORIZED_ERROR_CODE = "401";
     public static final String UNAUTHORIZED_ERROR_MESSAGE = "Unauthorized access.";

     // Bad Request
     public static final String BAD_REQUEST_ERROR_CODE = "400";
     public static final String BAD_REQUEST_ERROR_MESSAGE = "Bad request, invalid data.";

     // Not Found
     public static final String NOT_FOUND_CODE = "404";
     public static final String NOT_FOUND_MESSAGE = "Data not found.";

     public static final String BINDING_ERROR_MESSAGE = "Binding error";
     public static final String CONSTRAINT_ERROR_MESSAGE = "Database constraint violation";

}
