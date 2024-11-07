package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    /**
     * 处理超时订单
     *
     * @return void
     * @author zhuwanyi
     * @create 2024/11/7
     **/

    @Scheduled(cron ="0 * * * * ? " )//每分钟
    public void task() {
        log.info("处理超时订单,{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList=orderMapper.getByTask(Orders.PENDING_PAYMENT,time);
        if(ordersList.size()>0 && ordersList!=null){
            for (Orders orders : ordersList) {
                orders.setStatus(orders.CANCELLED);
                orders.setCancelReason("订单超时,自动取消!");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }
    /**
     * 处理派送中订单
     *
     * @return void
     * @author zhuwanyi
     * @create 2024/11/7
     **/

    @Scheduled(cron = "0 0 1 * * ? ")//每天凌晨一点
    public void outOfDelivery(){
        log.info("处理派送中订单,{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList=orderMapper.getByTask(Orders.DELIVERY_IN_PROGRESS,time);
        for (Orders orders : ordersList) {
            orders.setStatus(orders.COMPLETED);
            orderMapper.update(orders);
        }
    }
}
