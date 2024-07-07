package com.hana.hana1pick.domain.user.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hana.hana1pick.domain.user.dto.response.UserInfoResDto;
import com.hana.hana1pick.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.hana.hana1pick.global.exception.BaseResponseStatus.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    @Value("${app.kakao.url.login}")
    private String kakaoLoginUri;

    @Value("${app.kakao.url.logout}")
    private String kakaoLogoutUri;

    @Value("${app.kakao.rest_api_key}")
    private String kakaoClientId;

    @Value("${app.kakao.login-redirect}")
    private String kakaoLoginRedirectUri;

    @Value("${app.kakao.logout-redirect}")
    private String kakaoLogoutRedirectUri;

    @Value("${app.kakao.url.token}")
    private String kakaoTokenUri;

    @Value("${app.kakao.url.profile}")
    private String kakaoProfileUri;

//  private final HttpSession httpSession;
//  private final UserRepository userRepository;

    UserInfoResDto userDTO;

    // 1. 인가 코드 발급 받기
    public String getLoginRedirectUrl() {
        StringBuilder url = new StringBuilder()
                .append(kakaoLoginUri)
                .append("?response_type=code")
                .append("&client_id=").append(kakaoClientId)
                .append("&redirect_uri=").append("http://localhost:8080").append(kakaoLoginRedirectUri);

        return String.valueOf(url);
    }

    // 2. AccessToken 발급 받기
    public String getAccessToken(String code) {
        String accessToken = "";

        try {
            URL url = new URL(kakaoTokenUri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 파라미터 Form 맞추기
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(kakaoClientId);
            sb.append("&redirect_uri=").append(URLEncoder.encode("http://localhost:8080" + kakaoLoginRedirectUri, StandardCharsets.UTF_8));
            sb.append("&code=").append(URLEncoder.encode(code, StandardCharsets.UTF_8));

            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8))) {
                bw.write(sb.toString());
                bw.flush();
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }

                    JsonElement element = JsonParser.parseString(result.toString());
                    accessToken = element.getAsJsonObject().get("access_token").getAsString();
                    log.info("액세스 토큰이에요 ~ accessToken : " + accessToken);
                }
            } else {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }
                    log.error("에러에러 Error response: " + result);
                }
                throw new IOException("Failed to get access token, response code: " + responseCode);
            }
        } catch (IOException e) {
            log.error("IOException occurred while getting access token: ", e);
        } catch (Exception e) {
            log.error("Exception occurred while getting access token: ", e);

            // throw new BaseException(USER_NOT_FOUND);
        }
        return accessToken;
    }

    // 3. 로그인 처리
    // 발급 받은 토큰으로 사용자 정보 조회
    public UserInfoResDto getUserInfo(String accessToken) {

        try {
            URL url = new URL(kakaoProfileUri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            JsonElement element = JsonParser.parseString(result.toString());

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            // 이메일
            String email = kakaoAccount.getAsJsonObject().get("email").getAsString();
            // 프로필 사진
            String profileUrl = properties.getAsJsonObject().get("profile_image").getAsString();

            userDTO = UserInfoResDto.builder()
                    .profile(profileUrl)
                    .email(email)
                    .build();
        } catch (IOException e) {
            log.error("IOException occurred while getting user info: ", e);
        } catch (Exception e) {
            log.error("Exception occurred while getting user info: ", e);
            throw new BaseException(USER_NOT_FOUND);
        }
        return userDTO;
    }

    // TODO: logout 로직 구현 필요(마이페이지, 세션 30분 이상 지나면?)
    public void logout(String accessToken) {
        try {
            URL url = new URL(kakaoLogoutUri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode();
            log.info("[카카오 로그아웃] responseCode : {}", responseCode);

            BufferedReader br;
            if (responseCode >= 200 && responseCode <= 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line = "";
            StringBuilder responseSb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                responseSb.append(line);
            }
            String result = responseSb.toString();
            log.info("kakao logout - responseBody = {}", result);
        } catch (IOException e) {
            log.error("IOException occurred while logging out: ", e);
        } catch (Exception e) {
            log.error("Exception occurred while logging out: ", e);
        }
    }
}
