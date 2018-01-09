package com.leekyoungil.illuminati.common.util

import spock.lang.Specification

import java.text.DateFormat
import java.text.SimpleDateFormat

class FileUtilTest extends Specification {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String TODAY = DATE_FORMAT.format(new Date());

    def "file name generate test" () {
        setup:
        String fileName;

        when:
        fileName = FileUtil.generateFileName();

        then:
        fileName == TODAY + "_illuminati_data.log";
    }

    def "file exists check" () {
        setup:
        String basePath = "./";
        String fileName;

        when:
        fileName = FileUtil.generateFileName();
        boolean isFileExists = FileUtil.isFileExists(basePath, fileName);

        then:
        isFileExists == false;
    }

    def "file generate test" () {
        setup:
        String basePath = "./";
        String fileName;

        when:
        fileName = FileUtil.generateFileName();
        boolean isFileExists = FileUtil.isFileExists(basePath, fileName);
        File fileObj = null;
        if (isFileExists == false) {
            fileObj = FileUtil.generateFile(basePath, fileName);
            isFileExists = FileUtil.isFileExists(basePath, fileName);
        }

        then:
        fileObj != null;
        isFileExists == true;

        if (fileObj != null) {
            fileObj.delete();
        }
    }

    def "append date to file" () {
        setup:
        String basePath = "./";
        String fileName;

        List<String> dataList = new ArrayList<>();
        String dataString = "test";

        when:
        fileName = FileUtil.generateFileName();
        boolean isFileExists = FileUtil.isFileExists(basePath, fileName);
        File fileObj = FileUtil.generateFile(basePath, fileName);

        if (fileObj != null) {
            dataList.add(dataString);
            FileUtil.appendDataToFile(fileObj, dataList);
        }

        then:
        fileObj != null;
        fileObj.length() > 0;

        if (fileObj != null) {
            fileObj.delete();
        }
    }
}
