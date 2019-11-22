package com.liferunner.api.job;

import com.liferunner.mapper.OrderStatusMapper;
import com.liferunner.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * OrderJob for : 订单业务处理定时任务
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/22
 */
@Component
@Slf4j
public class OrderJob {

    @Autowired
    private IOrderService orderService;

    //    @Scheduled(cron = "0 0 0/1 * * ? *")
    @Scheduled(cron = "0/5 * * * * ?")
    public void closeOrder() {
        log.info("Auto close order on : {}", new Date());
        this.orderService.AutoCloseOvertimeOrder();
    }
}
