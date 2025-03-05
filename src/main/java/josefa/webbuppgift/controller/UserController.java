package josefa.webbuppgift.controller;

import josefa.webbuppgift.entity.User;
import josefa.webbuppgift.repository.UserRepository;
import josefa.webbuppgift.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof OAuth2User oAuth2User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        try {
            User user = userService.processOAuthLogin(oAuth2User);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to process OAuth login.");
            }

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing OAuth login: " + e.getMessage());
        }
    }
}
