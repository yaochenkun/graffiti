package com.noahark.moments.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chicken on 2016/11/21.
 */
public class CircleBean implements Serializable {

    private String avatar; //头像url
    private String nickname; //昵称
    private String content; //内容
    private String time; //时间
    private List<String> pictureList; //图片列表

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<String> pictureList) {
        this.pictureList = pictureList;
    }
}
