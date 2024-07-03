package com.hana.hana1pick.global.util;


import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUpload {
    public  static void deleteFile(String filename) throws IOException {
        Path filePath = Paths.get(filename);
        Files.delete(filePath);
    }
    public  static void saveFile(MultipartFile mf) throws IOException {
        byte [] data;
        String imgname = mf.getOriginalFilename();
        System.out.println("----------------"+imgname);

        // 저장할 디렉토리 경로
        String uploadDir = "uploads/";
        File directory = new File(uploadDir);

        // 디렉토리가 존재하지 않으면 생성
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일 저장 경로 설정
        String filePath = uploadDir + imgname;

        try {
            data = mf.getBytes();
            try (FileOutputStream fo = new FileOutputStream(filePath)) {
                fo.write(data);
            }
//            FileOutputStream fo =
//                    new FileOutputStream(imgname);
//            fo.write(data);
//            fo.close();
            System.out.println("----------------OK");

        }catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}