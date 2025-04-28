package josefa.webbuppgift.service;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class GitHubService {

    public String getGitHubUserData(OAuth2User oAuth2User) {
        return "User GitHub Data: " + oAuth2User.getAttributes().toString();
    }
}
