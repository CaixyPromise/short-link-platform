import React from 'react';
import { Separator } from "@/components/ui/separator";
import Link from "next/link";
import { Icon } from "@/components/ui/icons";
import { useAppSelector } from "@/stores/hooks";

interface FooterProps {
    className?: string;
}
const FooterPage: React.FC<FooterProps> = ({ className }) => {
    const year = new Date().getFullYear();
    const { footer: footerConfig } = useAppSelector(state => state.Layout)

    return (
        <footer className={`bg-white ${className ?? ""}`}>
            {/*<Separator className="mb-4"/>*/}
            <div className="max-w-screen-xl mx-auto px-6 pb-4">
                {/* Links section */}
                <div className="flex justify-center flex-wrap gap-3 text-sm">
                    {footerConfig.links.map((link, index) => (
                        <Link
                            key={index}
                            href={link.href ?? "/"}
                            onClick={link.onClick}
                            className="inline-flex items-center text-muted-foreground hover:underline break-all space-x-2"
                        >
                            {link.icon && (
                                <Icon icon={link.icon} className="w-4 h-4"/>
                            )}
                            <span>{link.label}</span>
                        </Link>
                    ))}
                </div>
                {/* Copyright information */}
                <div className="mt-4 text-center">
                    <p className="text-sm text-muted-foreground">
                        Copyright © {year} {footerConfig.copyright}. All rights reserved.
                    </p>
                </div>
            </div>
        </footer>
    )
}

export default FooterPage;