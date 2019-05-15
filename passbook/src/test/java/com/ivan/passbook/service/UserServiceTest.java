package com.ivan.passbook.service;

import com.alibaba.fastjson.JSON;
import com.ivan.passbook.vo.Response;
import com.ivan.passbook.vo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1>用户服务测试</h1>
 * @Author Ivan 16:12
 * @Description TODO
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private IUserService userService;

    @Test
    public void testCreateUser() throws Exception{

        User user = new User();
        user.setBaseInfo(
                new User.BaseInfo("ivan",10,"m"));
        user.setOtherInfo(
                new User.OtherInfo("123456","宇宙银河系"));
        Response res = userService.createUser(user);
        System.out.println(JSON.toJSONString(res));
        //{"data":{"baseInfo":{"age":10,"name":"ivan","sex":"m"},
        // "id":190756,"otherInfo":{"address":"宇宙银河系","phone":"123456"}},
        // "errorCode":0,"errorMsg":""}



    }
}
