package com.caixy.shortlink.aop.validator;

import com.caixy.shortlink.annotation.validator.NotBeforeCurrentTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 不早于当前时间的校验器
 *
 * @Author CAIXYPROMISE
 * @since 2024/11/20 0:45
 */
public class NotBeforeCurrentTimeValidator implements ConstraintValidator<NotBeforeCurrentTime, Object>
{
    private boolean allowNull; // 保存注解参数

    @Override
    public void initialize(NotBeforeCurrentTime constraintAnnotation)
    {
        // 读取注解参数
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context)
    {
        if (value == null)
        {
            return allowNull;
        }

        LocalDateTime now = LocalDateTime.now();

        if (value instanceof LocalDateTime dateTime)
        {
            return !dateTime.isBefore(now);
        }
        else if (value instanceof Date)
        {
            LocalDateTime dateTime = ((Date) value).toInstant()
                                                   .atZone(ZoneId.systemDefault()).toLocalDateTime();
            return !dateTime.isBefore(now);
        }

        throw new IllegalArgumentException("Unsupported type for validation");
    }
}