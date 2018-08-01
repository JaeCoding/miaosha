package com.imooc.miaosha.domain;


import lombok.Data;


@Data
public class OrderInfo {

  private long id;
  private long userId;
  private long goodsId;
  private long deliveryAddrId;
  private String goodsName;
  private long goodsCount;
  private double goodsPrice;
  private long orderChannel;
  private long status;
  private java.sql.Timestamp createDate;
  private java.sql.Timestamp payDate;

}
