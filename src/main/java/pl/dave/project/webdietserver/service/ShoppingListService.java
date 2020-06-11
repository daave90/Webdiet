package pl.dave.project.webdietserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.stereotype.Service;
import pl.dave.project.webdietserver.entity.ShoppingList;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.entity.enums.UserRole;
import pl.dave.project.webdietserver.exeption.ErrorCode;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.mapper.ShoppingListMapper;
import pl.dave.project.webdietserver.repository.ShoppingListRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListMapper mapper;

    public ShoppingList save(ShoppingList shoppingList, User user) {
        log.info("**************************************************************************************************");
        log.info("Saving shopping list: " + shoppingList);
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(shoppingList == null, ErrorCode.SOURCE_ENTITY_NOT_EXISTS);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);
        shoppingList.setUser(user);
        return shoppingListRepository.save(shoppingList);
    }

    public List<ShoppingList> list(User user) {
        log.info("**************************************************************************************************");
        log.info("Get all shopping lists");
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);
        if (user.getRole() == UserRole.ADMIN) {
            return IteratorUtils.toList(shoppingListRepository.findAll().iterator());
        }
        return IteratorUtils.toList(shoppingListRepository.findAll().iterator()).stream()
                .filter(shoppingList -> shoppingList.getUser().getGuid().equals(user.getGuid()))
                .collect(Collectors.toList());
    }

    public ShoppingList getByGuid(String guid, User user) {
        log.info("**************************************************************************************************");
        log.info("Get shopping list by guid: " + guid);
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);
        RestApiException.throwExceptionIfConditionIsTrue(guid == null, ErrorCode.SOURCE_ENTITY_NOT_EXISTS);
        ShoppingList shoppingList = shoppingListRepository.findById(guid).orElse(null);
        RestApiException.throwExceptionIfConditionIsTrue(shoppingList == null, ErrorCode.SHOPPINGLIST_NOT_EXISTS);
        if (user.getRole() == UserRole.ADMIN ||
                (shoppingList.getUser().getGuid().equals(user.getGuid()))) {
            return shoppingList;
        }
        throw new RestApiException(ErrorCode.GET_WRONG_RESOURCE);
    }

    public ShoppingList upate(String guid, ShoppingList source, User user) {
        log.info("**************************************************************************************************");
        log.info("Update shopping list " + guid + " source: " + source);
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);
        RestApiException.throwExceptionIfConditionIsTrue(source == null, ErrorCode.SOURCE_ENTITY_NOT_EXISTS);
        ShoppingList shoppingListToUpdate = shoppingListRepository.findById(guid).orElse(null);
        if (shoppingListToUpdate != null && (user.getRole() == UserRole.ADMIN ||
                (user.getGuid().equals(shoppingListToUpdate.getUser().getGuid())))) {
            User newUser = shoppingListToUpdate.getUser();
            shoppingListToUpdate = mapper.update(shoppingListToUpdate, source);
            shoppingListToUpdate.setUser(newUser);
            return shoppingListRepository.save(shoppingListToUpdate);
        }
        throw new RestApiException(ErrorCode.UPDATE_WRONG_RESOURCE);
    }

    public void delete(ShoppingList shoppingList, User user) {
        log.info("**************************************************************************************************");
        log.info("Remove shopping list: " + shoppingList);
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(shoppingList == null, ErrorCode.SHOPPINGLIST_NOT_EXISTS);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);
        if (user.getRole() == UserRole.ADMIN ||
                (shoppingList.getUser().getGuid().equals(user.getGuid()))) {
            shoppingListRepository.delete(shoppingList);
        } else {
            throw new RestApiException(ErrorCode.DELETE_WRONG_RESOURCE);
        }
    }
}
