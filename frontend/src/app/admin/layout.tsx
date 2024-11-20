import {Metadata} from "next";

export const metadata: Metadata = {
    title: "表格样例",
    description: "Advanced form example using react-hook-form and Zod.",
}

const Layout = ({ children }: { children: React.ReactNode }) => {
    return (
        <div>
            {children}
        </div>
    );
};

export default Layout;