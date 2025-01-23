package josefa.webbuppgift.controller;

import josefa.webbuppgift.entity.Folder;
import josefa.webbuppgift.entity.User;
import josefa.webbuppgift.repository.FolderRepository;
import josefa.webbuppgift.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // Skapa en ny mapp
    @PostMapping("/create")
    public ResponseEntity<String> createFolder(@RequestBody Folder folder, @RequestParam Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            folder.setOwner(user.get());
            folderRepository.save(folder);
            return ResponseEntity.ok("Folder created successfully");
        }
        return ResponseEntity.badRequest().body("User not found");
    }

    // Visa alla mappar för en användare
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Folder>> getFoldersByUser(@PathVariable Long userId) {
        List<Folder> folders = folderRepository.findByOwnerId(userId);
        return ResponseEntity.ok(folders);
    }

    // Radera en mapp
    @DeleteMapping("/{folderId}")
    public ResponseEntity<String> deleteFolder(@PathVariable Long folderId) {
        if (folderRepository.existsById(folderId)) {
            folderRepository.deleteById(folderId);
            return ResponseEntity.ok("Folder deleted successfully");
        }
        return ResponseEntity.badRequest().body("Folder not found");
    }
}
