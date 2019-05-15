package com.ivan.passbook.service;

import com.ivan.passbook.vo.Pass;
import com.ivan.passbook.vo.Response;

/**
 * <h1>获取用户个人优惠券信息</h1>
 * @Author Ivan 15:03
 * @Description TODO
 */
public interface IUserPassService {

    /**
     * <h2>获取用户个人优惠券信息，即我的优惠券功能实现</h2>
     * @param userId 用户 id
     * @return
     * @throws Exception
     */
    Response getUserPassInfo(Long userId) throws Exception;

    /**
     * <h2>获取用户已经消费了的优惠券，即已使用的优惠券功能实现</h2>
     * @param userId
     * @return
     * @throws Exception
     */
    Response getUserUsedPassInfo(Long userId) throws Exception;

    /**
     * <h2>获取用户所有的优惠券</h2>
     * @param userId
     * @return
     * @throws Exception
     */
    Response getUserAllPassInfo(Long userId) throws Exception;

    /**
     * <h2>用户使用优惠券</h2>
     * @param pass
     * @return
     */
    Response userUsePass(Pass pass) ;


}
