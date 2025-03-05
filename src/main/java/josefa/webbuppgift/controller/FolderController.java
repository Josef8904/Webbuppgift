package josefa.webbuppgift.controller;

import josefa.webbuppgift.entity.Folder;
import josefa.webbuppgift.entity.User;
import josefa.webbuppgift.repository.FolderRepository;
import josefa.webbuppgift.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof OAuth2User oAuth2User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        String githubId = String.valueOf(oAuth2User.getAttribute("id"));
        return userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @GetMapping("/user")
    public ResponseEntity<List<Folder>> getFoldersForLoggedInUser(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        List<Folder> folders = folderRepository.findByOwnerId(user.getId());
        return ResponseEntity.ok(folders);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createFolder(@RequestBody Folder folder, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        folder.setOwner(user);
        folderRepository.save(folder);
        return ResponseEntity.ok("Folder created successfully");
    }

    @Transactional
    @DeleteMapping("/{folderId}")
    public ResponseEntity<String> deleteFolder(@PathVariable Long folderId, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found"));

        if (!folder.getOwner().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this folder");
        }

        folderRepository.delete(folder);
        return ResponseEntity.ok("Folder deleted successfully!");
    }
}
