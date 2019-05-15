package com.ivan.passbook.vo;

import com.google.common.base.Enums;
import com.ivan.passbook.constant.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>用户评论表</h1>
 * @Author Ivan 16:08
 * @Description TODO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    /** 用户 id */
    private Long userId;

    /** 评论类型 */
    private String type;

    /** PassTemplate RowKey, 如果是 app 类型的评论, 则没有 */
    private String templateId;

    /** 评论内容 */
    private String comment;

    public boolean validate() {

        //若type不是FeedbackType中的两种类型，则会返回null
        FeedbackType feedbackType = Enums.getIfPresent(
                FeedbackType.class, this.type.toUpperCase()
        ).orNull();

        //校验规则是：要求 type 和 comment 均不为空
        return !(null == feedbackType || null == comment);
    }
}
