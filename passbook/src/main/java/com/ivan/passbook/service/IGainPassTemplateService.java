package com.ivan.passbook.service;

import com.ivan.passbook.vo.GainPassTemplateRequest;
import com.ivan.passbook.vo.Response;

/**
 * <h1>用户领取优惠券功能实现</h1>
 * @Author Ivan 15:01
 * @Description TODO
 */
public interface IGainPassTemplateService  {

    /**
     * <h2> 用户领取优惠券 </h2>
     * @param request
     * @return
     * @throws Exception
     */
    Response gainPassTemplate(GainPassTemplateRequest request) throws Exception;
}
