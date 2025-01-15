import React from 'react';
import Header from "@/layout/components/Header";
import Footer from "@/components/Footer";
import {useAppSelector} from "@/stores/hooks";
import {
    SidebarInset,
    SidebarProvider,
    SidebarTrigger,
} from "@/components/ui/sidebar"
import {Separator} from "@/components/ui/separator";
import {AppSidebar} from "@/components/SiderBar/AppSiderbar";
import {
    Breadcrumb,
    BreadcrumbItem,
    BreadcrumbLink,
    BreadcrumbList, BreadcrumbPage,
    BreadcrumbSeparator
} from "@/components/ui/breadcrumb";
import {usePathname} from "next/navigation";
import {Condition, Conditional} from "@/components/Conditional";
interface BasicLayoutProps {
    children: React.ReactNode;
}

const BasicLayoutPage: React.FC<BasicLayoutProps> = ({ children }) => {
    const {layout, navigationMenu} = useAppSelector(state => state.Layout);
    const pathname = usePathname();
    const navigation = navigationMenu.items.find(item => item.path === pathname);

    return (
        <Conditional>
            <Condition.When test={navigation?.layout != false}>
                <Condition.Switch value={layout.type}>
                    <Condition.Case case="Sidebar">
                        <SidebarProvider>
                            <AppSidebar />
                            <SidebarInset>
                                <header className="flex h-16 shrink-0 items-center gap-2">
                                    <div className="flex items-center gap-2 px-4">
                                        <SidebarTrigger className="-ml-1" />
                                        <Separator orientation="vertical" className="mr-2 h-4" />
                                        <Breadcrumb>
                                            <BreadcrumbList>
                                                <BreadcrumbItem className="hidden md:block">
                                                    <BreadcrumbLink href="#">
                                                        首页
                                                    </BreadcrumbLink>
                                                </BreadcrumbItem>
                                                {/*<BreadcrumbSeparator className="hidden md:block" />*/}
                                                {/*<BreadcrumbItem>*/}
                                                {/*    <BreadcrumbPage>Data Fetching</BreadcrumbPage>*/}
                                                {/*</BreadcrumbItem>*/}
                                            </BreadcrumbList>
                                        </Breadcrumb>
                                    </div>
                                </header>
                                <div className="flex flex-1 flex-col gap-4 p-4 pt-0">
                                    {children}
                                </div>
                                <Footer />
                            </SidebarInset>
                        </SidebarProvider>
                    </Condition.Case>
                    <Condition.Case case="Header">
                        <div className="flex min-h-screen w-full flex-col">
                            <Header/>
                            {/*content区域*/}
                            <main className="flex-grow">
                                <div className="max-w-7xl mx-auto px-0 lg:px-5">
                                    {children}
                                </div>
                            </main>
                            <Footer/>
                        </div>
                    </Condition.Case>
                </Condition.Switch>
                <Condition.Else>
                    {children}
                </Condition.Else>
            </Condition.When>
        </Conditional>
    );
};

export default BasicLayoutPage;
