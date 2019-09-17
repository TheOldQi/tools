package com.xiafei.tools.common;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * <P>Description: javax.validation包内的jsr303注解使用工具类. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/10/8</P>
 * <P>UPDATE DATE: 2018/10/8</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class ValidateUtils {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 验证
     *
     * @param object 校验对象
     * @param groups 校验规则分组
     * @param <T>    分组类泛型
     * @throws IllegalArgumentException 参数校验失败,内容是失败原因
     */
    public static <T> void validate(final T object, final Class<?>... groups) throws IllegalArgumentException {
        final Set<ConstraintViolation<T>> errors = validator.validate(object, groups);
        if (errors.isEmpty()) {
            return;
        }
        final StringBuilder errorMsg = new StringBuilder();
        for (ConstraintViolation constraintViolation : errors) {
            String message = constraintViolation.getMessage();
            message = (message == null || message.equals("")) ? "参数非法" : constraintViolation.getMessage();
            final Path propertyPath = constraintViolation.getPropertyPath();
            errorMsg.append(propertyPath).append(message).append(";");
        }
        errorMsg.deleteCharAt(errorMsg.length() - 1);
        throw new IllegalArgumentException(errorMsg.toString());
    }

    public static void main(String[] args) {
        validate(new Object());
    }
}
