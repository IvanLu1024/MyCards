package com.ivan.passbook.merchants.controller;

import com.alibaba.fastjson.JSON;
import com.ivan.passbook.merchants.service.IMerchantsServ;
import com.ivan.passbook.merchants.vo.CreateMerchantsRequest;
import com.ivan.passbook.merchants.vo.PassTemplate;
import com.ivan.passbook.merchants.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Ivan 20:00
 * @Description TODO
 */
@Slf4j
@RestController
@RequestMapping("/merchants")
public class MerchantsController {

    @Autowired
    private IMerchantsServ merchantsServ;

    public MerchantsController(IMerchantsServ merchantsServ) {
        this.merchantsServ=merchantsServ;
    }

    @ResponseBody
    @PostMapping("/create")
    public Response createMerchants(@RequestBody CreateMerchantsRequest request){
        log.info("CreateMerchants : {}", JSON.toJSONString(request));
        return merchantsServ.createMerchants(request);
    }

    @ResponseBody
    @GetMapping("/{id}")
    public Response buildMerchantsById(@PathVariable Integer id){
        log.info("BuildMerchants :{}",id);
        return merchantsServ.buildMerchantsById(id);
    }

    @ResponseBody
    @PostMapping("/drop")
    public Response dropPassTemplate(@RequestBody PassTemplate template){
        log.info("DropPassTemplate : {}",template);
        return merchantsServ.dropPassTemplate(template);
    }
}
