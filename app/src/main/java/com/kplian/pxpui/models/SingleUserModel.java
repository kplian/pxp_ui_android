package com.kplian.pxpui.models;

public class SingleUserModel {

//    private String name;
//    private String surname;
//    private String email;
//    private String token;
//    private String url_photo;
//    private String login_type;
//
//    private String code;
//    private String usuario;
//    private String type;
//    private String language;

//    private String device = "android";

//    name, surname, email, token, url_photo, type, device, language = ''

    private String name;
    private String surname;
    private String email;
    private String token;
    private String userId;
    private String url_photo;
    private String type;
    private String device;
    private String language;

    public SingleUserModel(String name, String surname, String email, String token, String userId,
                           String url_photo, String type, String language, String device) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.token = token;
        this.userId = userId;
        this.url_photo = url_photo;
        this.type = type;
        this.device = device;
        this.language = language;
    }

    @Override
    public String toString() {
        return "SingleUserModel{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                ", url_photo='" + url_photo + '\'' +
                ", type='" + type + '\'' +
                ", device='" + device + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
