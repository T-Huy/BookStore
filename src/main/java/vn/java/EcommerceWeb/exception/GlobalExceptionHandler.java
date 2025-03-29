package vn.java.EcommerceWeb.exception;

import io.jsonwebtoken.security.WeakKeyException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            {MethodArgumentNotValidException.class, ConstraintViolationException.class, HandlerMethodValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(Exception e, WebRequest request) {
        System.out.println("==============> handleValidationException");
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        String message = e.getMessage();
        if (e instanceof MethodArgumentNotValidException ex) {
            System.out.println("==============> MethodArgumentNotValidException");
            errorResponse.setError("Payload Invalid");
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            message = errors.toString();
        } else if (e instanceof ConstraintViolationException ex) {
            System.out.println("==============> ConstraintViolationException");
            errorResponse.setError("Parameter Invalid");
            Map<String, String> errors = new HashMap<>();
            ex.getConstraintViolations().forEach(violation -> {
                String fieldName = violation.getPropertyPath().toString();
                if (fieldName.contains(".")) {
                    fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1);
                }
                String errorMessage = violation.getMessage();
                errors.put(fieldName, errorMessage);
            });
            message = errors.toString();
        } else if (e instanceof HandlerMethodValidationException ex) {
            System.out.println("==============> HandlerMethodValidationException");
            errorResponse.setError("Validation Invalid");
            Map<String, String> errors = new HashMap<>();
            ex.getAllErrors().forEach(error -> {
                if (error instanceof FieldError fieldError) {
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage());
                } else {
                    errors.put("error", error.getDefaultMessage());
                }
            });
            message = errors.toString();
            errorResponse.setMessage(message);
            return errorResponse;
        }
        errorResponse.setMessage(message);
        return errorResponse;
    }


    @ExceptionHandler({MethodArgumentTypeMismatchException.class, NumberFormatException.class, WeakKeyException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerErrorException(Exception e, WebRequest request) {
        System.out.println("==============> handleInternalServerErrorException");
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        String message = e.getMessage();
        if (e instanceof MethodArgumentTypeMismatchException ex) {
            System.out.println("==============> MethodArgumentTypeMismatchException");
            Map<String, String> errors = new HashMap<>();
            String fieldName = ex.getName();
            String requiredType = (ex.getRequiredType() != null) ? ex.getRequiredType().getSimpleName() : "Unknown";
            String errorMessage = String.format("The value '%s' is not valid for parameter '%s'. Expected type: %s.", ex.getValue(), ex.getName(), requiredType);
            errors.put(fieldName, errorMessage);
            message = errors.toString();
//            int end = message.indexOf("java.lang.String");
//            message = message.substring(0, end - 2);
        }
        errorResponse.setMessage(message);
        return errorResponse;
    }
}
