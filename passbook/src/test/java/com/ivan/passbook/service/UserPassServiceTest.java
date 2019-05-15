package com.ivan.passbook.service;

import com.alibaba.fastjson.JSON;
import com.ivan.passbook.vo.Pass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1>用户优惠券服务测试</h1>
 * @Author Ivan 9:57
 * @Description TODO
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserPassServiceTest extends AbstractServiceTest {

    @Autowired
    private IUserPassService userPassService;

    @Test
    public void testGetUserPassInfo()throws Exception{
        String s = JSON.toJSONString(userPassService.getUserPassInfo(userId));
        System.out.println(s);
    }

    // {"data":[],"errorCode":0,"errorMsg":""}
    @Test
    public void getUserUsedPassInfo()throws Exception{
        String s = JSON.toJSONString(userPassService.getUserUsedPassInfo(userId));
        System.out.println(s);
    }

    @Test
    public void getUserAllPassInfo()throws Exception{
        String s = JSON.toJSONString(userPassService.getUserAllPassInfo(userId));
        System.out.println(s);
    }
//{"errorCode":0,"errorMsg":""}
    @Test
    public void testUserUsePass(){

        Pass pass = new Pass();
        pass.setUserId(userId);
        pass.setTemplateId("cc0662a2c2f643f8a7032fb3f0eae070");
        String s = JSON.toJSONString(userPassService.userUsePass(pass));
        System.out.println(s);
    }



}
