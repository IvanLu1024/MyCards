package com.ivan.passbook.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1>库存服务测试</h1>
 * @Author Ivan 9:20
 * @Description TODO
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IventoryServiceTest extends AbstractServiceTest {

    @Autowired
    private IInventoryService inventoryService;

    @Test
    public void testGetInventoryInfo() throws Exception{
        String res = JSON.toJSONString(inventoryService.getIventoryInfo(userId),
                SerializerFeature.DisableCircularReferenceDetect);
        System.out.println(res);
        //{"data":{"passTemplateInfos":
        // [{"merchants":{"address":"北京市海淀区","businessLicenseUrl":"www.qq.com","id":3,"isAudit":true,"logoUrl":"www.baidu.com","name":"小鹿","phone":"13355645684"},
        // "passTemplate":{"background":2,"desc":"详情：请点击查看","end":1558540800000,"hasToken":false,"id":3,"limit":10000,"start":1556812800000,"summary":"简介: SEU","title":"午餐券-1"}}],
        // "userId":190756},"errorCode":0,"errorMsg":""}

    }

}
