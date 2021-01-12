package ru.sibdigital.proccovid.utils;

public class AppUtils {

    public static String getFileNameFromPath(String path) {
        if (path == null) {
            return "";
        }
        String[] split = path.split("\\\\");
        split = split[split.length - 1].split("/");
        String fileName = split[split.length - 1];
        return fileName;
    }

    public static void main(String[] args) {
        String path = "upload.path\\tmp/f3e96a28-2e6f-4959-add2-acef4212453a_doc01504820200407045447.pdf";
//        String path = "/uploads/b1edf465-926b-47c6-a2c1-b529f95d4f91_DSCN2646.zip";
        String fullName = AppUtils.getFileNameFromPath(path);
        String[] subStrings = fullName.split("\\.");
        String fileName = subStrings[0];
        String fileExtension = subStrings.length > 1 ? subStrings[subStrings.length - 1] : "";
        System.out.println("fileName: " + fileName);
        System.out.println("fileExtension: " + fileExtension);
    }
}
