public class FileRequest extends AbstractMessage {
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public FileRequest(String _fileName) {
        this.fileName = _fileName;
    }
}