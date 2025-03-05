package josefa.webbuppgift.service;

import josefa.webbuppgift.entity.User;
import josefa.webbuppgift.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User processOAuthLogin(OAuth2User oAuth2User) {
        Object githubIdObj = oAuth2User.getAttribute("id");
        String githubIdString;

        if (githubIdObj instanceof Integer) {
            githubIdString = String.valueOf(githubIdObj);
        } else if (githubIdObj instanceof String) {
            githubIdString = (String) githubIdObj;
        } else {
            System.out.println(" Unknown GitHub ID type: " + (githubIdObj != null ? githubIdObj.getClass().getName() : "null"));
            return null;
        }

        Object usernameObj = oAuth2User.getAttribute("login");
        String username;

        if (usernameObj instanceof String) {
            username = (String) usernameObj;
        } else {
            System.out.println(" Unknown username format: " + (usernameObj != null ? usernameObj.getClass().getName() : "null"));
            return null;
        }

        System.out.println(" Checking OAuth login: GitHub ID: " + githubIdString + " - Username: " + username);

        Optional<User> existingUser = userRepository.findByGithubId(githubIdString);
        if (existingUser.isPresent()) {
            System.out.println(" User already exists: " + existingUser.get().getUsername());
            return existingUser.get();
        }

        Optional<User> userByUsername = userRepository.findByUsername(username);
        if (userByUsername.isPresent()) {
            System.out.println(" Username already taken: " + username);
            username = username + "_" + githubIdString;
            System.out.println(" New username generated: " + username);
        }

        User newUser = new User();
        newUser.setGithubId(githubIdString);
        newUser.setUsername(username);
        newUser.setPassword("oauth_user");

        userRepository.save(newUser);
        System.out.println("New GitHub user created: " + username);

        return newUser;
    }
}
