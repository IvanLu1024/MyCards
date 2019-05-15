package com.ivan.passbook.controller;

import com.ivan.passbook.log.LogConstants;
import com.ivan.passbook.log.LogGenerator;
import com.ivan.passbook.service.IUserPassService;
import com.ivan.passbook.service.IUserService;
import com.ivan.passbook.vo.Response;
import com.ivan.passbook.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>创建用户服务</h1>
 * @Author Ivan 10:50
 * @Description TODO
 */
@Slf4j
@RestController
@RequestMapping("/passbook")
public class CreateUserController {

    private final IUserService userPassService;

    private final HttpServletRequest httpServletRequest;


    @Autowired
    public CreateUserController(IUserService userPassService, HttpServletRequest httpServletRequest) {
        this.userPassService = userPassService;
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * <h2>创建用户</h2>
     * @param user
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/createuser")
    Response createUser(@RequestBody User user)throws Exception{
        LogGenerator.getLog(
                httpServletRequest,
                -1L,
                LogConstants.ActionName.CREATE_USER,
                null
        );
        return userPassService.createUser(user);
    }
}
