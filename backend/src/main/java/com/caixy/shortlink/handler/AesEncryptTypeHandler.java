package com.caixy.shortlink.handler;

import com.caixy.shortlink.utils.AesUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AES数据库加密处理器
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/14 19:05
 */
public class AesEncryptTypeHandler extends BaseTypeHandler<String>
{
    private static final Logger log = LoggerFactory.getLogger(AesEncryptTypeHandler.class);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws
                                                                                                      SQLException
    {
        ps.setString(i, encrypt(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException
    {
        String encrypted = rs.getString(columnName);
        return encrypted == null ? null : decrypt(encrypted);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException
    {
        String encrypted = rs.getString(columnIndex);
        return encrypted == null ? null : decrypt(encrypted);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException
    {
        String encrypted = cs.getString(columnIndex);
        return encrypted == null ? null : decrypt(encrypted);
    }

    /**
     * 加密方法
     *
     * @param data
     * @return
     */
    private String encrypt(String data)
    {
        log.info("AES加密前数据: {}", data);
        return AesUtils.encrypt(data).combineIvAndCipherText();
    }

    /**
     * 解密方法
     *
     * @param data
     * @return
     */
    private String decrypt(String data)
    {
        log.info("AES解密前数据: {}", data);
        return AesUtils.decrypt(AesUtils.CipherResult.splitIvAndCipherText(data));
    }
}
