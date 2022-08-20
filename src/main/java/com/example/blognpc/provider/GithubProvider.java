package com.example.blognpc.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.example.blognpc.dto.AccessTokenDTO;
import com.example.blognpc.model.GithubUser;
import okhttp3.*;

import java.io.IOException;

public class GithubProvider {
    public static String getAccessToken(AccessTokenDTO accessTokenDTO) {
        final MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        SerializeConfig config = new SerializeConfig();
        config.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);
        String content = JSON.toJSONString(accessTokenDTO, config);
        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String token = string.split("&")[0].split("=")[1];
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static GithubUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.github.com/user")
                .header("Authorization", "token " + accessToken)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
