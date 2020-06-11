package pl.dave.project.webdietserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.dave.project.webdietserver.dto.recipe.RecipeListRecord;
import pl.dave.project.webdietserver.dto.recipe.RecipeRequest;
import pl.dave.project.webdietserver.entity.Recipe;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.exeption.ErrorCode;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.mapper.RecipeMapper;
import pl.dave.project.webdietserver.service.RecipeService;
import pl.dave.project.webdietserver.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService service;
    private final RecipeMapper mapper;
    private final UserService userService;

    @GetMapping("/{guid}")
    public RecipeListRecord getRecipeByGuid(@PathVariable String guid) {
        return mapper.toListRecord(service.getByGuid(guid, userService.getCurrentLoginUser()));
    }

    @GetMapping
    public List<RecipeListRecord> listAllRecipes() {
        return mapper.toListRecords(service.list(userService.getCurrentLoginUser()));
    }

    @PostMapping
    public RecipeListRecord createRecipe(@RequestBody RecipeRequest request) {
        Recipe newRecipe = mapper.toEntity(request);
        return mapper.toListRecord(service.save(newRecipe, userService.getCurrentLoginUser()));
    }

    @PutMapping("/{guid}")
    public RecipeListRecord updateRecipe(@PathVariable String guid, @RequestBody RecipeRequest request) {
        Recipe recipe = service.update(guid, mapper.toEntity(request), userService.getCurrentLoginUser());
        return mapper.toListRecord(recipe);
    }

    @DeleteMapping("/{guid}")
    public void deleteRecipe(@PathVariable String guid) {
        try {
            User loginUser = userService.getCurrentLoginUser();
            service.delete(service.getByGuid(guid, loginUser), loginUser);
        } catch (RestApiException ex) {
            throw new RestApiException(ErrorCode.DELETE_WRONG_RESOURCE);
        }
    }
}
