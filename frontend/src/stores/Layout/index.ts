import {createSlice} from "@reduxjs/toolkit";
import {LAYOUT_RUNTIME_CONFIG} from "../../../config/layout";

export const layoutSlice = createSlice({
    name: "layout",
    initialState: LAYOUT_RUNTIME_CONFIG,
    reducers: {
        setLayout: (state, action) => {
            return {
                ...state,
                ...action.payload
            }
        }
    }
})

export const {setLayout} = layoutSlice.actions;
export default layoutSlice.reducer;