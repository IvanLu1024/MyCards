package com.ivan.passbook.service;

import com.alibaba.fastjson.JSON;
import com.ivan.passbook.constant.FeedbackType;
import com.ivan.passbook.vo.Feedback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1>用户反馈服务测试</h1>
 * @Author Ivan 10:47
 * @Description TODO
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FeedbackServiceTest extends AbstractServiceTest {

    @Autowired
    private IFeedbackService feedbackService;


    @Test
    public void testCreateFeedback(){
        Feedback appFeedback = new Feedback();
        appFeedback.setUserId(userId);
        appFeedback.setType(FeedbackType.APP.getCode());
        appFeedback.setTemplateId("-1");
        appFeedback.setComment("分布式卡包应用");

        String s = JSON.toJSONString(feedbackService.createFeedback(appFeedback));
        System.out.println(s);

        Feedback passFeedback = new Feedback();
        passFeedback.setUserId(userId);
        passFeedback.setType(FeedbackType.PASS.getCode());
        passFeedback.setTemplateId("cc0662a2c2f643f8a7032fb3f0eae070");
        passFeedback.setComment("优惠券评论");

        String p = JSON.toJSONString(feedbackService.createFeedback(passFeedback));
        System.out.println(p);

    }
//    {
//        "data": [
//        {
//            "comment": "优惠券评论",
//                "templateId": "cc0662a2c2f643f8a7032fb3f0eae070",
//                "type": "pass",
//                "userId": 190756
//        },
//        {
//            "comment": "分布式卡包应用",
//                "templateId": "-1",
//                "type": "app",
//                "userId": 190756
//        },
//        {
//            "comment": "分布式卡包应用",
//                "templateId": "-1",
//                "type": "app",
//                "userId": 190756
//        }
//    ],
//        "errorCode": 0,
//            "errorMsg": ""
//    }
    @Test
    public void testGetFeedback(){
        String s = JSON.toJSONString(feedbackService.getFeedback(userId));
        System.out.println(s);
    }

}
