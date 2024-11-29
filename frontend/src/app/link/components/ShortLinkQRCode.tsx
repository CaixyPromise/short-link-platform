"use client"
import React, {useRef, useState} from "react";
import {QRCodeCanvas} from "qrcode.react";
import {Button} from "@/components/ui/button";
import {DownloadUtil} from "@/lib/DownloadUtil";
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {ScanFace} from "lucide-react";

const ShortLinkQRCode: React.FC<{
    url: string;
    name: string;
}> = ({url, name}) => {
    // 二维码添加来源
    const [qrValue, setQrValue] = useState(`${url}?from=qrCode`);  // 存储二维码的value值
    const qrRef = useRef<HTMLCanvasElement>(null);

    // 刷新二维码内容的函数
    const handleRefresh = () => {
        console.log("qrValue: ", qrValue)

        const newUrl = `${url}?from=qrCode?refresh=${new Date().getTime()}`;
        setQrValue(newUrl);
    };

    if (url === '') {
        return null;
    }

    const handleDownload = () => {
        if (qrRef.current) {
            const canvas = qrRef.current;
            const pngUrl = canvas.toDataURL("image/png");

            DownloadUtil.downloadFile(pngUrl, `${name}-QRCode.png`);
        }
    };

    return (
        <Popover>
            <PopoverTrigger asChild>
                <Button variant="ghost" size="icon" className="mr-0 cursor-pointer hover:text-blue-300">
                    <ScanFace/>
                </Button>
            </PopoverTrigger>
            <PopoverContent className="w-80" side="right">
                <div className="flex flex-col items-center">
                    <h4 className="font-medium mb-2">{name}-分享二维码</h4>
                </div>
                <div className="flex justify-center items-center">
                    <QRCodeCanvas
                        ref={qrRef}
                        value={qrValue}  // 使用当前的qrValue
                        size={200}
                        bgColor="#ffffff"
                        fgColor="#000000"
                        level="H"
                        imageSettings={{
                            src: 'https://avatars.githubusercontent.com/u/81923692?v=4',
                            height: 40, // Logo的高度
                            width: 40, // Logo的宽度
                            excavate: true,
                            crossOrigin: 'anonymous',
                        }}
                    />
                </div>
                <div className="flex justify-center gap-2 mt-4">
                    {/* 刷新按钮 */}
                    <Button onClick={handleRefresh}>刷新</Button>
                    <Button onClick={handleDownload}>下载</Button>
                </div>
            </PopoverContent>
        </Popover>
    );
};

export default ShortLinkQRCode;