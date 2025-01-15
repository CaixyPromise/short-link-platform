package com.caixy.shortlink;

import com.caixy.shortlink.model.vo.api.ApiKeyVO;
import com.caixy.shortlink.model.vo.user.UserVO;
import com.caixy.shortlink.service.ApiKeyService;
import com.caixy.shortlink.utils.EncryptionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 测试密码
 *
 * @name: com.caixy.shortlink.TestRegex
 * @author: CAIXYPROMISE
 * @since: 2024-04-26 20:29
 **/
@SpringBootTest
public class TestRegex
{
    @Autowired
    private ApiKeyService apiKeyService;
    @Test
    public void test() throws Exception
    {
        UserVO userVO = new UserVO();
        userVO.setId(1819823136159395842L);
        apiKeyService.refreshApiKeyByUser(userVO, "123");
        ApiKeyVO apiKeyVO = apiKeyService.getUserApiKeyInSystem(userVO);
        System.out.println(apiKeyVO);
    }
}
