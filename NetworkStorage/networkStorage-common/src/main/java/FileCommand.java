public class FileCommand extends AbstractMessage{
    private String command;
    private String fileName;

    public String getCommand() {
        return command;
    }

    public FileCommand(String _command, String _fileName) {
        this.command = _command;
        this.fileName = _fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
