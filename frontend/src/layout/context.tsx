// // context.tsx
// import React, { createContext, useContext, useState } from 'react';
// import {LayoutRuntimeProps} from "@/app/typing";
// import {LAYOUT_RUNTIME_CONFIG} from "../../config/layout";
// import {useAppDispatch, useAppSelector} from "@/stores/hooks";
// import {setLayout} from "@/stores/Layout";
//
// const LayoutContext = createContext<LayoutRuntimeProps>(LAYOUT_RUNTIME_CONFIG);
//
// export const LayoutProvider: React.FC<{
//     children: React.ReactNode;
// }> = ({ children }) => {
//   const layoutStore = useAppSelector(state => state.Layout);
//   const [runtimeProps, setRuntimeProps] = useState<LayoutRuntimeProps>(layoutStore);
//   const dispatch = useAppDispatch();
//   const updateRuntimeProps = (props: LayoutRuntimeProps) => {
//     setRuntimeProps(props);
//     // 更新 layoutStore
//       dispatch(setLayout(props));
//   }
//   return (
//     <LayoutContext.Provider value={{ runtimeProps, updateRuntimeProps }}>
//       {children}
//     </LayoutContext.Provider>
//   );
// }