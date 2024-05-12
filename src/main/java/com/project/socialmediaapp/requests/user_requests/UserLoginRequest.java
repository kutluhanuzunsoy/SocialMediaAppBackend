package com.project.socialmediaapp.requests.user_requests;

import lombok.Data;

@Data
public class UserLoginRequest {
    String userName;
    String password;
}
