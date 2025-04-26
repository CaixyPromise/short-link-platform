import {
	modifyPassword, modifyPasswordStepByIdentification,
	resetEmail,
	submitResetEmail,
	submitResetEmailIdentify, submitResetEmailValidNewEmail,
	updateMeProfile
} from "@/api/userController";
import {sendEmail} from "@/api/emailController";
import {uploadFile} from "@/api/fileController";

export const queryServer = {
	updateUserInfo: async (data: API.UserUpdateProfileRequest) => {
		const {code} = await updateMeProfile(data);
		return code === 0;
	},
	submitModifyEmailStepByCheckOrigin: async (data: API.UserResetEmailRequest) => {
		if (!data?.originalEmail) {
			return Promise.reject("请输入原邮箱");
		}
		return await submitResetEmail(data);
	},
	submitModifyStepByPasswordAndCode: async (data: API.UserResetEmailRequest) => {
		const {password, code} = data;
		if (!password || !code) {
			return Promise.reject("请输入密码和验证码");
		}
		return await submitResetEmailIdentify(data);
	},
	validNewEmail: async (data: API.UserResetEmailRequest) => {
		if (!data?.newEmail) {
			return Promise.reject("请输入新邮箱");
		}
		return await submitResetEmailValidNewEmail(data);
	},
	modifyEmail: async (data: API.UserResetEmailRequest) => {
		return await resetEmail(data);
	},

	/** 修改密码 **/
	resetPassword: async (data: API.UserModifyPasswordRequest) => {
		return await modifyPassword(data);
	},
	modifyPasswordStepByIdentification: async () => {
		return await modifyPasswordStepByIdentification();
	},
	/** 更新头像 **/
	uploadAvatar: async (file: File) => {
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
