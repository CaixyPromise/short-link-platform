export function copyToClipboard(text:string) {
    navigator
        .clipboard
        .writeText(text).then(() => {
            console.log("复制成功")
        }, (err) => {
            console.error("复制失败", err)
        });
}
