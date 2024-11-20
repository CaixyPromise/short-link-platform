"use client"

import React, {useState, useRef, useEffect, useCallback} from "react"
import {zodResolver} from "@hookform/resolvers/zod"
import {useForm} from "react-hook-form"
import {z} from "zod"
import {motion, AnimatePresence} from "framer-motion"

import {
    Form,
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form"
import {Input} from "@/components/ui/input"
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import {Textarea} from "@/components/ui/textarea"
import {toast} from "@/hooks/use-toast"
import {Button} from "@/components/ui/button"
import {CircleUser, Search, X, CheckCircle, XCircle} from "lucide-react"
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar"
import {Icon, Icons} from "@/components/ui/icons";
import {useUserInfo} from "@/hooks/useUserInfo";

import Spinner from "@/components/Spinner";
import {Condition, Conditional} from "@/components/Conditional";
import useAsyncHandler from "@/hooks/useAsyncHandler";
import {queryServer} from "@/app/settings/server";
import {useAppDispatch} from "@/stores/hooks";
import {setLoginUser} from "@/stores/LoginUser";

const profileFormSchema = z.object({
    userName: z
        .string()
        .min(2, {
            message: "Username must be at least 2 characters.",
        })
        .max(30, {
            message: "Username must not be longer than 30 characters.",
        }),
    userProfile: z
        .string()
        .max(512, {
            message: "Profile must not be longer than 512 characters.",
        }),
    userGender: z
        .number()
        .min(0, {
            message: "Please select a gender.",
        })
        .max(2, {
            message: "Please select a gender.",
        }),
})

type ProfileFormValues = z.infer<typeof profileFormSchema>


function AvatarDisplay()
{
    const [isExpanded, setIsExpanded] = useState(false)
    const [isImageLoaded, setIsImageLoaded] = useState(false)
    const [isImageLoadedExpanded, setIsImageLoadedExpanded] = useState(true)
    const fileInputRef = useRef<HTMLInputElement>(null)
    const [isImageLoading, isIsImageLoading] = useState<boolean>(true)
    const loginUser = useUserInfo()
    const dispatch = useAppDispatch()

    useEffect(() =>
    {
        // 重置图像加载状态
        if (!isExpanded) {
            setIsImageLoadedExpanded(true)
        }
    }, [isExpanded])

    const handleAvatarClick = () =>
    {
        if (isImageLoaded) {
            setIsExpanded(true)
        } else {
            handleChangeAvatar()
        }
    }

    const handleClose = () =>
    {
        setIsExpanded(false)
    }

    const handleChangeAvatar = () =>
    {
        fileInputRef.current?.click()
    }

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) =>
    {
        const file = event.target.files?.[0]
        if (file) {
            // 获取文件大小
            const fileSize = file.size / 1024 / 1024 // 转换为MB
            if (fileSize > 2) {
                toast({
                    title: "File size too large",
                    description: "The file size must not exceed 2MB.",
                    variant: "destructive",
                })
            }
            // 判断文件类型
            if (!file.type.startsWith("image/")) {
                toast({
                    title: "Invalid file type",
                    description: "Only image files are allowed.",
                    variant: "destructive",
                })
            }
            // 判断文件后缀，是否符合"jpeg", "jpg", "svg", "png", "webp"
            const allowedExtensions = ["jpeg", "jpg", "svg", "png", "webp"]
            const fileExtension = file.name.split(".").pop()?.toLowerCase()
            if (!allowedExtensions.includes(fileExtension || "")) {
                toast({
                    title: "Invalid file extension",
                    description: "Only the following file extensions are allowed: " + allowedExtensions.join(", "),
                    variant: "destructive",
                })
            }
            // 上传逻辑
            queryServer.uploadAvatar(file)
                .then((response => {
                    // @ts-ignore
                    if (response.code === 0)
                    {
                        toast({
                            title: "Avatar updated",
                            description: "Your new avatar has been uploaded successfully.",
                        })
                        setIsImageLoaded(true)
                        setIsImageLoadedExpanded(true)
                        dispatch(setLoginUser({
                            ...loginUser,
                            // @ts-ignore
                            userAvatar: response?.data,
                        }));
                    }
                    else
                    {
                        toast({
                            title: "Avatar update failed",
                            // @ts-ignore
                            description: response.message,
                            variant: "destructive",
                        })
                    }
                }))
                .catch(error => {
                    console.log(error)
                    toast({
                        title: "Avatar update failed",
                        variant: "destructive",
                    });
                })
        }
    }


    const handleExpandedImageError = () =>
    {
        setIsImageLoadedExpanded(false)
    }

    return (
        <div className="relative">
            <div
                className="w-40 h-40 rounded-full overflow-hidden cursor-pointer relative"
                onClick={handleAvatarClick}
            >
                <Spinner loading={isImageLoading}>
                    <Avatar className="w-full h-full">
                        <AvatarImage
                            src={loginUser.userAvatar}
                            onLoadingStatusChange={(status) =>
                            {
                                if (status === "loaded") {
                                    setIsImageLoaded(true)
                                }
                                if (status !== "loading") {
                                    isIsImageLoading(false)
                                }
                            }}
                        />
                        <AvatarFallback><CircleUser className="w-full h-full"/></AvatarFallback>
                    </Avatar>
                    <div
                        className="absolute inset-0 bg-black bg-opacity-30 flex items-center justify-between opacity-0 hover:opacity-100 transition-opacity">
                        {isImageLoaded ? (
                            <>
                                <div
                                    className="w-1/2 h-full flex items-center justify-center hover:bg-black hover:bg-opacity-20 active:bg-opacity-30 transition-colors">
                                    <Search className="text-white" size={24}/>
                                </div>
                                <div className="w-px h-full bg-white bg-opacity-30"/>
                                <div
                                    className="w-1/2 h-full flex items-center justify-center hover:bg-black hover:bg-opacity-20 active:bg-opacity-30 transition-colors"
                                    onClick={(e) =>
                                    {
                                        e.stopPropagation();
                                        handleChangeAvatar();
                                    }}
                                >
                                    <Icons.ImageUpload className="text-white" size={24}/>
                                </div>
                            </>
                        ) : (
                            <div
                                className="w-full h-full flex items-center justify-center hover:bg-black hover:bg-opacity-20 active:bg-opacity-30 transition-colors"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    handleChangeAvatar();
                                }}
                            >
                                <Icons.ImageUpload className="text-white" size={24}/>
                            </div>
                        )}
                    </div>
                </Spinner>


            </div>
            <AnimatePresence>
                {isExpanded && (
                    <motion.div
                        initial={{opacity: 0, scale: 0.8}}
                        animate={{opacity: 1, scale: 1}}
                        exit={{opacity: 0, scale: 0.8}}
                        className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
                        onClick={handleClose}
                    >
                        <div className="relative" onClick={(e) => e.stopPropagation()}>
                            <div className="w-[80vw] h-[80vh] max-w-[800px] max-h-[800px] overflow-hidden">
                                <img
                                    src={loginUser?.userAvatar || ""}
                                    alt="User Avatar"
                                    className="w-full h-full object-contain"
                                    onError={handleExpandedImageError}
                                />
                                {!isImageLoadedExpanded && (
                                    <div className="absolute inset-0 flex items-center justify-center bg-muted">
                                        <CircleUser className="w-1/2 h-1/2 text-muted-foreground"/>
                                    </div>
                                )}
                            </div>
                            <button
                                onClick={handleClose}
                                className="absolute top-2 right-2 text-white bg-black bg-opacity-50 rounded-full p-1 hover:bg-opacity-70 transition-colors"
                                aria-label="Close expanded view"
                            >
                                <X size={24}/>
                            </button>
                            {isImageLoadedExpanded && (
                                <button
                                    onClick={handleChangeAvatar}
                                    className="absolute bottom-2 right-2 text-white bg-black bg-opacity-50 rounded-full p-2 hover:bg-opacity-70 transition-colors"
                                    aria-label="Change avatar"
                                >
                                    <Icons.ImageUpload size={24}/>
                                </button>
                            )}
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>
            <input
                type="file"
                ref={fileInputRef}
                onChange={handleFileChange}
                accept=".jpeg, .jpg, .svg, .png, .webp"
                className="hidden"
                aria-label="上传头像"
            />

        </div>
    )
}

