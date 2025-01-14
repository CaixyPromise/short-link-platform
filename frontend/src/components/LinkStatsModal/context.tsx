"use client"

import React, {createContext, ReactNode, useContext, useEffect, useState} from "react";
import ShortLinkStats from "@/components/LinkStatsModal/index";

interface LinkStatsModalContextType {
	statsModalVisible: boolean;
	setStatsModalVisible: (visible: boolean) => void;
	linkItem: API.LinkVO;
	setLinkItem: (item: API.LinkVO) => void;
}

const LinkStatsModalContext = createContext<LinkStatsModalContextType>({
	statsModalVisible: false,
	setStatsModalVisible: () => {},
	linkItem: {} as API.LinkVO,
	setLinkItem: () => {}
});

export const LinkStatsModalProvider: React.FC<{ children: ReactNode }> = ({children}) => {
	const [visible, setVisible] = useState(false);
	const [linkItem, setLinkItem] = useState({} as API.LinkVO);


	return (
		<LinkStatsModalContext.Provider
			value={{
				statsModalVisible: visible,
				setStatsModalVisible: setVisible,
				linkItem,
				setLinkItem
			}}
		>
			{children}
			<ShortLinkStats/>
		</LinkStatsModalContext.Provider>
	)
}

export const useLinkStatsModal = (): LinkStatsModalContextType => {
	const context = useContext<LinkStatsModalContextType>(LinkStatsModalContext)
	if (!context) {
		throw new Error('useLinkStatsModal must be used within a LinkStatsModalProvider')
	}
	return context;
}