package pavel.lobanov.reviews.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pavel.lobanov.reviews.domain.User;
import pavel.lobanov.reviews.dto.LoginResponse;
import pavel.lobanov.reviews.dto.UserDto;
import pavel.lobanov.reviews.services.AuthenticationService;
import pavel.lobanov.reviews.services.JwtService;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {
    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody UserDto userDto) {
        return authenticationService.userToUserDto(authenticationService.registerUser(userDto));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody UserDto userDto) {
        User authenticatedUser = authenticationService.authenticateUser(userDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return loginResponse;
    }
}
