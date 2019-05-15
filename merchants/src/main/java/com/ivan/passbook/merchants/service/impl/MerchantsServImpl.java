package com.ivan.passbook.merchants.service.impl;

import com.alibaba.fastjson.JSON;
import com.ivan.passbook.merchants.constant.Constants;
import com.ivan.passbook.merchants.constant.ErrorCode;
import com.ivan.passbook.merchants.dao.MerchantsDao;
import com.ivan.passbook.merchants.entity.Merchants;
import com.ivan.passbook.merchants.service.IMerchantsServ;
import com.ivan.passbook.merchants.vo.CreateMerchantsRequest;
import com.ivan.passbook.merchants.vo.CreateMerchantsResponse;
import com.ivan.passbook.merchants.vo.PassTemplate;
import com.ivan.passbook.merchants.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h1>商户服务接口实现</h1>
 *
 * @Author Ivan 14:47
 * @Description TODO
 */
@Slf4j
@Service
public class MerchantsServImpl implements IMerchantsServ {

    /** Merchants 数据库接口 */
    private final MerchantsDao merchantsDao;

    /** kafka 客户端 */
    private final KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    public MerchantsServImpl(MerchantsDao merchantsDao,KafkaTemplate<String,String> kafkaTemplate){
        this.merchantsDao=merchantsDao;
        this.kafkaTemplate=kafkaTemplate;
    }



    @Override
    @Transactional
    public Response createMerchants(CreateMerchantsRequest request) {
        Response response = new Response();
        CreateMerchantsResponse merchantsResponse = new CreateMerchantsResponse();

        ErrorCode errorCode = request.validate(merchantsDao);
        if (errorCode != ErrorCode.SUCCESS){
            merchantsResponse.setId(-1);
            response.setErrorCode(errorCode.getCode());
            response.setErrorMsg(errorCode.getDesc());
        }else {
            merchantsResponse.setId(merchantsDao.save(request.toMerchants()).getId());
        }
        response.setData(merchantsResponse);

        return response;
    }

    @Override
    public Response buildMerchantsById(Integer id) {
        Response response = new Response();

        Merchants merchants = merchantsDao.findById(id);

        if (null == merchants){
            response.setErrorCode(ErrorCode.MERCHANTS_NOT_EXIST.getCode());
            response.setErrorMsg(ErrorCode.MERCHANTS_NOT_EXIST.getDesc());

        }

        response.setData(merchants);

        return response;
    }

    @Override
    public Response dropPassTemplate(PassTemplate passTemplate) {
        Response response = new Response();

        ErrorCode errorCode = passTemplate.validate(merchantsDao);

        if (errorCode != ErrorCode.SUCCESS){
            response.setErrorCode(errorCode.getCode());
            response.setErrorMsg(errorCode.getDesc());
        }else {
            String passBookTemplate = JSON.toJSONString(passTemplate);
            //将优惠券投放到kafka中
            kafkaTemplate.send(
                        Constants.TEMPLATE_TOPIC,
                        Constants.TEMPLATE_TOPIC,
                        passBookTemplate
                );
                log.info("DropPassTemplate: {}", passBookTemplate);


        }

        return response;
    }
}
