package pl.dave.project.webdietserver.exeption;

import lombok.Getter;

@Getter
public class RestApiException extends RuntimeException{
    private String message;

    public RestApiException(ErrorCode errorCode){
        this.message = errorCode.getMessage();
    }

    public static void throwExceptionIfConditionIsTrue(boolean condition, ErrorCode errorCode) {
        if (condition) {
            throw new RestApiException(errorCode);
        }
    }
}
