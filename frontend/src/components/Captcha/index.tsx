import { Captcha } from './captcha-root'
import { ImageCaptcha } from './captcha-image';
import { CodeCaptcha } from './captcha-code';

Captcha.Image = ImageCaptcha;
Captcha.Code = CodeCaptcha;

export { Captcha }
export type { CaptchaProps, CaptchaRef } from './typing';
