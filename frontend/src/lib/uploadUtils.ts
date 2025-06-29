/**
 * 文件上传工具类
 * 用于生成防重放攻击所需的nonce和timestamp
 */

/**
 * 生成UUID作为nonce
 * @returns 唯一的nonce字符串
 */
export function generateNonce(): string {
  return crypto.randomUUID();
}

/**
 * 生成当前时间戳（秒）
 * @returns 当前时间戳
 */
export function generateTimestamp(): number {
  return Math.floor(Date.now() / 1000);
}

/**
 * 生成防重放攻击所需的参数
 * @returns 包含nonce和timestamp的对象
 */
export function generateReplayAttackParams() {
  return {
    nonce: generateNonce(),
    timestamp: generateTimestamp(),
  };
}

/**
 * 验证时间戳是否在有效范围内
 * @param timestamp 时间戳
 * @param tolerance 容差时间（秒），默认5分钟
 * @returns 是否有效
 */
export function isValidTimestamp(timestamp: number, tolerance: number = 300): boolean {
  const currentTime = Math.floor(Date.now() / 1000);
  return Math.abs(currentTime - timestamp) <= tolerance;
}

/**
 * 上传文件请求参数构建器
 */
export class UploadRequestBuilder {
  private nonce: string;
  private timestamp: number;

  constructor() {
    this.nonce = generateNonce();
    this.timestamp = generateTimestamp();
  }

  /**
   * 构建上传请求参数
   * @param biz 业务类型
   * @param token 上传token
   * @param fileName 文件名
   * @param signature 签名（可选，用于秒传）
   * @returns 完整的请求参数
   */
  buildUploadRequest(
    biz: number,
    token: string,
    fileName: string,
    signature?: string
  ) {
    return {
      biz,
      token,
      fileName,
      signature,
      nonce: this.nonce,
      timestamp: this.timestamp,
    };
  }

  /**
   * 获取nonce
   */
  getNonce(): string {
    return this.nonce;
  }

  /**
   * 获取timestamp
   */
  getTimestamp(): number {
    return this.timestamp;
  }
} 