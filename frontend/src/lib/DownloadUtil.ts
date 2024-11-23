export class DownloadUtil 
{
    /**
     * 提供下载支持
     */
    public static downloadFile(url: string, fileName: string)
    {
        const downloadLink = document.createElement('a');
        downloadLink.href = url;
        downloadLink.download = fileName;
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
    }
}