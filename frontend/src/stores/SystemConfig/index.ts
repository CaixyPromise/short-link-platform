import {createSlice} from "@reduxjs/toolkit";
import {SYSTEM_RUNTIME_CONFIG} from "../../../config/config";
import {SystemRuntimeConfig} from "@/app/typing";

export const systemSlice = createSlice({
	name: "system-config",
	initialState: SYSTEM_RUNTIME_CONFIG as SystemRuntimeConfig,
	reducers: {
		setLayout: (state, action) => {
			return {
				...state,
				...action.payload
			}
		}
	}
})

export const {setLayout} = systemSlice.actions;
export default systemSlice.reducer;