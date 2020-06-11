package pl.dave.project.webdietserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.dave.project.webdietserver.dto.EmailRequest;
import pl.dave.project.webdietserver.entity.Product;
import pl.dave.project.webdietserver.entity.ShoppingList;
import pl.dave.project.webdietserver.service.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;
    private final UserService userService;
    private final RecipeService recipeService;
    private final ShoppingListService shoppingListService;
    private final ProductService productService;

    @PostMapping("/shopping-List/{guid}")
    public void sendShoppingList(@RequestBody EmailRequest request, @PathVariable String guid) {
        StringBuilder text = new StringBuilder();
        ShoppingList shoppingList = shoppingListService.getByGuid(guid, userService.getCurrentLoginUser());

        Map<String, Long> productsAndWeight = shoppingList.getProductsAndWeight();
        List<Product> products = productsAndWeight.keySet().stream()
                .map(productGuid -> productService.getByGuid(productGuid, userService.getCurrentLoginUser()))
                .collect(Collectors.toList());

        text.append("Shopping list for next ")
                .append(shoppingList.getDaysNumber())
                .append(" day(s)\n");

        products.forEach(product -> text.append(product.getName())
                .append(": ")
                .append(productsAndWeight.get(product.getGuid()))
                .append("g\n"));

        text.append("\nRecipes:\n");

        shoppingList.getRecipes().stream()
                .map(recipeService::getByGuid)
                .forEach(recipe -> text.append(recipe.getName()).append("\n"));

        emailService.sendEmail(request, userService.getCurrentLoginUser(), text.toString());
    }
}
