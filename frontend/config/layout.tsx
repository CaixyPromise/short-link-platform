import {LayoutRuntimeProps} from "@/app/typing";
import {NavItem} from "./menu";

export const LAYOUT_RUNTIME_CONFIG: LayoutRuntimeProps = {
    navigationMenu: {
        items: NavItem,
        collapsedRow: 6
    },
    footer: {
        enable: true,
        copyright: "CAIXYPROMISE",
        links: [
            { href: "https://www.github.com/CaixyPromise", label: "CaixyPromise", icon: "Github"},
            { href: "/", label: "HomePage"},
            { href: "mailto:caixypromised@gmail.com", label: "Contact with Email", icon: "Email"}
        ]
    },
    showSearch: true,
    isHeaderSticky: true,
    layout: {
        type: "Sidebar",
        title: "Next Template App",
        logo: "/assets/logo.svg"
    }
}