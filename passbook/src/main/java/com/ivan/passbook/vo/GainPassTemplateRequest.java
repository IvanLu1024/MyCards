package com.ivan.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>用户领取优惠券的请求对象</h1>
 * @Author Ivan 14:53
 * @Description TODO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GainPassTemplateRequest {

    /** 用户 id */
    private Long userId;

    /** PassTemplate 对象 */
    private PassTemplate passTemplate;
}
