import {modifyPassword, resetEmail, updateMeProfile} from "@/api/userController";
import {sendEmail} from "@/api/emailController";
import {uploadFile} from "@/api/fileController";

export const queryServer = {
    updateUserInfo: async (data: API.UserUpdateProfileRequest) => {
        const {code} = await updateMeProfile(data);
        return code === 0;
    },
    fetchEmailCode: async (data: API.SendEmailRequest) => {
        const {code} = await sendEmail(data);
        return code === 0;
    },
    modifyEmail: async (data: API.UserResetEmailRequest) => {
        const {code} = await resetEmail(data);
        return code === 0;
    },
    resetPassword: async (data: API.UserModifyPasswordRequest) => {
        const {code} = await modifyPassword(data);
        return code === 0;
    },
    uploadAvatar: async (file: File)=> {
        if (file) {
            return await uploadFile(
                null,
                {
                    biz: "user_avatar"
                },
                file)
        }
    }
}
