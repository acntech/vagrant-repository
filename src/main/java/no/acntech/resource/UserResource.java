package no.acntech.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.acntech.model.CreateUser;
import no.acntech.model.UpdateUser;
import no.acntech.model.User;
import no.acntech.service.UserService;
import no.acntech.util.UrlBuilder;

@RequestMapping(path = "/api/v1/user")
@RestController
public class UserResource {

    private final UserService userService;

    public UserResource(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "{username}")
    public ResponseEntity<User> getUser(@PathVariable(name = "username") final String username) {
        final var user = userService.getUser(username);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody @Valid @NotNull final CreateUser.Request createUserRequest,
                                           final UriComponentsBuilder uriBuilder) {
        final var createUser = createUserRequest.user();
        userService.createUser(createUser);
        final var uri = UrlBuilder.userUri(uriBuilder, createUser.username());
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "{username}")
    public ResponseEntity<Void> updateUser(@PathVariable(name = "username") final String username,
                                           @RequestBody @Valid @NotNull final UpdateUser.Request updateUserRequest) {
        final var updateUser = updateUserRequest.user();
        userService.updateUser(username, updateUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "username") final String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }
}
