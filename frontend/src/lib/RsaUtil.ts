// 将 ArrayBuffer 转换为 Base64 字符串
function arrayBufferToBase64(buffer: ArrayBuffer): string {
	const bytes = new Uint8Array(buffer);
	let binary = '';
	bytes.forEach(b => binary += String.fromCharCode(b));
	return window.btoa(binary);
}

// 将 Base64 字符串转换为 ArrayBuffer
function base64ToArrayBuffer(base64: string): ArrayBuffer {
	const binary = window.atob(base64);
	const bytes = new Uint8Array(binary.length);
	for (let i = 0; i < binary.length; i++) {
		bytes[i] = binary.charCodeAt(i);
	}
	return bytes.buffer;
}

export enum RSA_KEY_PAIR {
	PRIVATE = "pkcs8",
	PUBLIC = "spki"
}

export class RsaUtil {
	/**
	 * RSA 算法参数，设为只读以防修改
	 */
	private static readonly algorithm = {
		name: "RSA-OAEP",
		modulusLength: 2048,
		publicExponent: new Uint8Array([0x01, 0x00, 0x01]),
		hash: "SHA-256"
	};


	/**
	 * 生成新的 RSA 密钥对。
	 * @returns 一个 Promise，解析为生成的 CryptoKeyPair 对象。
	 */
	public static async generateRsaKeyPair(): Promise<CryptoKeyPair> {
		return await window.crypto.subtle.generateKey(
			this.algorithm,
			true,
			["encrypt", "decrypt"]
		);
	}

	/**
	 * 导出给定的密钥为 Base64 编码的字符串。
	 * @param key - 要导出的 CryptoKey。
	 * @param format - 导出格式："spki" 用于公钥，"pkcs8" 用于私钥。
	 * @returns 一个 Promise，解析为导出的 Base64 编码字符串。
	 */
	public static async exportKey(key: CryptoKey, format: RSA_KEY_PAIR): Promise<string> {
		const exported = await window.crypto.subtle.exportKey(format, key);
		return arrayBufferToBase64(exported);
	}

	/**
	 * 导出给定的密钥为 Base64 编码的字符串。
	 */
	public static async exportPair(keyPair: CryptoKeyPair): Promise<{ publicKey: string, privateKey: string }> {
		return {
			publicKey: await this.exportKey(keyPair.publicKey, RSA_KEY_PAIR.PUBLIC),
			privateKey: await this.exportKey(keyPair.privateKey, RSA_KEY_PAIR.PRIVATE)
		}
	}

	/**
	 * 从 Base64 编码的字符串导入公钥。
	 * @param publicKeyBase64 - Base64 编码的公钥（SPKI 格式）。
	 * @returns 一个 Promise，解析为导入的 CryptoKey 公钥对象。
	 */
	public static async importPublicKey(publicKeyBase64: string): Promise<CryptoKey> {
		const binaryDer = base64ToArrayBuffer(publicKeyBase64);
		return await window.crypto.subtle.importKey(
			RSA_KEY_PAIR.PUBLIC,
			binaryDer,
			this.algorithm,
			true,
			["encrypt"]
		);
	}

	/**
	 * 使用 Base64 编码的公钥加密明文。
	 * @param plainText - 要加密的明文字符串。
	 * @param publicKeyBase64 - Base64 编码的公钥。
	 * @returns 一个 Promise，解析为加密后的 Base64 编码字符串。
	 */
	public static async encryptWithPublicKey(plainText: string, publicKeyBase64: string): Promise<string> {
		const publicKey = await this.importPublicKey(publicKeyBase64);
		const encoder = new TextEncoder();
		const data = encoder.encode(plainText);

		const encrypted = await window.crypto.subtle.encrypt(
			{name: "RSA-OAEP"},
			publicKey,
			data
		);

		return arrayBufferToBase64(encrypted);
	}

	/**
	 * 从 Base64 编码的字符串导入私钥。
	 * @param privateKeyBase64 - Base64 编码的私钥（PKCS#8 格式）。
	 * @returns 一个 Promise，解析为导入的 CryptoKey 私钥对象。
	 */
	public static async importPrivateKey(privateKeyBase64: string): Promise<CryptoKey> {
		const binaryDer = base64ToArrayBuffer(privateKeyBase64);
		return await window.crypto.subtle.importKey(
			RSA_KEY_PAIR.PRIVATE,
			binaryDer,
			this.algorithm,
			true,
			["decrypt"]
		);
	}

	/**
	 * 使用 Base64 编码的私钥解密加密的 Base64 字符串。
	 * @param encryptedBase64 - 使用公钥加密后的 Base64 编码字符串。
	 * @param privateKeyBase64 - Base64 编码的私钥。
	 * @returns 一个 Promise，解析为解密后的明文字符串。
	 */
	public static async decryptWithPrivateKey(encryptedBase64: string, privateKeyBase64: string): Promise<string> {
		const privateKey = await this.importPrivateKey(privateKeyBase64);
		const encryptedData = base64ToArrayBuffer(encryptedBase64);

		const decrypted = await window.crypto.subtle.decrypt(
			{name: "RSA-OAEP"},
			privateKey,
			encryptedData
		);

		const decoder = new TextDecoder();
		return decoder.decode(decrypted);
	}
}