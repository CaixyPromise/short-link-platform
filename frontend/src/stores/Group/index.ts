import {createSlice} from "@reduxjs/toolkit";
import {GroupStoreProps} from "@/stores/Group/typing";

export const groupSlice = createSlice({
    name: "group",
    initialState: {
        currentGroupId: "",
        currentGroupName: "",
        currentGroupInfo: {} as API.GroupItemVO,
        groupList: []
    } as GroupStoreProps,
    reducers: {
        setGroupItem: (state, action) => {
            return {
                ...state,
                groupList: action.payload
            }
        },
        addGroupItem: (state, action) => {
            state.groupList.push(action.payload)
        },
        modifyGroupItem: (state, action) => {
            console.log('Previous groupList:', state.groupList);
            console.log('Action payload:', action.payload);

            const updatedGroupList = state.groupList.map(item =>
                item.gid === action.payload.gid ? { ...item, ...action.payload } : item
            );

            console.log('Updated groupList:', updatedGroupList);

            // 返回一个新的状态对象
            return {
                ...state,
                groupList: updatedGroupList,
            };
        },

        onChangeGroupClick: (state, action) => {
            const group = action.payload;
            if (group) {
                state.currentGroupId = group.gid || "";
                state.currentGroupName = group.name || "";
                state.currentGroupInfo = group || {};
            }
        },
        updateCurrentGroupByGid: (state, action) => {
            const group = state.groupList.find(item => item.gid === action.payload);
            if (group) {
                state.currentGroupId = group.gid || "";
                state.currentGroupName = group.name || "";
                state.currentGroupInfo = group || {};
            }
        }
    }
})

export const {
    setGroupItem,
    addGroupItem,
    modifyGroupItem,
    onChangeGroupClick,
    updateCurrentGroupByGid
} = groupSlice.actions;

export default groupSlice.reducer;