export function ProfileForm()
{
    const userInfo = useUserInfo();
    const [canModify, setCanModify] = useState<boolean>(false);
    const [submitHandler] = useAsyncHandler<boolean>();
    const form = useForm<ProfileFormValues>({
        resolver: zodResolver(profileFormSchema),
        defaultValues: userInfo,
        mode: "onChange",
    })
    const { isSubmitting } = form.formState;

    const onSubmit = async (data: ProfileFormValues) =>
    {
        console.log("data: ", data)
        console.log("userInfo: ", userInfo)
        if (data.userProfile === userInfo.userProfile &&
            data.userGender === userInfo.userGender &&
            data.userName === userInfo.userName )
        {
            toast({
                title: "无需提交",
                description: (
                    <div className="flex item-center">
                        <Icon icon="CircleAlert" className="text-yellow-400 mr-1 h-4 w-4" />
                        <p>数据没有变化，无需修改:)</p>
                    </div>
                ),
            });
            return;
        }
        toast({
            title: "正在提交表单",
            description: (
                <div className="flex item-center">
                    <Icon icon="Spinner" className="animate-spin mr-1 h-4 w-4" />
                    <p>您的信息正在提交，请稍候...</p>
                </div>
            ),
        });
        const result = await submitHandler(queryServer.updateUserInfo, [data as API.UserUpdateProfileRequest]);

        toast({
            title: result ? "提交成功" : "提交失败",
            description: <>
                <div className="flex item-center">
                    <Conditional value={result}>
                        <Condition.When test={result}>
                            <CheckCircle className="text-green-500 mr-1 h-4 w-4" />
                            <p>您的信息已成功提交。</p>
                            <Condition.Else>
                                <XCircle className="text-red-500 mr-1 h-4 w-4" />
                                <p>提交失败，请稍后再试。</p>
                            </Condition.Else>
                        </Condition.When>
                    </Conditional>
                </div>
            </>,
            duration: 3000,
        });
        setCanModify(false)
    };

    return (
        <div className="flex">
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8 flex-1">
                    <FormField
                        control={form.control}
                        name="userName"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>用户名</FormLabel>
                                <FormControl>
                                    <Input disabled={!canModify} placeholder="用户名" {...field}
                                           defaultValue={userInfo?.userName ?? ""}/>
                                </FormControl>
                                <FormDescription>
                                    这是您的公开显示名称。最少2个字符，最多30个字符。
                                </FormDescription>
                                <FormMessage/>
                            </FormItem>
                        )}
                    />


                    <FormField
                        control={form.control}
                        name="userProfile"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>个人介绍</FormLabel>
                                <FormControl>
                                    <Textarea
                                        placeholder="Tell us a little bit about yourself"
                                        className="resize-none"
                                        defaultValue={field.value}
                                        disabled={!canModify}
                                        {...field}
                                    />
                                </FormControl>
                                <FormDescription>
                                    用于描述您自己。最多100个字符。
                                </FormDescription>
                                <FormMessage/>
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="userGender"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>性别</FormLabel>
                                <Select onValueChange={(value)=>{
                                    field.onChange( Number(value) )
                                }} disabled={!canModify}
                                        defaultValue={String(field.value)}>
                                    <FormControl>
                                        <SelectTrigger>
                                            <SelectValue placeholder="Select a verified email to display"/>
                                        </SelectTrigger>
                                    </FormControl>
                                    <SelectContent>
                                        <SelectItem value={"1"}>男</SelectItem>
                                        <SelectItem value={"2"}>女</SelectItem>
                                        <SelectItem value={"0"}>保密</SelectItem>
                                    </SelectContent>
                                </Select>
                                <FormDescription>
                                    设置你的对外展示的性别。
                                </FormDescription>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                    <div>
                        <Button
                            type={canModify ? "submit" : "button"}
                            onClick={(e) =>
                            {
                                if (!canModify) {
                                    e.preventDefault();
                                    setCanModify(true);
                                }
                            }}
                            className="mr-3 relative overflow-hidden"
                            disabled={isSubmitting}
                        >
                            <AnimatePresence mode="wait">
                                <motion.div
                                    key={canModify || isSubmitting ? "submit" : "modify"}
                                    initial={{opacity: 0, x: -20}}
                                    animate={{opacity: 1, x: 0}}
                                    exit={{opacity: 0, x: 20}}
                                    transition={{duration: 0.2}}
                                    className="flex items-center"
                                >
                                    <Conditional value={isSubmitting}>
                                        <Condition.When test={isSubmitting}>
                                            <div className="flex items-center">
                                                <Icon icon="Spinner" className="animate-spin mr-1 h-4 w-4"/>
                                                <span>正在提交...</span>
                                            </div>

                                            <Condition.Else>
                                                <Conditional value={canModify}>
                                                    <Condition.Switch value={canModify}>
                                                        <Condition.Case case={true}>
                                                            <Icon icon="SendHorizontal" className="mr-1 h-4 w-4"/>
                                                            提交信息
                                                        </Condition.Case>
                                                        <Condition.Case case={false}>
                                                            <Icon icon="Pen" className="mr-1 h-4 w-4"/>
                                                            修改信息
                                                        </Condition.Case>
                                                    </Condition.Switch>
                                                </Conditional>
                                            </Condition.Else>
                                        </Condition.When>
                                    </Conditional>
                                </motion.div>
                            </AnimatePresence>
                        </Button>

                        <Conditional value={canModify}>
                            <Condition.When test={canModify}>
                                <Button
                                    variant="destructive"
                                    onClick={() =>
                                    {
                                        setCanModify(false)
                                    }}
                                >
                                    <Icon icon="CircleX" className="mr-1 h-4 w-4"/>

                                    取消修改
                                </Button>
                            </Condition.When>
                        </Conditional>
                    </div>
                </form>
            </Form>
            <div className="ml-10 flex-shrink-0">
                <AvatarDisplay/>
            </div>
        </div>
    )
}
