package pl.dave.project.webdietserver.exeption;

import lombok.Getter;

@Getter
public class RestApiException extends RuntimeException {

    public RestApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

    public RestApiException(String message) {
        super(message);
    }

    public static void throwExceptionIfConditionIsTrue(boolean condition, ErrorCode errorCode) {
        if (condition) {
            throw new RestApiException(errorCode);
        }
    }
}
