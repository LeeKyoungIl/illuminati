package me.phoboslabs.illuminati.common.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileUtil {

    private final static Logger FILE_UTIL_LOGGER = LoggerFactory.getLogger(FileUtil.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String ILLUMINATI_DATA_FILE_NAME_POSTFIX = "_illuminati_data.log";

    private static final String ENCODING = "utf-8";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static String generateFileName() {
        Date nowDate = new Date();
        StringBuilder illuminatiDataFileName = new StringBuilder();
        String stringDate = FileUtil.DATE_FORMAT.format(nowDate);
        illuminatiDataFileName.append(stringDate);
        illuminatiDataFileName.append(ILLUMINATI_DATA_FILE_NAME_POSTFIX);

        return illuminatiDataFileName.toString();
    }

    public static File generateFile(String basePath, String fileName) {
        File file = new File(basePath, fileName);

        try {
            if (file.createNewFile()) {
                return file;
            } else if (file.exists()) {
                return file;
            }
        } catch (IOException e) {
            FILE_UTIL_LOGGER.error("File create error : ", e.getMessage());
            return null;
        }

        return null;
    }

    public static boolean isFileExists(String basePath, String fileName) {
        File file = new File(basePath, fileName);

        if (file.exists()) {
            return true;
        }

        return false;
    }

    public static void appendDataToFileByOnce(File file, String textData) {
        if (file.canWrite()) {
            try {
                long start = System.currentTimeMillis();
                FileWriter writer = new FileWriter(file, true);
                writer.append(textData);
                writer.flush();
                writer.close();
                long end = System.currentTimeMillis();
                FILE_UTIL_LOGGER.info("Time spent writing files : " + ((end - start) / 1000f) + " seconds");
            } catch (IOException e) {
                FILE_UTIL_LOGGER.error("File write error : ", e.getMessage());
            }
        } else {
            FILE_UTIL_LOGGER.error("Can't write file : " + file.getAbsolutePath());
        }
    }

    public static void appendDataToFile(File file, List<String> dataList) {
        if (file.canWrite()) {
            try {
                FileWriter writer = new FileWriter(file, true);
                write(dataList, writer);
            } catch (IOException e) {
                FILE_UTIL_LOGGER.error("File write error : ", e.getMessage());
            }
        } else {
            FILE_UTIL_LOGGER.error("Can't write file : " + file.getAbsolutePath());
        }
    }

    private static void write(List<String> dataList, Writer writer) throws IOException {
        long start = System.currentTimeMillis();
        for (String data: dataList) {
            writer.append(data + LINE_SEPARATOR);
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        FILE_UTIL_LOGGER.info("Time spent writing files : " + ((end - start) / 1000f) + " seconds (" + dataList.size() + " line)");
    }

    public static boolean isDirectoryExists(String directoryName) {
        File file = new File(directoryName);

        if (file.exists() && file.isDirectory()) {
            return true;
        }

        return false;
    }

    public static boolean createDirectory(String directoryName) {
        if (isDirectoryExists(directoryName)) {
            FILE_UTIL_LOGGER.info(directoryName + " is already exists.");
            return true;
        }

        try {
            File file = new File(directoryName);
            return file.mkdir();
        } catch (SecurityException ex) {
            FILE_UTIL_LOGGER.info("check your dir permission.");
            return false;
        }
    }

    public static List<String> getDataFromFile (File fileOb) {
        List<String> readDataLines = null;
        try {
            readDataLines = FileUtils.readLines(fileOb, ENCODING);
        } catch (IOException e) {
            FILE_UTIL_LOGGER.info("check your file.", e.getMessage());
        }

        return readDataLines;
    }
}
