package com.ivan.passbook.service;

import com.alibaba.fastjson.JSON;
import com.ivan.passbook.vo.GainPassTemplateRequest;
import com.ivan.passbook.vo.PassTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author Ivan 9:41
 * @Description TODO
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GainPassTemplateServiceTest extends AbstractServiceTest {

    @Autowired
    private IGainPassTemplateService gainPassTemplateService;

    @Test
    public void testGainPassTemplate() throws Exception{
        PassTemplate target = new PassTemplate();
        target.setId(3);
        target.setTitle("晚餐券");
        target.setHasToken(true);

        String res = JSON.toJSONString(gainPassTemplateService.gainPassTemplate(
                new GainPassTemplateRequest(userId, target)
        ));

        System.out.println(res);


    }

}
