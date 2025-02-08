package josefa.webbuppgift.Service;

import josefa.webbuppgift.entity.File;
import josefa.webbuppgift.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public Optional<File> getFileById(Long fileId) {
        return fileRepository.findById(fileId);
    }
}
