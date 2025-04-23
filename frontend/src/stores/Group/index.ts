import {createSlice} from "@reduxjs/toolkit";
import {GroupStoreProps} from "@/stores/Group/typing";

export const groupSlice = createSlice({
	name: "group",
	initialState: {
		currentGroupId: "",
		currentGroupName: "",
		currentGroupInfo: {} as API.GroupItemVO,
		groupList: [],
		addGroupModalVisible: false
	} as GroupStoreProps,
	reducers: {
		updateAddGroupModalVisible: (state, action) => {
			return {
				...state,
				addGroupModalVisible: action.payload
			}
		},
		setGroupItem: (state, action) => {
			return {
				...state,
				groupList: sortGroups(action.payload)
			}
		},
		deleteGroupItem: (state, action) => {
			state.groupList = state.groupList.filter(item => item.gid !== action.payload)
		},
		addGroupItem: (state, action) => {
			state.groupList.push(action.payload)
		},
		updateGroupLinkCountByGid: (state, action) => {
			const updatedGroupList = state.groupList.map(item => {
				if (item.gid === action.payload.gid) {
					return {...item, linkCount: action.payload.linkCount};
				}
				return item;
			});
			return {
				...state,
				groupList: updatedGroupList,
			};
		},
		modifyGroupItem: (state, action) => {
			const updatedGroupList = state.groupList.map(item =>
				item.gid === action.payload.gid ? {...item, ...action.payload} : item
			);
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
		},
	}
})

export function sortGroups(groups: API.GroupItemVO[]): API.GroupItemVO[] {
	return groups.sort((a, b) => {
		// 比较 sortOrder，越小的越靠前
		const sortOrderA = a.sortOrder ?? Number.MAX_SAFE_INTEGER; // 默认最大值
		const sortOrderB = b.sortOrder ?? Number.MAX_SAFE_INTEGER;

		if (sortOrderA !== sortOrderB) {
			return sortOrderA - sortOrderB;
		}

		// 如果 sortOrder 相等，比较 linkCount，越大的越靠前
		const linkCountA = a.linkCount ?? 0; // 默认值 0
		const linkCountB = b.linkCount ?? 0;

		if (linkCountA !== linkCountB) {
			return linkCountB - linkCountA; // linkCount 越大越靠前
		}

		// 如果 linkCount 相等，比较创建时间，越早的越靠前
		const createTimeA = a.createTime ? new Date(a.createTime).getTime() : Number.MAX_SAFE_INTEGER; // 默认最大值
		const createTimeB = b.createTime ? new Date(b.createTime).getTime() : Number.MAX_SAFE_INTEGER;

		return createTimeA - createTimeB; // 创建时间越早越靠前
	});
}

export const {
	setGroupItem,
	addGroupItem,
	updateAddGroupModalVisible,
	modifyGroupItem,
	updateGroupLinkCountByGid,
	onChangeGroupClick,
	updateCurrentGroupByGid,
	deleteGroupItem
} = groupSlice.actions;

export default groupSlice.reducer;