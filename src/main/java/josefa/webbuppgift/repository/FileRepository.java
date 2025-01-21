package josefa.webbuppgift.repository;

import josefa.webbuppgift.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByFolderId(Long folderId);
    List<File> findByOwnerId(Long ownerId);
}
