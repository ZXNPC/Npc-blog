package com.example.blognpc.controller;

import com.example.blognpc.dto.AccessTokenDTO;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.LoginErrorCode;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                           HttpServletResponse response,
                           RedirectAttributes redirectAttributes
    ) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClientId(clientId);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClientSecret(clientSecret);
        accessTokenDTO.setRedirectUri(redirectUri);
        accessTokenDTO.setState(state);
        String accessToken = GithubProvider.getAccessToken(accessTokenDTO);
        if (accessToken == null) {
            log.error("Getting accesstoken failed.");
            redirectAttributes.addFlashAttribute("resultDTO", ResultDTO.errorOf(LoginErrorCode.GITHUB_OAUTH_ERROR));
            return "redirect:/login";
        }
        GithubUser githubUser = GithubProvider.getUser(accessToken);
        if (githubUser != null && githubUser.getId() != null) {
            User user = userService.saveOrUpdate(githubUser);
            response.addCookie(new Cookie("token", user.getToken()));
            return "redirect:/";
        } else {
            log.error("callback get github error, {}", githubUser);
            redirectAttributes.addFlashAttribute("resultDTO", ResultDTO.errorOf(LoginErrorCode.GITHUB_OAUTH_ERROR));
            return "redirect:/login";
        }
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
