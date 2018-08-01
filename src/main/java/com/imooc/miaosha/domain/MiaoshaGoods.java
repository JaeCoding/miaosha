package com.imooc.miaosha.domain;


public class MiaoshaGoods {

  private long id;
  private long goodsId;
  private double miaoshaPrice;
  private long stockCount;
  private java.sql.Timestamp startDate;  //用时间戳还是用date呢？？
  private java.sql.Timestamp endDate;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getGoodsId() {
    return goodsId;
  }

  public void setGoodsId(long goodsId) {
    this.goodsId = goodsId;
  }


  public double getMiaoshaPrice() {
    return miaoshaPrice;
  }

  public void setMiaoshaPrice(double miaoshaPrice) {
    this.miaoshaPrice = miaoshaPrice;
  }


  public long getStockCount() {
    return stockCount;
  }

  public void setStockCount(long stockCount) {
    this.stockCount = stockCount;
  }


  public java.sql.Timestamp getStartDate() {
    return startDate;
  }

  public void setStartDate(java.sql.Timestamp startDate) {
    this.startDate = startDate;
  }


  public java.sql.Timestamp getEndDate() {
    return endDate;
  }

  public void setEndDate(java.sql.Timestamp endDate) {
    this.endDate = endDate;
  }

}
