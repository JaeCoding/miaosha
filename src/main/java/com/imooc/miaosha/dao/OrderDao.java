package com.imooc.miaosha.dao;


import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;


//dao是个接口，而domain是实现，GoodsVo类似domain，这里取出来的不是Goods而是GoodsVo
@Mapper
public interface OrderDao {


    @Select("select * from order_info where user_id = #{userId} and goods_id = #{goodsId}")
    MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId") Long id,@Param("goodsId") long goodsId);


    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    //因为要获取一个返回值,     keyColumn数据库字段，keyProperty BEAN对象属性
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class,before = false, statement = "select last_insert_id()")
    void insert(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(long orderId);
}
