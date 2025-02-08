package josefa.webbuppgift.controller;

import josefa.webbuppgift.entity.File;
import josefa.webbuppgift.entity.Folder;
import josefa.webbuppgift.repository.FileRepository;
import josefa.webbuppgift.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderRepository folderRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("folderId") Long folderId,
                                        @RequestParam("file") MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found"));

        if (!folder.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this folder!");
        }

        try {
            File newFile = new File();
            newFile.setName(file.getOriginalFilename());
            newFile.setContent(file.getBytes());
            newFile.setOwner(folder.getOwner());
            newFile.setFolder(folder);

            fileRepository.save(newFile);
            return ResponseEntity.ok("File uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed!");
        }
    }

    @Transactional
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<File>> getFilesByFolder(@PathVariable Long folderId) {
        List<File> files = fileRepository.findByFolderId(folderId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<File> file = fileRepository.findById(fileId);
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        File foundFile = file.get();

        if (!foundFile.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + foundFile.getName() + "\"")
                .body(foundFile.getContent());
    }

    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<File> fileOptional = fileRepository.findById(fileId);

        if (fileOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found!");
        }

        File file = fileOptional.get();

        if (!file.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this file!");
        }

        fileRepository.delete(file);
        return ResponseEntity.ok("File deleted successfully!");
    }
}
