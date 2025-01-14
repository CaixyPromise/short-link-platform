package com.caixy.shortlink.model.dto.linkLocaleStats;

import cn.hutool.http.HttpUtil;
import com.caixy.shortlink.constant.CommonConstant;
import com.caixy.shortlink.utils.JsonUtils;
import com.caixy.shortlink.utils.NetUtils;
import com.google.gson.JsonObject;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 远程请求链接ip信息响应结构
 *
 * @Author CAIXYPROMISE
 * @since 2025/1/13 16:47
 */
@Setter
@ToString
@Builder
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class LinkLocaleStateIpApiResponse
{
    /**
     * 状态码
     */
    @Getter
    private String code;

    /**
     * 区域代码
     */
    @Getter
    private String adcode;

    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;

    /**
     * 国家
     */
    private String country;

    @Getter
    private Boolean isSucceed;

    /**
     * 是否是局域网
     */
    @Getter
    Boolean isInternalIp;

    public String getProvince()
    {
        return (province == null || province.trim().isEmpty()) ? "未知" : province;
    }

    public String getCity()
    {
        return (city == null || city.trim().isEmpty()) ? "未知" : city;
    }

    public String getCountry()
    {
        return (country == null || country.trim().isEmpty()) ? "未知" : country;
    }

    public String getLocation()
    {
        if (!getIsSucceed() || getIsInternalIp())
        {
            return "局域网地址";
        }
        return String.join("-", getCountry(), getProvince(), getCity());
    }

    public static LinkLocaleStateIpApiResponse fetchLocaleInfo(String ip)
    {
        // 判断是否是局域网 IP
        if (NetUtils.isInternalIp(ip))
        {
            return LinkLocaleStateIpApiResponse.builder()
                                               .province("局域网")
                                               .city("局域网")
                                               .country("局域网")
                                               .adcode("未知")
                                               .code("LOCAL")
                                               .isSucceed(true)
                                               .isInternalIp(true)
                                               .build();
        }
        try
        {
            // 远程请求获取 IP 信息
            Map<String, Object> localeParamMap = new HashMap<>();
            localeParamMap.put("ip", ip);
            String localeResultStr = HttpUtil.get(CommonConstant.IP_REMOTE_URL, localeParamMap);
            log.info("远程请求结果: {}", localeResultStr);
            // 解析返回结果
            JsonObject localeJson = JsonUtils.getJsonObject(localeResultStr);
            return LinkLocaleStateIpApiResponse.fromJson(localeJson);
        }
        catch (Exception e)
        {
            log.error("请求或解析地区信息时发生错误: {}", e.getMessage());
            return LinkLocaleStateIpApiResponse.builder()
                                               .province("未知")
                                               .city("未知")
                                               .country("未知")
                                               .adcode("未知")
                                               .code("ERROR")
                                               .isInternalIp(false)
                                               .isSucceed(false)
                                               .build();
        }
    }

    public static LinkLocaleStateIpApiResponse fromJson(JsonObject jsonObject)
    {

        // 初始化目标对象
        LinkLocaleStateIpApiResponse response = new LinkLocaleStateIpApiResponse();

        // 设置返回状态码
        response.setCode(jsonObject.has("code") ? jsonObject.get("code").getAsString() : "未知");

        // 设置 success 状态
        response.setIsSucceed("200".equals(response.getCode())); // 假设 "200" 表示成功

        // 设置 infocode 和其他区域信息
        JsonObject adcodeObject = jsonObject.getAsJsonObject("adcode");
        if (adcodeObject != null)
        {
            response.setAdcode(adcodeObject.has("p") ? adcodeObject.get("p").getAsString() : "未知");
            response.setProvince(adcodeObject.has("p") ? adcodeObject.get("p").getAsString() : "未知");
            response.setCity(adcodeObject.has("c") ? adcodeObject.get("c").getAsString() : "未知");
        }

        // 设置国家信息
        JsonObject ipdataObject = jsonObject.getAsJsonObject("ipdata");
        if (ipdataObject != null)
        {
            response.setCountry(ipdataObject.has("info1") ? ipdataObject.get("info1").getAsString() : "未知");
        }
        response.setIsInternalIp(false);
        return response;
    }
}
