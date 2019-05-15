package com.ivan.passbook.utils;

import com.ivan.passbook.vo.Feedback;
import com.ivan.passbook.vo.GainPassTemplateRequest;
import com.ivan.passbook.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * <h1>Row Key 生成器工具类 </h1>
 * @Author Ivan 16:00
 * @Description TODO
 */
@Slf4j
public class RowKeyGenUtil {

    /**
     * <h2>根据提供的PassTemplate 对象生成 RowKey </h2>
     * @param passTemplate {@link PassTemplate}
     * @return String RowKey
     */
    public static  String genPassTemplateRowKey(PassTemplate passTemplate){

        //拼接id和title形成一个优惠券的唯一值
        String passInfo = String.valueOf(passTemplate.getId()+"_"+passTemplate.getTitle());
        //rowKey使用md5的目的是使得rowKey的分布尽量分散，充分利用HBase集群的负载均衡
        String rowKey = DigestUtils.md5Hex(passInfo);
        log.info("GenPassTemplateRowKey:{},{}",passInfo,rowKey);

        return rowKey;

    }

    /**
     * <h2>根据提供的领取优惠券请求生成 RowKey，只可以在领取优惠券的时候使用 </h2>
     * Pass RowKey = reversed(userId) + inverse(timestamp) + passTemplate RowKey
     * @param request
     * @return
     */
    public static String genPassRowKey(GainPassTemplateRequest request){

        return new StringBuilder(String.valueOf(request.getUserId())).reverse().toString()
                + (Long.MAX_VALUE-System.currentTimeMillis())
                + genPassTemplateRowKey(request.getPassTemplate());

    }

    /**
     * <h2>根据 Feedback 构造 RowKey </h2>
     * @param feedback {@link Feedback}
     * @return String RowKey
     */
    public static String genFeedBackRowKey(Feedback feedback){

        //由于userId的后缀是随机的，这样生成的RowKey更加具有随机性
        // 并且最近时间的记录是靠前的
        return new StringBuilder(String.valueOf(feedback.getUserId())).reverse().toString()+
                (Long.MAX_VALUE-System.currentTimeMillis());



    }
}
