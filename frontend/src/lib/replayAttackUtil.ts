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