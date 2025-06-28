// 通用上传请求处理，封装秒传逻辑
import { checkFileExist, uploadFile, uploadFileFaster } from '@/api/fileController';
import { FileUploadScene } from '@/enums/FileUploadScene';
import { ResultCode } from '@/enums/ResultCodeEnum';
import { useState, useCallback } from 'react';
import {generateReplayAttackParams} from "@/lib/replayAttackUtil";

/**
 * 生成UUID作为nonce
 */
const generateNonce = (): string => {
	return crypto.randomUUID();
};

/**
 * 生成当前时间戳（秒）
 */
const generateTimestamp = (): number => {
	return Math.floor(Date.now() / 1000);
};

/**
 * 计算文件sha256
 * */
const calcFileSha256 = async (file: File): Promise<string> => {
	if (!file) return Promise.reject();

	const arrayBuffer = await file.arrayBuffer();
	const hashBuffer = await crypto.subtle.digest('SHA-256', arrayBuffer);

	// 把 ArrayBuffer 转成 hex 字符串
	const hashArray = Array.from(new Uint8Array(hashBuffer));
	return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
}

// 计算HMAC-SHA256
const calcHmacSha256 = async (file: Blob, challenge: string, nonce: string, timestamp: string): Promise<string> => {
	const key = await crypto.subtle.importKey(
		'raw',
		new TextEncoder().encode(challenge),
		{ name: 'HMAC', hash: 'SHA-256' },
		false,
		['sign']
	);
	const encoder = new TextEncoder();
	const nonceBytes = encoder.encode(nonce ?? "");
	const timestampBytes = encoder.encode(timestamp ?? "");
	const challengeBytes = encoder.encode(challenge ?? "");
	const fileBuffer = await file.arrayBuffer();

	const totalLength = nonceBytes.length + timestampBytes.length + challengeBytes.length + fileBuffer.byteLength;
	const dataToSign = new Uint8Array(totalLength);
	let offset = 0;
	dataToSign.set(nonceBytes, offset); offset += nonceBytes.length;
	dataToSign.set(timestampBytes, offset); offset += timestampBytes.length;
	dataToSign.set(challengeBytes, offset); offset += challengeBytes.length;
	dataToSign.set(new Uint8Array(fileBuffer), offset);

	const signatureBuffer = await crypto.subtle.sign('HMAC', key, dataToSign);
	return Array.from(new Uint8Array(signatureBuffer)).map(b => b.toString(16).padStart(2, '0')).join('');
};

interface UseUploadFileOptions {
	scene: FileUploadScene;
	onSuccess?: (result: string) => void;
	onError?: (err: any) => void;
	onProgress?: (percent: number) => void;
}

interface UseUploadFileResult {
	status: 'idle' | 'uploading' | 'success' | 'error';
	result?: string;
	error?: any;
	startUpload: (file: File) => Promise<void>;
}

const useUploadFile = (options: UseUploadFileOptions): UseUploadFileResult => {
	const { scene, onSuccess, onError } = options;
	const [status, setStatus] = useState<'idle' | 'uploading' | 'success' | 'error'>('idle');
	const [result, setResult] = useState<string | undefined>();
	const [error, setError] = useState<any>();

	const startUpload = useCallback(async (file: File) => {
		setStatus('uploading');
		setResult(undefined);
		setError(undefined);

		try {
			// 1. 计算sha256
			const fileHash = await calcFileSha256(file);
			// 2. 检查文件是否存在
			const checkResp = await checkFileExist({
				sha256: fileHash,
				size: file.size,
				scene: scene,
			}) as any; // 临时类型断言
			if (checkResp.code !== ResultCode.SUCCESS) {
				throw new Error(checkResp.message || '文件校验失败');
			}
			const token = checkResp.data?.token;
			const challenge = checkResp.data?.challenge as API.FileHmacInfo;
			const fileName = file.name;

			let fasterFailed = false;
			if (challenge) {
				try {
					const salt = challenge.challenge;
					if (!salt) throw new Error('challenge字段缺失');
					if (!token) throw new Error('token缺失');
					if (!fileName) throw new Error('fileName缺失');
					const {offset, length} = challenge;
					if (!offset || !length) throw new Error('offset或length字段缺失');
					const chunk = file.slice(offset, offset + length);
					const challengeNonce = String(challenge?.nonce ?? "");
					const challengeTimestamp = String(challenge?.timestamp ?? "");
					const signature = await calcHmacSha256(chunk, salt, challengeNonce, challengeTimestamp);
					
					// 秒传请求，使用相同的nonce和timestamp
					const fasterResp = await uploadFileFaster({
						biz: scene as any,
						token: token,
						fileName: fileName,
						signature,
						...generateReplayAttackParams()
					}) as any; // 临时类型断言
					if (fasterResp.code === ResultCode.SUCCESS) {
						setStatus('success');
						setResult(fasterResp.data);
						onSuccess?.(fasterResp.data);
						return;
					} else {
						fasterFailed = true;
					}
				} catch (e) {
					console.error("HMAC计算异常", e);
					fasterFailed = true;
				}
			}
			// 普通上传流程
			if (!challenge || fasterFailed) {
				if (!token) throw new Error('token缺失');
				if (!fileName) throw new Error('fileName缺失');
				
				// 普通上传请求，使用相同的nonce和timestamp（支持降级）
				const resp = await uploadFile(
					{ 
						uploadFileRequest: { 
							biz: scene as any, 
							token, 
							fileName,
							...generateReplayAttackParams()
						} 
					},
					file
				) as any; // 临时类型断言
				if (resp?.code === ResultCode.SUCCESS) {
					setStatus('success');
					setResult(resp.data);
					onSuccess?.(resp.data);
				} else {
					setStatus('error');
					setError(resp.message);
					onError?.(resp.message);
				}
			}
		} catch (e: any) {
			setStatus('error');
			setError(e?.message || e);
			onError?.(e);
			console.error(e)
		}
	}, [scene, onSuccess, onError]);

	return {
		status,
		result,
		error,
		startUpload,
	};
};

export default useUploadFile;