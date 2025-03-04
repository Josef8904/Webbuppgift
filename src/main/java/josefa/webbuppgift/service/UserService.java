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

    public void processOAuthPostLogin(OAuth2User oAuth2User) {

        Object githubIdObj = oAuth2User.getAttribute("id");

        String githubIdString = null;

        if (githubIdObj instanceof Integer) {
            Integer githubId = (Integer) githubIdObj;
            githubIdString = String.valueOf(githubId);
        } else {
            githubIdString = String.valueOf(githubIdObj);
        }

        Optional<User> existingUser = userRepository.findByGithubId(githubIdString);

        if (existingUser.isEmpty()) {
            User newUser = new User();
            newUser.setGithubId(githubIdString);
            userRepository.save(newUser);
            System.out.println("New GitHub user created: " + githubIdString);
        } else {
            System.out.println("User already exists: " + githubIdString);
        }
    }
}
