package com.ivan.passbook.merchants.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>通用的响应对象</h1>
 * @Author Ivan 14:03
 * @Description TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    /** 错误码，正确返回0  */
    private Integer errorCode = 0;

    /** 错误信息，正确返回空字符串 */
    private String errorMsg = "";

    /** 返回值对象 */
    private Object data;

    /**
    <h2> 正确的响应构造函数</h2>
     */
    public Response(Object data) {
        this.data = data;
    }


}
