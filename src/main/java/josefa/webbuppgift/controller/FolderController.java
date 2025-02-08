package josefa.webbuppgift.controller;

import josefa.webbuppgift.entity.Folder;
import josefa.webbuppgift.entity.User;
import josefa.webbuppgift.repository.FolderRepository;
import josefa.webbuppgift.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    public ResponseEntity<?> getFoldersForLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println("Logged-in user: " + username);

        if (username == null || username.isEmpty() || username.equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user is authenticated.");
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            System.out.println("User not found in the database!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        List<Folder> folders = folderRepository.findByOwnerId(user.get().getId());

        System.out.println("Number of folders found: " + folders.size());

        return ResponseEntity.ok(folders);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createFolder(@RequestBody Folder folder) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (username == null || username.isEmpty() || username.equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user is authenticated.");
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        folder.setOwner(user.get());
        folderRepository.save(folder);
        return ResponseEntity.ok("Folder created successfully");
    }
}
