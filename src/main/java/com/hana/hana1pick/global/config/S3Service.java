package com.hana.hana1pick.global.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hana.hana1pick.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.hana.hana1pick.global.exception.BaseResponseStatus.CELEBRITY_UPLOAD_FAIL;

@Service
@RequiredArgsConstructor
public class S3Service {
	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String uploadPng(MultipartFile multipartFile, String folder) {
		// 1. 랜덤 UUID를 생성하여 파일 이름으로 사용
		String fileName = UUID.randomUUID().toString() + ".png";

		// 2. ObjectMetadata 객체를 생성하여 파일 메타데이터 설정
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType("image/png"); // 파일의 Content-Type을 설정 (PNG 이미지 파일)
		metadata.setContentLength(multipartFile.getSize()); // 파일의 크기를 설정

		try(InputStream inputStream = multipartFile.getInputStream()) {
			// 3. S3 버킷에 파일을 업로드
			amazonS3.putObject(new PutObjectRequest(bucket + "/" + folder + "/png", fileName, inputStream, metadata));

			// 4. 업로드한 파일의 URL을 반환
			return amazonS3.getUrl(bucket+"/"+folder + "/png" , fileName).toString();
		} catch(IOException e) {
			// 5. 업로드 중 예외가 발생하면 BaseException을 던짐
			throw new BaseException(CELEBRITY_UPLOAD_FAIL);
		}
	}
}