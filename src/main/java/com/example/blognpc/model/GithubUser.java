package com.example.blognpc.model;

import lombok.Data;

import java.util.Map;

@Data
public class GithubUser {
    String login;
    String id;
    String nodeId;
    String avatarUrl;
    String gravatarId;
    String url;
    String htmlUrl;
    String followersUrl;
    String followingUrl;
    String gistsUrl;
    String starredUrl;
    String subscriptionsUrl;
    String organizationsUrl;
    String reposUrl;
    String eventsUrl;
    String receivedEventsUrl;
    String type;
    String site_admin;
    String name;
    String company;
    String blog;
    String location;
    String email;
    String hireable;
    String bio;
    String twitterUsername;
    Long publicRepos;
    Long publicGists;
    Long followers;
    Long following;
    Long createdAt;
    Long updatedAt;
    Long privateGists;
    Long totalPrivateRepos;
    Long ownedPrivateRepos;
    Long diskUsage;
    Long collaborators;
    Boolean twoFactorAuthentication;
    Plan plan;
    @Data
    private class Plan {
        String name;
        Long space;
        Long collaborators;
        Long private_repos;
    }
}
