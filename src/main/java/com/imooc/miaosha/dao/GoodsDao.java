package com.imooc.miaosha.dao;


import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


//dao是个接口，而domain是实现，GoodsVo类似domain，这里取出来的不是Goods而是GoodsVo
@Mapper
public interface GoodsDao {

    @Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();//希望同时查出商品信息和秒杀信息,所以用goodsVo合并下

    //从goods和miaosha_goods中查出四个属性注入到GoodsVo中，为什么能完美注入呢？
    @Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id " +
            "where g.id = #{goodsId}")//用######
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);


    /**
     * @PathVariable("goodsId")  不用加到吗MiaoshaGoods g 前面吗？？不用，如果是g.id = #{goodsId} 才要
     * 若采用注释掉的方法， 传入的是个MiaoshaGoods，查询依靠的是goodsId，能自动获取吗？  应该是的了。。。。
     */
    @Update("update miaosha_goods set stock_count = stock_count -1 " +
            "where goods_id = #{goodsId} and stock_count > 0")//会有加锁操作
    int reduceStock(long goodsId);
//   void reduceStock(MiaoshaGoods g);

}
