package com.example.hpsus.clientauthdocsvision.Common;

import okhttp3.MediaType;

public class Common {
    public static final String API_SERVERA_DEFAULT = "http://almaz2.digdes.com/syncwebservice/api/SyncMessage/";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final String URL_POST_MESSAGE = "PostMessage";
    public static final String URL_GET_MESSAGE = "GetMessages";
    public static final String URL_POST_AUTH_MESSAGE = "PostAuthMessage";
    public static final String URL_GET_AUTH_MESSAGE = "GetAuthMessages";
    public static final String URL_MESSAGE = "/syncwebservice/api/SyncMessage/";
    public static final String ERROR_LOGIN = "неправильный логин";
    public static final String ERROR_PASSWORD = "неправильный пароль";
    public static final String ERROR_BASE= "выберите базу из списка";
    public static final String ERROR_NAME_SERVER= "неправильный адрес сервера";
}
