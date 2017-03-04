package com.noahark.moments.bean;

import java.io.Serializable;

/**
 * ¹Ø×¢ÈË
 */
public class FollowBean implements Serializable {

    private String avatar; //Í·ÏñURI
    private String nickname;//êÇ³Æ

    public FollowBean(){
        super();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}