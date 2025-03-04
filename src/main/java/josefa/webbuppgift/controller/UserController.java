package josefa.webbuppgift.controller;

import josefa.webbuppgift.entity.User;
import josefa.webbuppgift.repository.UserRepository;
import josefa.webbuppgift.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof OAuth2User oAuth2User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        String githubId = String.valueOf(oAuth2User.getAttribute("id"));
        System.out.println("🔍 Checking logged-in user: " + githubId);

        String gitHubUserData = gitHubService.getGitHubUserData(oAuth2User);
        System.out.println("GitHub User Data: " + gitHubUserData);

        Optional<User> userOpt = userRepository.findByGithubId(githubId);
        if (userOpt.isEmpty()) {
            User newUser = new User();
            newUser.setGithubId(githubId);
            newUser.setUsername(String.valueOf(oAuth2User.getAttribute("login")));
            newUser.setPassword("dummy_password");

            userRepository.save(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body("User created with dummy password");
        }

        return ResponseEntity.ok(userOpt.get());
    }
}
