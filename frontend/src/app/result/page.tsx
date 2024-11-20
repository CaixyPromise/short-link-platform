import React from 'react';
import Result from "@/components/Result";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";

const ResultPage: React.FC = () =>
{
    return (
        <Result
            status="failure"
            title="提交成功"
            subText="提交结果页用于反馈一系列操作任务的处理结果。如果仅需简单操作，使用 Message 全局提示反馈即可。"
            extraContent={
                <div className="flex justify-center space-x-4">
                    <Button variant="outline">返回列表</Button>
                    <Button variant="outline">查看项目</Button>
                    <Button variant="outline">打印</Button>
                    <Button variant="outline">返回列表</Button>
                    <Button variant="outline">查看项目</Button>
                    <Button variant="outline">打印</Button>
                </div>
            }
        >
            <Card>
                <CardHeader>
                    <CardTitle>项目名称</CardTitle>
                </CardHeader>
                <CardContent>
                    <p>项目 ID: 23421</p>
                    <p>负责人: 曲丽丽</p>
                    <p>生效时间: 2016-12-12 ~ 2017-12-12</p>
                    {/* Add more project details here */}
                </CardContent>
            </Card>
        </Result>

    )
}

export default ResultPage;
