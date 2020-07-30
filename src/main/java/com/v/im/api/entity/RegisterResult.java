package com.v.im.api.entity;


import lombok.Data;

/**
 * 注册返回信息
 *
 * @author 皓天
 * @since 2020-7-07
 */
@Data
public class RegisterResult {

    public static final String SUCCESS = "0";

    public static final String ERROR = "1";

    /**
     * 返回代码
     */
    private String resultCode;

    /**
     * 返回结果
     */
    private String message;


}
