package com.project.socialmediaapp.responses;

import com.project.socialmediaapp.entities.User;
import lombok.Data;

@Data
public class UserResponse {
    Long id;
    int avatarId;
    String userName;

    public UserResponse (User entity) {
        this.id = entity.getId();
        this.avatarId = entity.getAvatarId();
        this.userName = entity.getUserName();
    }
}
