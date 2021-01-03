package pl.dave.project.webdietserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.dave.project.webdietserver.dto.NameAndGuid;
import pl.dave.project.webdietserver.dto.user.UserListRecord;
import pl.dave.project.webdietserver.dto.user.UserRequest;
import pl.dave.project.webdietserver.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "creationTimestamp", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "role", ignore = true)
    public abstract User toEntity(UserRequest request);

    public abstract UserListRecord toListRecord(User user);

    public abstract List<UserListRecord> toListRecords(List<User> user);

    @Mapping(target = "guid", ignore = true)
    public abstract User update(@MappingTarget User target, User source);

    @Mapping(target = "name", source = "username")
    public abstract NameAndGuid toNameAndGuid(User user);
}
