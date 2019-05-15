package com.ivan.passbook.service;

import com.ivan.passbook.vo.Response;

/**
 * <h1>获取库存信息：只返回用户没有领取的优惠券，即优惠券库存功能实现接口的定义 </h1>
 * @Author Ivan 14:58
 * @Description TODO
 */
public interface IInventoryService {

    /**
     * <h2>获取库存信息</h2>
     * @param userId 用户 id
     * @return
     * @throws Exception
     */
    Response getIventoryInfo(Long userId) throws Exception;


}
