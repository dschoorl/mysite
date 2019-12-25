package info.rsdev.mysite.util;

import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.ZoneId;

public class FileAttribUtils {

    private FileAttribUtils() {
    }

    public static LocalDate toLocalDateTime(FileTime timestamp) {
        if (timestamp == null) {
            return LocalDate.now();
        }

        return timestamp
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

    }

}
