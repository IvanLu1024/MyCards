package com.ivan.passbook.vo;

import com.ivan.passbook.entity.Merchants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>用户领取的优惠券信息，即HBase中Pass表返回给用户的信息</h1>
 *
 * @Author Ivan 14:54
 * @Description TODO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassInfo {

    /** 优惠券 */
    private Pass pass;

    /** 优惠券模板 */
    private PassTemplate passTemplate;

    /** 优惠券对应的商户 */
    private Merchants merchants;
}
