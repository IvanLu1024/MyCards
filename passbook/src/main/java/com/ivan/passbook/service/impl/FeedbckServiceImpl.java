package com.ivan.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.ivan.passbook.constant.Constants;
import com.ivan.passbook.mapper.FeedbackRowMapper;
import com.ivan.passbook.service.IFeedbackService;
import com.ivan.passbook.utils.RowKeyGenUtil;
import com.ivan.passbook.vo.Feedback;
import com.ivan.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <h1>评论功能实现</h1>
 * @Author Ivan 14:18
 * @Description TODO
 */
@Slf4j
@Service
public class FeedbckServiceImpl implements IFeedbackService {

    /** HBase 客户端 */
    private final HbaseTemplate hbaseTemplate;

    @Autowired
    public FeedbckServiceImpl(HbaseTemplate hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    @Override
    public Response createFeedback(Feedback feedback) {

        //校验
        if (!feedback.validate()){
            log.error("Feedback Error:{}", JSON.toJSONString(feedback));
            return Response.failure("Feedback Error");
        }

        //填充内容
        Put put = new Put(Bytes.toBytes(RowKeyGenUtil.genFeedBackRowKey(feedback)));

        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.USER_ID),
                Bytes.toBytes(feedback.getUserId())
        );

        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.TYPE),
                Bytes.toBytes(feedback.getType())
        );
        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.TEMPLATE_ID),
                Bytes.toBytes(feedback.getTemplateId())
        );
        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.COMMENT),
                Bytes.toBytes(feedback.getComment())
        );
        hbaseTemplate.saveOrUpdate(Constants.Feedback.TABLE_NAME,put);

        return Response.success();
    }

    @Override
    public Response getFeedback(Long userId) {

        byte[] reverseUserId = new StringBuilder(String.valueOf(userId)).reverse().toString().getBytes();
        Scan scan = new Scan();
        scan.setFilter(new PrefixFilter(reverseUserId));

        List<Feedback> feedbacks = hbaseTemplate.find(Constants.Feedback.TABLE_NAME,scan,new FeedbackRowMapper());

        return new  Response(feedbacks);
    }
}
