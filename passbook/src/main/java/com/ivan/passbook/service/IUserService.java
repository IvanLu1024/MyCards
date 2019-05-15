package com.ivan.passbook.service;

import com.ivan.passbook.vo.Response;
import com.ivan.passbook.vo.User;

/**
 * <h1>用户服务： 创建 User 服务 ，具体就是将注册的用户写入HBase </h1>
 */
public interface IUserService {

    /**
     * <h2>创建用户</h2>
     * @param user {@link User}
     * @return {@link Response}
     * @throws Exception
     */
    Response createUser(User user) throws Exception;
}
