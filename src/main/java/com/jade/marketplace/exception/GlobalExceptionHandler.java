package com.jade.marketplace.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.util.stream.Collectors;

/**
 * Global exception handler for all GraphQL endpoints
 */
@Component
public class GlobalExceptionHandler extends DataFetcherExceptionResolverAdapter {

    /**
     * Get readable message from different exception types
     */
    private String getReadableMessage(Throwable exception) {
        
        /**
         * get all error messages for custom exceptions
         */
        // Custom application exceptions
        if (exception instanceof ResourceNotFoundException
            || exception instanceof UnauthorizedException
            || exception instanceof ValidationException
            || exception instanceof ProductOutOfStockException
            || exception instanceof DuplicateEmailException
            || exception instanceof ForbiddenException) {
        return exception.getMessage();
    }

        // exception where fields have certain constraints but do not meet them
        // example: password does not have at least 8 characters
        if (exception instanceof ConstraintViolationException validationException) {
            return validationException.getConstraintViolations()
                // turns a collection of errors into pipeline to process each item
                .stream()
                // turns each error into its message
                // [error message, error message] -> ["Email is required", "Password too short"]
                .map(error -> error.getPropertyPath() + ": " + error.getMessage())
                // put all transformed results into a List
                .collect(Collectors.joining(", "));
        }

        // exception where Spring fails to bind/validate request data due to field-level errors
        // example: blank fields
        if (exception instanceof BindException bindException) {
            return bindException.getBindingResult()
                // get all field binding errors
                .getFieldErrors()
                // turns a collection of errors into pipeline to process each item
                .stream()
                // turns each error into its message
                // [fieldError, fieldError] -> ["Email is required", "Password too short"]
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                // put all transformed results into a List
                .collect(Collectors.joining(", "));
        }

        // return this for all other exceptions
        return "An unexpected error occured";
    }

    /**
     * Converts exception into GraphQL-friendly error responses
     * 
     * Note:
     * Throwable = parent of all things Java can throw 
     * DataFetchingEnvironment = contains extra info about the GraphQL request currently being processed. It can include: field name, arguments, current user context, GraphQL path, source object
     */
    @Override
    protected GraphQLError resolveToSingleError(@NonNull Throwable exception, @NonNull DataFetchingEnvironment environment) {
        // get message from exception
        String message = getReadableMessage(exception);

        // return GraphQL-friendly error response
        // GraphqlErrorBuilder = a built-in helper to build error response
        return GraphqlErrorBuilder.newError(environment)
            // set the message of the response
            .message(message)
            // build the response
            .build();
    }

}