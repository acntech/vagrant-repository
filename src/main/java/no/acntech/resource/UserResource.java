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

import no.acntech.model.CreateUser;
import no.acntech.model.UpdateUserPassword;
import no.acntech.model.User;
import no.acntech.service.UserService;

@RequestMapping(path = "/api/user")
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
    public ResponseEntity<Void> createUser(@RequestBody final CreateUser createUser,
                                           final UriComponentsBuilder uriBuilder) {
        userService.createUser(createUser);
        final var uri = uriBuilder.path("/api/user/{username}")
                .buildAndExpand(createUser.username())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping(path = "{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "username") final String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "{username}/password")
    public ResponseEntity<Void> updateUserPassword(@PathVariable(name = "username") final String username,
                                                   @RequestBody final UpdateUserPassword updateUserPassword) {
        userService.updatePassword(username, updateUserPassword);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "{username}/role")
    public ResponseEntity<Void> updateUserRole(@PathVariable(name = "username") final String username,
                                               @RequestBody final UpdateUserPassword updateUserPassword) {
        userService.updatePassword(username, updateUserPassword);
        return ResponseEntity.ok().build();
    }
}
