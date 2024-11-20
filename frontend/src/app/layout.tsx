"use client"
import localFont from "next/font/local";
import "./globals.css";
import BasicLayout from "@/layout/BasicLayout";
import {Provider} from "react-redux";
import stores from "@/stores";

import React from "react";
import InitUserInfoProvider from "@/components/InitUserInfo";
import {Toaster} from "@/components/ui/toaster";
import {useRouter} from "next/router";
import {usePathname} from "next/navigation";

const geistSans = localFont({
  src: "./fonts/GeistVF.woff",
  variable: "--font-geist-sans",
  weight: "100 900",
});
const geistMono = localFont({
  src: "./fonts/GeistMonoVF.woff",
  variable: "--font-geist-mono",
  weight: "100 900",
});


export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {

  return (
    <html lang="zh-CN">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased`}
      >
        <Provider store={stores}>
          <InitUserInfoProvider>
            <BasicLayout>
              {children}
              <Toaster />
            </BasicLayout>
          </InitUserInfoProvider>
        </Provider>
      </body>
    </html>
  );
}
