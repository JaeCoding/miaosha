package com.imooc.miaosha.validator;

import com.imooc.miaosha.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * IsMobileValidator 验证器  继承 ConstraintValidator
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;


    /**
     * 验证器实现类  初始化方法， 验证功能 通过ValidatorUtil工具类实现验证， 返回true or false
     * 为什么不直接在isValid里写而是要新建ValidatorUtil工具类？
     * 因为ValidatorUtil工具类专门用于实现验证方法，里面可以有各种验证函数
     * @param isMobile  接收 注释对象 作为参数，初始化变量 required 与注释变量相同（default true）
     */
    @Override
    public void initialize(IsMobile isMobile) {
        required = isMobile.required();//默认返回为true
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) { //后面这个环境参数意义不明
        if(required) {
            return ValidatorUtil.isMobile(value);
        }else {
            if(StringUtils.isEmpty(value)) {
                return true;
            }else {
                return ValidatorUtil.isMobile(value);
            }
        }
    }

}