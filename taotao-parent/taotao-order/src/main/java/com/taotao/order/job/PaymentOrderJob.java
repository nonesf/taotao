package com.taotao.order.job;

import com.taotao.order.mapper.OrderMapper;
import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 扫描超过两天未付款的订单关闭
 */
public class PaymentOrderJob  extends QuartzJobBean {


    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        ApplicationContext applicationContext = (ApplicationContext) context.getJobDetail().getJobDataMap()
                .get("applicationContext");
        //时间参数，当前时间向前推两天
        applicationContext.getBean(OrderMapper.class).paymentOrderScan(new DateTime().minusDays(2).toDate());
    }
}
