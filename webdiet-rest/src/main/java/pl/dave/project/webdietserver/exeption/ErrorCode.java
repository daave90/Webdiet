package pl.dave.project.webdietserver.exeption;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USERNAME_EXISTS("This username exists in database."),
    USER_NOT_EXISTS("User not exists in database."),
    GET_WRONG_RESOURCE("You cannot read resource which not belongs to your account."),
    DELETE_WRONG_RESOURCE("You cannot delete resource which not belongs to your account."),
    UPDATE_WRONG_RESOURCE("You cannot update resource which not belongs to your account."),
    PRODUCTS_WEIGHT_LESS_THAN_ZERO("Product weight is less than 0."),
    TOTAL_KCAL_SUM_ERROR("Total kcal sum error."),
    WEIGHT_ERROR("Weight cannot be less than 0."),
    USER_CANNOT_BE_NULL("User cannot be null."),
    PRODUCT_NOT_EXISTS("Product not exists in database."),
    SOURCE_ENTITY_NOT_EXISTS("Source entity cannot be null"),
    RECIPE_NOT_EXISTS("Recipe not exists in database."),
    SHOPPINGLIST_NOT_EXISTS("Shopping list not exists in database."),
    EMPTY_EMAIL_RECIPIENT("Email recipient cannot be null"),
    EMPTY_EMAIL_TEXT("Email text is empty"),
    ADMIN_ACCOUNT_EXIST_IN_DB("Admin account exist in database"),
    PASSWORD_LENGTH("Password minimal length is 8 digits"),
    PASSWORD_UPPERCASE("Password must have at least one uppercase digit"),
    PASSWORD_LOWERCASE("Password must have at least one lowercase digit"),
    PASSWORD_NUMBER("Password must have at least one number");

    private String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
