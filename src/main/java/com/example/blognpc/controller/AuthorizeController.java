package com.example.blognpc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.AccessTokenDTO;
import com.example.blognpc.model.GithubUser;
import com.example.blognpc.model.User;
import com.example.blognpc.provider.GithubProvider;
import com.example.blognpc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
public class AuthorizeController {
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code,
                           @RequestParam("state") String state,
                           HttpServletResponse response
                           ) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClientId(clientId);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClientSecret(clientSecret);
        accessTokenDTO.setRedirectUri(redirectUri);
        accessTokenDTO.setState(state);
        String accessToken = GithubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = GithubProvider.getUser(accessToken);
        if (githubUser != null && githubUser.getId() != null) {
            User user = userService.saveOrUpdate(githubUser);
            response.addCookie(new Cookie("token", user.getToken()));
        }
        else {
            log.error("callback get github error, {}", githubUser);
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/";
    }
}
