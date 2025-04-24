import "./globals.css";
import React from "react";
import LayoutProvider from "@/layout/LayoutProvider";
import {Metadata, Viewport} from "next";
import {SYSTEM_RUNTIME_CONFIG} from "../../config/config";
export const metadata: Metadata = {
  title: {
    default: SYSTEM_RUNTIME_CONFIG.title,
    template: "%s",
  },
  authors: {
    url: "http://localhost:3000",
    name: "CAIXYPROMISE",
  },
  description: "开源短链平台，全链路追踪",
  keywords: ["短链", "开源", "链路追踪", "开放接口"],

  openGraph: {
    type: "website",
    url: "http://localhost:3000",
    title: SYSTEM_RUNTIME_CONFIG.title,
    description: `欢迎来到 ${SYSTEM_RUNTIME_CONFIG.title}，我们提供一站式短链解决方案。`,
    images: [
      {
        url: "http://localhost:3000/og-image.png",
        width: 1200,
        height: 630,
        alt: `${SYSTEM_RUNTIME_CONFIG.title} 封面图`,
      },
    ],
  },
};

export const viewport: Viewport = {
  width: 'device-width',
  initialScale: 1
}


export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="zh-CN">
      <body>
        <LayoutProvider>
          {children}
        </LayoutProvider>
      </body>
    </html>
  );
}
