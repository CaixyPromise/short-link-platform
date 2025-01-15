import {getCaptcha} from "@/api/captchaController";


export const queryServer = {
	captchaImage: async () => {
		const {code, data} = await getCaptcha();
		if (code === 0) {
			return data
		}
		return null;
	},
}
