package com.ivan.passbook.merchants.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>创建商户响应对象</h1>
 * @Author Ivan 14:32
 * @Description TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMerchantsResponse {

    /** 商户 id：创建失败则为 -1 */
    private Integer id;


}
