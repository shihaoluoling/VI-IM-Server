package com.v.im.api.entity;

import lombok.Data;

/**
 * websocket 通讯的json 封装
 *
 * @author 皓天
 * @since 2020-7-07
 */
@Data
public class SendInfo {

    /**
     * 发送信息的代码
     */
    private String code;

    /**
     * 信息
     */
    private Message message;


}
