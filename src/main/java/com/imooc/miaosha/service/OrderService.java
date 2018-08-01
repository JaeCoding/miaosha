package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.OrderDao;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.OrderKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    /**
     * 原本是查数据库，现在改成查缓存
     * @param userId
     * @param goodsId
     * @return
     */
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, long goodsId) {
//        return orderDao.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        return  redisService.get(OrderKey.getMiaoshaOrderByUidGid, userId + "" + goodsId, MiaoshaOrder.class);

    }


    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {

        //写普通订单info，哇靠一堆 有没有简便方法啊
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Timestamp(new Date().getTime()));
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderDao.insert(orderInfo);

        //写秒杀订单info，插入miaoshaOrder到miaosha_order表
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);

        //订单写入缓存
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, user.getId() + "" + goods.getId(), MiaoshaOrder.class);

        return orderInfo;



    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
