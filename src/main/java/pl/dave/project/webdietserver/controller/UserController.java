package pl.dave.project.webdietserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.dave.project.webdietserver.dto.user.UserListRecord;
import pl.dave.project.webdietserver.dto.user.UserRequest;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.entity.enums.UserRole;
import pl.dave.project.webdietserver.exeption.ErrorCode;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.mapper.UserMapper;
import pl.dave.project.webdietserver.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @GetMapping("/{guid}")
    public UserListRecord getUserByGuid(@PathVariable String guid) {
        return mapper.toListRecord(service.getByGuid(guid));
    }

    @GetMapping
    public List<UserListRecord> listAllUsers() {
        return mapper.toListRecords(service.list());
    }

    @PostMapping
    public UserListRecord createUser(@RequestBody UserRequest request) {
        User newUser = mapper.toEntity(request);
        return mapper.toListRecord(service.save(newUser));
    }

    @PostMapping("/admin")
    public UserListRecord createAdmin(@RequestBody UserRequest request) {
        User newAdmin = mapper.toEntity(request);
        newAdmin.setRole(UserRole.ADMIN);
        return mapper.toListRecord(service.save(newAdmin));
    }

    @PutMapping("/{guid}")
    public UserListRecord updateUser(@PathVariable String guid, @RequestBody UserRequest request) {
        User user = service.update(guid, mapper.toEntity(request));
        return mapper.toListRecord(user);
    }

    @DeleteMapping("/{guid}")
    public void deleteUser(@PathVariable String guid) {
        try {
            service.delete(service.getByGuid(guid));
        } catch (RestApiException ex) {
            throw new RestApiException(ErrorCode.DELETE_WRONG_RESOURCE);
        }
    }
}
