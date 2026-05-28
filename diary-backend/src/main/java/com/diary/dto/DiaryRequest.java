package com.diary.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class DiaryRequest {
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最长200字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String mood;

    private String weather;

    private String tags;

    private Boolean isPublic;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
}