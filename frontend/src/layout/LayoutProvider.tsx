"use client";
import { Provider } from "react-redux";
import stores from "@/stores";
import InitUserInfoProvider from "@/components/InitUserInfo";
import BasicLayout from "@/layout/BasicLayout";
import { Toaster } from "@/components/ui/toaster";

export default function LayoutProvider({ children }: { children: React.ReactNode }) {
	return (
		<Provider store={stores}>
			<InitUserInfoProvider>
				<BasicLayout>
					{children}
					<Toaster />
				</BasicLayout>
			</InitUserInfoProvider>
		</Provider>
	);
}
