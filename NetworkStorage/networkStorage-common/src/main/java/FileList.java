import javafx.scene.control.ListView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
public class FileList extends AbstractMessage {
    private String fileName;
    private List<String> fileList;
    private byte[] data;


    public List<String> getFileList() {
        return fileList;
    }

    public String getFileName() {
        return fileName;
    }

    public String get(int i) {
        return fileList.get(i);
    }

    public byte[] getData() {
        return data;
    }

    public FileList(List<String> _fileList) throws IOException {
        this.fileList = _fileList;
    }
}
