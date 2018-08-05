---
title: 秒杀项目学习笔记 第一、二章——项目框架搭建 实现登陆功能
date: 2018-07-19 12:13:56
tags: [高并发,秒杀项目,安全]
categories: 秒杀项目

---

详细地址见个人博客：jaecoding.github.io

主要秒杀流程
![此处输入图片的描述][1]
[1]: http://pbw0qqogs.bkt.clouddn.com/%E7%A7%92%E6%9D%80%E6%A8%A1%E5%9D%97%E6%B5%81%E7%A8%8B.jpg


# 秒杀项目学习笔记 第一、二章——项目框架搭建 实现登陆功能
redis有多个库，最多16个，默认为0库

# 第一章：
集成Redis：
1.添加Jedis依赖：
2.添加Fastjson：为了序列化，对象与字符串（json格式）的转化

# 第二章(实现登陆功能）：

1.数据库设计
2.明文密码两次MD5处理
3.JSR303参数检验+全局异常处理器
4.分布式session(重要）

## 两次MD5（安全）
http是明文传输，用户密码会在网络上传输
1.用户端： PASS = MD5 (明文+固定Salt）
    用户端先MD5后再传输给服务端，防止传输窃取
2.服务端:  PASS = MD5 (用户输入+ 随机Salt）
    接收后，会随机生成salt，与用户md5生成拼装，再做MD5, 结果再写入数据库，放置数据库被盗。防止彩虹表，由一次的MD5反查出密码，所以要再进行一次MD5。
    
## 2-2 实现登陆功能
新建了一个LoginVo类
作用：用于在console中输入后台所接收到的mobile和password。
实现：在loginController中引入变量log，使用`log.info(loginVo.toString())`输出，loginVo就是前端传来的参数
前端:
```jquery
<script>
function login(){
	$("#loginForm").validate({
        submitHandler:function(form){
             doLogin();
        }    
    });
}

function doLogin(){
	g_showLoading();//展示loading框
	
	var inputPass = $("#password").val();
	var salt = g_passsword_salt; //在common.js中提供
	var str = ""+salt.charAt(0)+salt.charAt(2) + inputPass +salt.charAt(5) + salt.charAt(4);
	var password = md5(str);  //md5.js提供
	
	$.ajax({
		url: "/login/do_login",
	    type: "POST",
	    data:{
	    	mobile:$("#mobile").val(),
	    	password: password
	    },
	    success:function(data){
	    	layer.closeAll();   //不管成功失败，先关框
            console.log(data);
	    	if(data.code == 0){
	    		layer.msg("成功");
	    		window.location.href="/goods/to_list";
	    	}else{
	    		layer.msg(data.msg);
	    	}
	    },
	    error:function(){
	    	layer.closeAll();
	    }
	});
}
</script>
```
```Java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
private static Logger log = LoggerFactory.getLogger(LoginController.class);//导入slf4j的Logger
```
## 2-3 JSR303参数校验 + 全局异常处理器

在登陆的时候，传参的时候需要检验。若每个都写在loginController里的话，很麻烦

参数校验
1.引入spring-boot-starter-validation依赖
2.给前端传来的参数LoginVo加上@Valid注解
3.给参数LoginVo对象类，所需要验证的属性（如电话，密码）加上校验注解，比如NOTNULL，也可以自己创建符合的注解，比如手机号是1开头，共11位。
4.若要新建注解，应在validator文件夹中新建@注解，并传入所对应的验证类。如IsMobileValidator，`IsMobileValidator implements ConstraintValidator`，重写初始化和验证方法。
5.


异常拦截处理：
问题：当加上参数校验时，若未通过校验，会返回给浏览器400异常，但是并不会显示，添加异常处理显示，这样对用户更加友好
目的：拦截绑定异常，输出错误信息

**结构：**

**Controller类**：负责业务的转发，接收传来的@Valid LoginVo(mobile password已装载)

**Service类**：负责业务逻辑，包含业务上的校验（手机是否存在，密码是否正确）。校验成功返回true，失败则new GlobalException（CodeMsg）对应异常并抛出

**GlobalException类**：根据CodeMsg构造，具有CodeMsg属性

**GlobalExceptionHandler类**： 类名前添加注解@ControllerAdvice，类似切面功能，有exceptionHandler方法，能够捕获异常，根据异常类别，返回不同的Result.error(ex.getCodeMsg())


**@Valid**：负责入参的格式校验，表明LoginVo(mobile password)受校验，可自定义添加注解校验
IsMobileValidator类：用于实现注解@IsMobile（用于验证手机号）的验证，里面可能会使用到工具类ValidatorUtil来校验。

**ValidatorUti类**：提供了多种验证方法

## 2-6 分布式Session

分布式多台服务器，处理用户的Session，

可选方法：1.Session同步（应用很少，因为多服务器同步实现复杂）

**所用方法：**
1.使用工具类UUID，修改并生成不带“-”的cookie字符串`String token = UUIDUtil.uuid();`

2.将token保存在redis缓存中，以便于下次验证
`redisService.set(MiaoshaUserKey.token, token, user);` 前缀，key，value

3.将cookie对象加入response，发送回给用户，以便用户下次发送给客户端
```Java
Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token); //作为name和value
cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
cookie.setPath("/");
response.addCookie(cookie);//加入response,
```
4.验证Session如何实现？？
登陆成功后，在login.html中会有ajax异步`window.location.href="/goods/to_list";`跳转到商品列表,访问/goods/to_list，客户端会将session放在request中发送

```Java
    /**
     *
     * @param model
     * @param cookieToken COOKI_NAME_TOKEN是从request中所取的参数名字
     * @param paramToken 有时候手机客户端会将token放在参数中传递，而不是cookie中发给客户端，为了兼容加上这个注解,并且优先取paramToken
     * @return
     */
    @RequestMapping("/to_list")
    public String toLogin(Model model,
                          @CookieValue(value = MiaoshaUserService.COOKI_NAME_TOKEN,required = false) String cookieToken,
                          @RequestParam(value = MiaoshaUserService.COOKI_NAME_TOKEN,required = false) String paramToken
                          ){
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return "login";
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;//优先取paramToken，为空才取cookieToken
        //根据token从redis中获取用户信息
        MiaoshaUser user = userService.getByToken(token);
        model.addAttribute("user", user);
        return "goods_list";
    }

}
```
5. 最后goods.html通过thymeleaf:`<p th:text="'hello:'+${user.nickname}" ></p>`

6. 实现**Session的更新功能**，根据用户最后一次点击时间为起点，在to_list中调用getByToken获取user对象时，若取到了用户，就会重新`addCookie(response, token, user)`

## 分布式Session的优化
在很多的界面跳转时都要验证Session，若在每个方法内都加注解判断token，每个方法都增加根据token获取user的话很冗杂。想到可以将方法抽离出来也需要有没有实现Session更优雅的方式？
```Java
    @RequestMapping("/to_list")
    public String list(HttpServletResponse response, Model model,
                          @CookieValue(value = MiaoshaUserService.COOKI_NAME_TOKEN,required = false) String cookieToken,
                          @RequestParam(value = MiaoshaUserService.COOKI_NAME_TOKEN,required = false) String paramToken
                          ){
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return "login";
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;//优先取paramToken，为空才取cookieToken
        //根据token从redis中获取用户信息
        MiaoshaUser user = userService.getByToken(response,token);

        model.addAttribute("user", user);

        return "goods_list";
    }
```
能不能变成如下方式？直接就获取到了user，不用根据Token来判断了，需要实现argument resolvor参数处理，mvc框架提供了
```Java
    @RequestMapping("/to_list")
    public String list(Model model,  MiaoshaUser user){
        model.addAttribute("user", user);
        return "goods_list";
    }
```
这里我们联想到了添加参数model，request，response实现的原理————argumentResolver，  通过WebMvcConfigurerAdapter（WebMVC配置适配器）
实现：
1.新建WebConfig继承WebMvcConfigurerAdapter，加上@Configuration
```Java
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{

    @Autowired
    UserArgumentResolver userArgumentResolver;//这是为了添加user实现的resolver

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);//将其加入argumentResolver列表

    }
}
```
2.新建UserArgumentResolver 实现 HandlerMethodArgumentResolver
```Java
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MiaoshaUserService userService;

    //判断是否是要引入的对应类
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == MiaoshaUser.class;
    }
    
    //用于根据各种参数，返回所引入得对象。（就跟引入model一样啦）
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
    
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);

        String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);//参数中的根据名字就有
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKI_NAME_TOKEN);//取放在cookies中的cookie，只取cookie名字对上的
        
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }

        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        //根据token从redis中获取用户信息
        return userService.getByToken(response,token);
    }


    private String getCookieValue(HttpServletRequest request, String cookiNameToken) {
        //疑问：request.getCookies()会有很多个cookies吗？只取名为MiaoshaUserService.COOKI_NAME_TOKEN的
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookiNameToken)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
```
![此处输入图片的描述][1]
  [1]: http://pbw0qqogs.bkt.clouddn.com/cookie_token.png
  
  这样，我们的GoodsController就变得异常简洁了。能够直接自动的取user，取不到会直接返回个null的user。
  
```Java
    @RequestMapping("/to_list")
    public String list(Model model,  MiaoshaUser user){
        model.addAttribute("user", user);
        return "goods_list";
    }
```

# 秒杀项目学习笔记 第三章(秒杀功能开发及管理后台）
# 3-1 商品列表页的设计
1.表设计：分为商品表，订单表，秒杀商品表，秒杀订单表
分开是为了易于维护。

![此处输入图片的描述][1]

2.使用IDEA的数据表生成pojo功能：datebase→连接→Scripted Extensions→generatePojo

3.goodsService的会调用goodsDao.listGoodsVo方法取出来的是GoodsVo而不是Goods，GoodsVo内的属性是从数据库多表中联立取出。

4.取出的list加入到model中，在html中通过thymleaf循环取出

# 3-3 商品详情页的设计

彩蛋：数据库ID一般不用自增，容易被他人遍历，而用snowflake算法

前端部分：
```html
<span th:if="${user eq null}"> 您还没有登录，请登陆后再操作<br/></span>
```
```html
     <tr>  
        <td>秒杀开始时间</td>  
        <td th:text="${#dates.format(goods.startDate, 'yyyy-MM-dd HH:mm:ss')}"></td>
        <td id="miaoshaTip">
        <!-- 隐藏域临时保存${remainSeconds}，在进行中和已结束也有remainSeconds，只是不显示-->
        	<input type="hidden" id="remainSeconds" th:value="${remainSeconds}" />
        	<span th:if="${miaoshaStatus eq 0}">秒杀倒计时：<span id="countDown" th:text="${remainSeconds}"></span>秒</span>//倒计时的设计
        	<span th:if="${miaoshaStatus eq 1}">秒杀进行中</span>
        	<span th:if="${miaoshaStatus eq 2}">秒杀已结束</span>
        </td>
        <td>
        	<form id="miaoshaForm" method="post" action="/miaosha/do_miaosha">
        		<button class="btn btn-primary btn-block" type="submit" id="buyButton">立即秒杀</button>
        		<input type="hidden" name="goodsId" th:value="${goods.id}" />
        	</form>
        </td>
     </tr>
```
秒杀倒计时的设计：
```html
<span th:if="${miaoshaStatus eq 0}">秒杀倒计时：<span id="countDown" th:text="${remainSeconds}"></span>秒</span>//倒计时的设计
```
```Jquery
<script>
$(function(){
	countDown();
});

function countDown(){
	
	var remainSeconds = $("#remainSeconds").val();
    var timeout;
    
	if(remainSeconds > 0){//秒杀还没开始，倒计时
		$("#buyButton").attr("disabled", true);

		timeout = setTimeout(function(){
			$("#countDown").text(remainSeconds - 1);
			$("#remainSeconds").val(remainSeconds - 1);
			countDown();//回调函数，回调自己
		},1000);
	}else if(remainSeconds == 0){//秒杀进行中
		$("#buyButton").attr("disabled", false);
		if(timeout){
			clearTimeout(timeout);
		}
		$("#miaoshaTip").html("秒杀进行中");
	}else{//秒杀已经结束
		$("#buyButton").attr("disabled", true);
		$("#miaoshaTip").html("秒杀已经结束");
	}
}
</script>
```
## controller类里接收参数@PathVariable和@RequestParam的区别
1.@PathVariable 路径变量，是用来获得请求url中的动态参数的，用于**将请求URL中的模板变量**映射到功能处理方法的参数上。
```@PathVariable：<a th:href="'/goods/to_detail/'+${goods.id}">//变量在url中```

然后
```Java
    @RequestMapping("/to_detail/{goodsId}")
    public String detail(Model model, MiaoshaUser user,
                         @PathVariable("goodsId") long goodsId){
```

             
2.@RequestParam ：请求参数  用于接收request发来的参数

在SpringMVC后台控制层获取参数的方式主要有两种:
一种是request.getParameter("name")，另外一种是用注解@RequestParam直接获取
```Java
    @RequestMapping("/do_miaosha")
    public String list(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId){
```


接下来我们看一下@RequestParam注解主要有哪些**参数**：
**value：**参数名字，即入参的请求参数名字，如username表示请求的参数区中的名字为username的参数的值将传入；
**required：**是否必须，默认是true，表示请求中一定要有相应的参数，否则将报404错误码；
**defaultValue：**默认值，表示如果请求中没有同名参数时的默认值，例如：
```Java
public List<EasyUITreeNode> getItemTreeNode(@RequestParam(value="id",defaultValue="0")long parentId)
```

# 3-4 秒杀方法的实现  秒杀需要user，和gooodsId
1.点击秒杀，传入
```
<form id="miaoshaForm" method="post" action="/miaosha/do_miaosha">
    <button class="btn btn-primary btn-block" type="submit" id="buyButton">立即秒杀</button><!-- 只有在进行中才可以点击-->
    <input type="hidden" name="goodsId" th:value="${goods.id}" />
</form>
```
2.MiaoshaController只需要两个

 - 判断user是否为空，为空跳转到login
 - 判断商品是否还有库存，user是否已经秒杀过（这个用户是否有order）。是的话跳转到miaosha_fail
 - 调用miaoshaService。`OrderInfo orderInfo = miaoshaService.miaosha(user, goods);`

3.`MiaoshaService `
```Java
    @Transactional//事务
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {

        //减库存根据逻辑应该在goodsService中完成而不是在miaoshaService里完成
        goodsService.reduceStock(goods);

        //新建订单,返回一个orderInfo对象
        return orderService.createOrder(user, goods);
    }
```
**3.1 `goodsService`减库存操作**
```Java
goodsService.reduceStock(goods.getId());//使用了ID而没有像教程一样传入GoodsVo,会根据goodsId去减少Miaosha_goods中对应的库存
```
**3.2 `orderService`新建订单操作**
orderService中。先创建一个OrderInfo对象，然后根据GoodsVogoods对其设置其属性值。
```Java
//生成订单后插入orderInfo到order表里
long orderId = orderDao.insert(orderInfo);
```
再创建miaoshaOrder并设置其属性值，这里用到了生成的orderId，然后
```Java
//生成订单后插入miaoshaOrder到miaosha_order表里
orderDao.insertMiaoshaOrder(miaoshaOrder);
return orderInfo;
```
最后orderService返回订单，并MiaoshaService返回订单，MiaoshaController添加对象到视图中，跳转到order_detail显示属性。
```Java
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);

        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
```


# 3-5 订单详情页
很简单。在order_detail中取出显示出来即可。
```Java
     <tr>
     	<td>订单状态</td>  
        <td >
        	<span th:if="${orderInfo.status eq 0}">未支付</span>
        	<span th:if="${orderInfo.status eq 1}">待发货</span>
        	<span th:if="${orderInfo.status eq 2}">已发货</span>
        	<span th:if="${orderInfo.status eq 3}">已收货</span>
        	<span th:if="${orderInfo.status eq 4}">已退款</span>
        	<span th:if="${orderInfo.status eq 5}">已完成</span>
        </td>  
        <td>
        	<button class="btn btn-primary btn-block" type="submit" id="payButton">立即支付</button>
        </td>
     </tr>
```
  [1]: http://pbw0qqogs.bkt.clouddn.com/%E7%A7%92%E6%9D%80%E6%A8%A1%E5%9D%97%E6%B5%81%E7%A8%8B.jpg

# 秒杀项目学习笔记 第五章————页面优化技术

**核心思路：减少对数据库的访问**


# 5-1 页面缓存

特点：有效期往往比较短
1.取缓存，html存放在缓存中，可取出则
2.不可则 手动渲染模板  ，并且存放在redis中
3.结果输出

以**goods_list**为例，在Controller中，添加注解，直接返回html
```Java
       //取缓存,取到返回。html是长文本，一堆
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        //取不到，手动渲染并且加入到redis中，ThemleafViewResolver,
        SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap(), applicationContext);//配置一下环境，下面会用到，配起来就是根据接口， 缺啥补啥
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);//选择模板（页面），进行渲染（成字符串）
        if (StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
```
时间一般较短，显示前两页

# 5-2 对象缓存（重点：更新对象缓存！！）

**获取对象操作**
比如user对象的缓存，对于对象，设置过期时间为永不过期。
1.取对象缓存
2.取为null则从数据库中取，并且写入redis缓存。
**更新密码操作。**
1.取对象（以上）
2.更新对象密码，update写入数据库，
3.更新缓存：删除原缓存，`user.setPassword`，user写入缓存

不能去调用其他类的DAO，只能调用其他类的Service，
Jmeter查询发现mysql的内存占用还是很高。

# 5-4 商品详情静态化、也就是前后端分离
常用技术AngularJs  Vue.js
优点：利用浏览器的缓存

1.将goods_detail.htm放在resoucrse的static中，
2.将静态页面放在static中，后缀为htm，因为在application中配置了会去resources中寻找.html后缀
` <td><a th:href="'/goods_detail.htm?goodsId='+${goods.id}">详情</a></td>  `
3.在客户端写入异步获取请求。
```Jquery
$(function(){
	//countDown();
	getDetail();
});

function getDetail(){
	var goodsId = g_getQueryString("goodsId");
	$.ajax({ //异步访问客户端，获取参数
		url:"/goods/detail/"+goodsId,
		type:"GET",
		success:function(data){
			if(data.code == 0){
				render(data.data);
			}else{
				layer.msg(data.msg);
			}
		},
		error:function(){
			layer.msg("客户端请求有误");
		}
	});
}
```
# 5-5 秒杀静态化 

1.改造后端，
不是 不停地`model.addAttribute("orderInfo", orderInfo);`
最后再返回"order_detail"客户端跳转，获取model熟悉

而是返回一个`Result.success(orderInfo)`表单，从客户端调用ajax跳转。然后取客户端传来的参数。

2.改造前端，前端goods_detail.htm的秒杀按钮，对应函数如下
```Jquery
function doMiaosha(){

    //点击了秒杀按钮后， ajax异步调用 发送表单请求（传goodsId），得到结果，success则跳转到订单详情，失败则，error
	$.ajax({
		url:"/miaosha/do_miaosha",
		type:"POST",
		data:{
			goodsId:$("#goodsId").val(),
		},
		success:function(data){ //这里的data就是do_miaosha返回的Result了
			if(data.code == 0){
				window.location.href="/order_detail.htm?orderId="+data.data.id;//秒杀成功，跳转到订单详情//data.data就是vo
			}else{
				layer.msg(data.msg);
			}
		},
		error:function(){
			layer.msg("客户端请求有误");
		}
	});
}
```
秒杀返回的状态码是304，表示服务端表示你本地的页面没有变化，页面不用下载了，但是客户端与服务端还是有交互。

## 利用SpringBoot的 SPRING RESOURCES HANDLING 
参考文档：https://docs.spring.io/spring-boot/docs/2.1.0.BUILD-SNAPSHOT/reference/htmlsingle/

然后在浏览器访问一次后，后台就会判断，不会发生交互。直接返回200状态码。


# 5-6 订单详情静态化 解决超卖

生成的订单放到redis缓存中，秒杀判断是否已经生成订单的时候，可以不用去数据库查询了，直接从缓存中查询





# 超卖问题

秒杀逻辑： 1.先判断库存 >0 2.再判断是否已经秒杀过，也就是是否存在秒杀订单。
3.再减库存下订单。（这里再判断一下库存？？）

**问题：同一用户买多个**
同一个用户两个请求，判断有库存，判断都没有秒杀，然后下了两个订单。
**解决：更新加判断，并且秒杀订单表加唯一索引报错，秒杀事务回滚**
**优化1：**给更新库存，减库存的时候加了一个判断。只在stock_count>0的情况下减库存。
**优化2：**:对其数据表建立唯一索引，第二个订单就插不进表，从而报错，秒杀请求事务回滚。


# 5-7 静态资源优化

1.JS/CSS压缩， 减少流量（去掉空格之类）
2.多个JS/CSS组合，减少连接数
一般并发3 4个链接 从服务端获取资源，提高页面加载速度

淘宝：Tengine 
http://tengine.taobao.org/
http://tengine.taobao.org/document_cn/http_concat_cn.html

webpack：打包前端文件

3.CDN就近访问
CDN：内容分发网络，把数据缓存到全网节点上，根据用户请求，分发到离用户最近（最快）的位置上。

瓶颈：数据库，所以要削减请求到数据库的数量



## 并发解决流程
用户开始：

 1. 浏览器页面静态化，缓存到浏览器端。
 2. 部署CDN节点，请求首先访问到就近CDN缓存，
 3. ngiinx缓存
 4. redis应用程序页面缓存 --对象缓存 
 5. 数据库
 # 秒杀项目学习笔记 第七章————安全优化
1.秒杀接口地址隐藏
2.数学公式验证码（防止机器人，延迟请求分散并发量）
3.接口限流防刷（限制访问次数）


# 7-1 秒杀接口地址隐藏
思路：秒杀开始之间，先去请求接口  获取秒杀地址

1.接口改造，带上PathVariable参数
秒杀按钮不是秒杀，而是先异步请求`"/miaosha/path"`，生成path写入缓存，并且返回path

2.拿到path之后返回，异步请求miaosha，传入path，验证是否合法


3.秒杀收到请求，先验证PathVariable


# 7-2 数学公式验证码
思路：防止机器人，分散用户的请求

1.添加生成验证码的接口
前端：添加 验证码图片展示模块，结果输入模块。设置好显示与隐藏逻辑。
在进入goods_ detail时候会异步访问，请求验证，得到返回的验证码渲染展示出来。

后端:


2.在获取秒杀路径的时候，验证验证码
3.SriptEngine使用（JDK1.6 添加）


# 7-4 接口限流防刷

一般解决方法，加入一个访问次数缓存。每次访问一次+1，超过5次返回错误。

# 7-5 通用化的 接口限流防刷

通过 自定义拦截器 来限制流量。
这样在所需限制的方法上添加。`@AccessLimit(seconds=5, maxCount=5, needLogin=true)`

1.创建注解@AccessLimit，内部添加 所需的限制对象。
2.`AccessInterceptor  extends HandlerInterceptorAdapter`。自定义拦截器类，继承适配器类，重写`preHandle`方法。在其中
通过HandlerMethod拿到注解，获得对外接口的限制属性。对这些属性，来做一些redis缓存。
没每访问一次就给缓存加一次


