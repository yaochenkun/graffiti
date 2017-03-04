package com.noahark.moments.bean;

import java.io.Serializable;

/**
 * 关注人
 */
public class ChatBean implements Serializable {

    private String avatar; //头像URI
    private String nickname;//昵称
    private String date; //日期
    private String content; //最近一条消息的内容

    public ChatBean() {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}