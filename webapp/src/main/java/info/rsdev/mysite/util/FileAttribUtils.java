package info.rsdev.mysite.util;

import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class FileAttribUtils {

    private FileAttribUtils() {
    }

    public static LocalDateTime toLocalDateTime(FileTime timestamp) {
        if (timestamp == null) {
            return LocalDateTime.now();
        }

        return timestamp
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

    }

}
