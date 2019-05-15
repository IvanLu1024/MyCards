package com.ivan.passbook.controller;

import ch.qos.logback.classic.joran.action.LoggerAction;
import com.ivan.passbook.log.LogConstants;
import com.ivan.passbook.log.LogGenerator;
import com.ivan.passbook.service.IFeedbackService;
import com.ivan.passbook.service.IGainPassTemplateService;
import com.ivan.passbook.service.IInventoryService;
import com.ivan.passbook.service.IUserPassService;
import com.ivan.passbook.vo.Feedback;
import com.ivan.passbook.vo.GainPassTemplateRequest;
import com.ivan.passbook.vo.Pass;
import com.ivan.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>Passbook Rest Controller</h1>
 * @Author Ivan 10:08
 * @Description TODO
 */
@Slf4j
@RestController
@RequestMapping("/passbook")
public class PassbookController {

    /** 用户优惠券服务 */
    private final IUserPassService userPassService;

    /** 优惠券库存服务 */
    private final IInventoryService inventoryService;

    /** 领取优惠券服务 */
    private final IGainPassTemplateService gainPassTemplateService;

    /** 反馈服务 */
    private final IFeedbackService feedbackService;

    /** httpServletRequest */
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public PassbookController(IUserPassService userPassService, IInventoryService inventoryService, IGainPassTemplateService gainPassTemplateService, IFeedbackService feedbackService, HttpServletRequest httpServletRequest) {
        this.userPassService = userPassService;
        this.inventoryService = inventoryService;
        this.gainPassTemplateService = gainPassTemplateService;
        this.feedbackService = feedbackService;
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * <h2>获取用户个人的优惠券信息</h2>
     * @param userId 用户 id
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/userpassinfo")
    Response userPassInfo(Long userId)throws Exception{
        LogGenerator.getLog(httpServletRequest,
                userId, LogConstants.ActionName.USER_PASS_INFO,
                null);

        return userPassService.getUserPassInfo(userId);
    }

    /**
     * <h2>获取用户使用了的优惠券信息</h2>
     * @param userId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/userusedpassinfo")
    Response userUsedPassInfo(Long userId)throws Exception{
        LogGenerator.getLog(httpServletRequest,
                userId, LogConstants.ActionName.USER_USED_PASS_INFO,
                null);
        return userPassService.getUserUsedPassInfo(userId);
    }

    /**
     * <h2>用户使用优惠券</h2>
     * @param pass
     * @return
     */
    @ResponseBody
    @PostMapping("/userusepass")
    Response userUsePass(@RequestBody Pass pass){
        LogGenerator.getLog(httpServletRequest,
                pass.getUserId(), LogConstants.ActionName.USER_USE_PASS,
                null);
        return userPassService.userUsePass(pass);
    }

    /**
     * <h2>获取库存信息</h2>
     * @param userId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/inventoryinfo")
    Response inventoryInfo(Long userId)throws Exception{
        LogGenerator.getLog(httpServletRequest,
                userId, LogConstants.ActionName.INVENTORY_INFO,
                null);
        return inventoryService.getIventoryInfo(userId);

    }

    /**
     * <h2>用户领取优惠券</h2>
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/gainpasstemplate")
    Response gainPassTemplate(@RequestBody GainPassTemplateRequest request)throws Exception{
        LogGenerator.getLog(httpServletRequest,
                request.getUserId(), LogConstants.ActionName.GAIN_PASS_TEMPALTE,
                null);
        return gainPassTemplateService.gainPassTemplate(request);

    }

    /**
     * <h2>用户创建评论</h2>
     * @param feedback
     * @return
     */
    @ResponseBody
    @PostMapping("/createFeedbck")
    Response createFeedbck(@RequestBody Feedback feedback){
        LogGenerator.getLog(httpServletRequest,
                feedback.getUserId(), LogConstants.ActionName.CREATE_FEEDBACK,
                null);
        return feedbackService.createFeedback(feedback);

    }

    /**
     * <h2>用户获取评论信息</h2>
     * @param usedId
     * @return
     */
    @ResponseBody
    @GetMapping("/getfeedback")
    Response getFeedback(Long usedId){
        LogGenerator.getLog(httpServletRequest,
                usedId, LogConstants.ActionName.GET_FEEDBACK,
                null);
        return feedbackService.getFeedback(usedId);
    }

    /**
     * <h2>异常演示接口</h2>
     * @return
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/exception")
    Response exception()throws Exception{
        throw new Exception("Welcome To Ivan Passbook");
    }



}
