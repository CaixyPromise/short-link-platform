package com.caixy.shortlink;

import com.caixy.shortlink.model.vo.group.GroupItemVO;
import com.caixy.shortlink.service.GroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author CAIXYPROMISE
 * @since 2024/11/21 0:20
 */
@SpringBootTest
public class GroupServiceTest
{
    @Autowired
    private GroupService groupService;
    @Test
    public void testGetMyGroupItems() {
        String nickName = "caixypromise";
        List<GroupItemVO> myGroupItems = groupService.getMyGroupItems(nickName);
        System.out.println(myGroupItems);
        assert !myGroupItems.isEmpty();
    }
}
