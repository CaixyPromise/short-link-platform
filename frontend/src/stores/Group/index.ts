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
            state.groupList = action.payload
        },
        addGroupItem: (state, action) => {
            state.groupList.push(action.payload)
        },
        modifyGroupItem: (state, action) => {
            const index = state.groupList.findIndex(item => item.id === action.payload.id)
            if (index !== -1) {
                state.groupList[index] = action.payload
            }
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