export interface GroupStoreProps {
    /**
     * 当前群组ID
     */
    currentGroupId: string;
    /**
     * 当前组名称
     */
    currentGroupName: string;
    /**
     * 当前群组信息
     */
    currentGroupInfo: API.GroupItemVO;
    /**
     * 组列表
     */
    groupList: API.GroupItemVO[];
}