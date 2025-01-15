package com.caixy.shortlink;

import com.caixy.shortlink.model.dto.link.LinkAddRequest;
import com.caixy.shortlink.model.enums.ShortLinkCreateType;
import com.caixy.shortlink.model.vo.link.LinkCreateVO;
import com.caixy.shortlink.service.LinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @Author CAIXYPROMISE
 * @since 2024/11/20 1:50
 */
@SpringBootTest
public class LinkServiceTest
{
    @Autowired
    private LinkService linkService;

    @Test
    public void testCreateShortLink()
    {
        LinkAddRequest linkAddRequest = new LinkAddRequest();
        linkAddRequest.setOriginUrl("https://www.baidu.com/");
        linkAddRequest.setGid("5iarerh5fn");
        linkAddRequest.setValidDateStart(new Date());
        linkAddRequest.setValidDateType(0);
        linkAddRequest.setDescribe("测试链接");

        LinkCreateVO linkCreateVO = linkService.addShortLink(linkAddRequest, ShortLinkCreateType.CONSOLE);
        System.out.println(linkCreateVO);
        assert linkCreateVO != null;
    }
}
