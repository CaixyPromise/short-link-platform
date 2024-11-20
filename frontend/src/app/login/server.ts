import {getCaptcha} from "@/api/captchaController";
import {userLogin} from "@/api/authController";


export const queryServer = {
    captchaImage: async () =>
    {
        const {code, data} = await getCaptcha();

        if (code === 0)
        {
            return data
        }
        return null;
    },
    userLogin: async ({username, password, captcha, captchaId}: {
        username: string,
        password: string,
        captcha: string,
        captchaId: string
    }) =>
    {
        const response = await userLogin({
            userAccount: username,
            userPassword: password,
            captcha: captcha,
            captchaId: captchaId
        });
        return response as API.BaseResponseLoginUserVO_;
    }
}
