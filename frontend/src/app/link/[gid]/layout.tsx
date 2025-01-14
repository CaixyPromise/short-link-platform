import PageContainer from "@/layout/PageContainer";
import {ConfirmationModalProvider} from "@/components/confirmation-modal/ConfirmationModalContext";
import {LinkStatsModalProvider} from "@/components/LinkStatsModal/context";

interface LinkPageLayoutProps {
    children: React.ReactNode
}
function LinkPageLayout({children}: LinkPageLayoutProps) {
    return (
        <PageContainer>
            <ConfirmationModalProvider>
                <LinkStatsModalProvider>
                    {children}
                </LinkStatsModalProvider>
            </ConfirmationModalProvider>
        </PageContainer>
    )
}

export default LinkPageLayout;