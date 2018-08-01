package com.imooc.miaosha.vo;

import com.imooc.miaosha.domain.Goods;

import java.sql.Timestamp;


/**
 * 为什么不是继承MiaoshaUserDao，这里里面的大部分属性都是MiaoshaUserDao的属性
 * 与goods共有属性有 stockCount
 * 与miaoshaGoods共有属性有 miaoshaPrice startDate endDate
 */
public class GoodsVo extends Goods {


    private Double miaoshaPrice;
    private long stockCount;
    private java.sql.Timestamp startDate;
    private java.sql.Timestamp endDate;


    public Double getMiaoshaPrice() {
        return miaoshaPrice;
    }

    public void setMiaoshaPrice(Double miaoshaPrice) {
        this.miaoshaPrice = miaoshaPrice;
    }

    public long getStockCount() {
        return stockCount;
    }

    public void setStockCount(long stockCount) {
        this.stockCount = stockCount;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }
}
