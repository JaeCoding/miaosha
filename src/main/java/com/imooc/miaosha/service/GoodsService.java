package com.imooc.miaosha.service;


import com.imooc.miaosha.dao.GoodsDao;
import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }


    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(long goodsId) {
//        MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
//        miaoshaGoods.setGoodsId(goodsId);

        //减库存为啥要新建一个 MiaoshaGoods,为其设置上GoodsVo的ID
        //能不知直接通过goods_id直接去miaosha_goods里减库存呢？
        //实验后发现可行！！！
        int ret = goodsDao.reduceStock(goodsId);
        return ret > 0;
    }
}
