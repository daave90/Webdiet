package pl.dave.project.webdietserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.dave.project.webdietserver.dto.shoppinglist.ShoppingListListRecord;
import pl.dave.project.webdietserver.dto.shoppinglist.ShoppingListRequest;
import pl.dave.project.webdietserver.entity.ShoppingList;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.exeption.ErrorCode;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.mapper.ShoppingListMapper;
import pl.dave.project.webdietserver.service.ShoppingListService;
import pl.dave.project.webdietserver.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/shopping-lists")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListService service;
    private final ShoppingListMapper mapper;
    private final UserService userService;

    @GetMapping("/{guid}")
    public ShoppingListListRecord getShoppingListByGuid(@PathVariable String guid) {
        return mapper.toListRecord(service.getByGuid(guid, userService.getCurrentLoginUser()));
    }

    @GetMapping
    public List<ShoppingListListRecord> listAllShoppingLists() {
        return mapper.toListRecords(service.list(userService.getCurrentLoginUser()));
    }

    @PostMapping
    public ShoppingListListRecord createShoppingList(@RequestBody ShoppingListRequest request) {
        ShoppingList newShoppingList = mapper.toEntity(request);
        return mapper.toListRecord(service.save(newShoppingList, userService.getCurrentLoginUser()));
    }

    @PutMapping("/{guid}")
    public ShoppingListListRecord updateShoppingList(@PathVariable String guid,
                                                     @RequestBody ShoppingListRequest request) {
        ShoppingList shoppingList = service.upate(guid, mapper.toEntity(request), userService.getCurrentLoginUser());
        return mapper.toListRecord(shoppingList);
    }

    @DeleteMapping("/{guid}")
    public void deleteShoppingList(@PathVariable String guid) {
        try {
            User loginUser = userService.getCurrentLoginUser();
            service.delete(service.getByGuid(guid, loginUser), loginUser);
        } catch (RestApiException ex) {
            throw new RestApiException(ErrorCode.DELETE_WRONG_RESOURCE);
        }
    }
}
