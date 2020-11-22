package pl.dave.project.webdietserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.dave.project.webdietserver.config.SecurityConfig;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.entity.enums.UserRole;
import pl.dave.project.webdietserver.exeption.ErrorCode;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.mapper.UserMapper;
import pl.dave.project.webdietserver.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    public User save(User user) {
        log.info("**************************************************************************************************");
        log.info("Register new user: " + user);
        checkIfEmailExists(user);
        if (user.getRole() == UserRole.ADMIN) {
            checkIfAdminExistInDatabase();
        }
        validatePassword(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (StringUtils.isNotEmpty(user.getMailPassword())) {
            user.setMailPassword(passwordEncoder.encode(user.getMailPassword()));
        }
        return userRepository.save(user);
    }

    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new RestApiException(ErrorCode.PASSWORD_LENGTH);
        }
        checkPasswordCondition(password, ErrorCode.PASSWORD_UPPERCASE, code -> !Character.isUpperCase(code));
        checkPasswordCondition(password, ErrorCode.PASSWORD_LOWERCASE, code -> !Character.isLowerCase(code));
        checkPasswordCondition(password, ErrorCode.PASSWORD_NUMBER, code -> !Character.isDigit(code));
    }

    private void checkPasswordCondition(String password, ErrorCode error, IntPredicate predicate) {
        boolean condition = password.chars().map(ascii -> (char) ascii)
                .allMatch(predicate);
        if (condition) {
            throw new RestApiException(error);
        }
    }

    public List<User> list() {
        return IteratorUtils.toList(userRepository.findAll().iterator());
    }

    public User getByGuid(String guid) {
        log.info("**************************************************************************************************");
        log.info("Get user by guid: " + guid);
        if (getCurrentLoginUser().getRole() == UserRole.ADMIN ||
                (getCurrentLoginUser().getRole() == UserRole.USER && getCurrentLoginUser().getGuid().equals(guid))) {
            return userRepository.findById(guid).orElse(new User());
        }
        throw new RestApiException(ErrorCode.GET_WRONG_RESOURCE);
    }

    public void delete(User user) {
        log.info("**************************************************************************************************");
        log.info("Remove user: " + user);
        log.info("Login user: " + user);
        if (getCurrentLoginUser().getRole() == UserRole.ADMIN) {
            userRepository.delete(user);
        } else if (getCurrentLoginUser().getRole() == UserRole.USER && (getCurrentLoginUser().getGuid().equals(user.getGuid()))) {
            user.setEnabled(false);
            update(user.getGuid(), user);
        }
    }

    public User update(String guid, User source) {
        log.info("**************************************************************************************************");
        log.info("Update user " + guid + " source: " + source);
        User userToUpdate = userRepository.findById(guid).orElse(null);
        if (userToUpdate != null && (getCurrentLoginUser().getRole() == UserRole.ADMIN ||
                (getCurrentLoginUser().getGuid().equals(guid)))) {
            if (!source.getEmail().equals(userToUpdate.getEmail())) {
                checkIfEmailExists(source);
            }
            userToUpdate = mapper.update(userToUpdate, source);
            validatePassword(userToUpdate.getPassword());
            userToUpdate.setPassword(passwordEncoder.encode(userToUpdate.getPassword()));
            if (StringUtils.isNotEmpty(userToUpdate.getMailPassword())) {
                userToUpdate.setMailPassword(passwordEncoder.encode(userToUpdate.getMailPassword()));

            }
            return userRepository.save(userToUpdate);
        }
        throw new RestApiException(ErrorCode.UPDATE_WRONG_RESOURCE);
    }

    private void checkIfEmailExists(User userToUpdate) {
        List<String> emails = list().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
        if (emails.contains(userToUpdate.getEmail())) {
            throw new RestApiException(ErrorCode.EMAIL_EXISTS);
        }
    }

    public User getCurrentLoginUser() {
        return userRepository.findByEmail(SecurityConfig.getCurrentUsername());
    }

    public void checkIfAdminExistInDatabase() {
        boolean isAdminInDb = list().stream()
                .map(User::getRole)
                .filter(userRole -> userRole == UserRole.ADMIN)
                .findFirst().isEmpty();
        if (!isAdminInDb) {
            throw new RestApiException(ErrorCode.ADMIN_ACCOUNT_EXIST_IN_DB);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.emptyList());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
