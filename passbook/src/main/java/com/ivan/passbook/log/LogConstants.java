package com.ivan.passbook.log;

/**
 * <h1> 日志记录常量定义 </h1>
 * @Author Ivan 15:36
 * @Description TODO
 */
public class LogConstants {

    public class ActionName{

        /** 用户查看优惠券信息 */
        public static final  String USER_PASS_INFO = "UserPassInfo";

        /** 用户查看已使用的优惠券信息 */
        public static final String USER_USED_PASS_INFO = "UserUsedPassInfo";

        /** 用户使用优惠券 */
        public static final String USER_USE_PASS = "UserUsePass";

        /** 用户获取库存信息 */
        public static final String INVENTORY_INFO = "InventoryInfo";

        /** 用户领取优惠券 */
        public static final String GAIN_PASS_TEMPALTE = "GainPassTemplate";

        /** 用户创建评论 */
        public static final String CREATE_FEEDBACK = "CreateFeedback";

        /** 用户获取评论 */
        public static final String GET_FEEDBACK = "GetFeedback";

        public static final String CREATE_USER = "CreateUser";


    }
}
