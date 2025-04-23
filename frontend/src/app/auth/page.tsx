'use client'

import {useState, useEffect} from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { LoginForm } from './components/login-form'
import { RegisterForm } from './components/register-form'
import { ForgotPasswordForm } from './components/forgot-password-form'
import {ActivateAccountForm} from "@/app/auth/components/activate-account-form";
import {useAuthPageData} from "@/app/auth/contexts";
import {FormStateEnum} from "@/app/auth/enums";

export default function AuthPage() {
	const [isMobile, setIsMobile] = useState(false)
	const {authState} = useAuthPageData();

	useEffect(() => {
		const checkMobile = () => setIsMobile(window.innerWidth < 1024)
		checkMobile()
		window.addEventListener('resize', checkMobile)
		return () => window.removeEventListener('resize', checkMobile)
	}, [])

	const renderForm = (key: string) => {
		switch (authState) {
			case FormStateEnum.LOGIN:
				return (
					<LoginForm
						key={key}
					/>
				)
			case FormStateEnum.REGISTER:
				return (
					<RegisterForm
						key={key}
					/>
				)
			case FormStateEnum.FORGET:
				return (
					<ForgotPasswordForm
						key={key}
					/>
				)
			case FormStateEnum.ACTIVATE:
				return <ActivateAccountForm key={key} />
		}
	}

	const pageVariants = {
		initial: { opacity: 0, x: 100 },
		in: { opacity: 1, x: 0 },
		out: { opacity: 0, x: -100 },
	}

	const pageTransition = {
		type: "tween",
		duration: 0.35,
		ease: "easeInOut"
	}


	return (
		<>
			{/* Content */}
			<div className="relative flex min-h-screen">
				{/* Desktop Layout */}
				<div className="hidden lg:flex w-full">
					<motion.div
						className="flex-1 flex items-center justify-center"
						initial={false}
						animate={{x: authState === FormStateEnum.REGISTER ? '-100%' : '0%'}}
						transition={pageTransition}
					>
						<AnimatePresence mode="wait">
							<motion.div
								key={authState.getCode()}
								className="flex-1 flex items-center justify-center"
								initial="initial"
								animate="in"
								exit="out"
								variants={pageVariants}
								transition={pageTransition}
							>
								{authState !== FormStateEnum.REGISTER && renderForm('desktop-left')}
							</motion.div>
						</AnimatePresence>
					</motion.div>

					<motion.div
						className="flex-1 flex items-center justify-center"
						initial={false}
						animate={{x: authState === FormStateEnum.REGISTER ? '0%' : '100%'}}
						transition={pageTransition}
					>
						<AnimatePresence mode="wait">
							<motion.div
								key={authState.getCode()}
								className="flex-1 flex items-center justify-center"
								initial="initial"
								animate="in"
								exit="out"
								variants={pageVariants}
								transition={pageTransition}
							>
								{authState === FormStateEnum.REGISTER && renderForm('desktop-right')}
							</motion.div>
						</AnimatePresence>
					</motion.div>

					{/* Decorative Side Content */}
					<motion.div
						className="absolute top-1/2 transform -translate-y-1/2 w-1/2 p-12 text-center"
						initial={false}
						animate={{
							x: authState === FormStateEnum.REGISTER ? '0%' : '100%',
							opacity: 1
						}}
						transition={pageTransition}
					>
						<div className="space-y-4">
							<h2 className="text-4xl font-bold">Join Our Community</h2>
							<p className="text-xl text-zinc-400">Create Your High-Level Cloud Network Service!</p>
						</div>
					</motion.div>
				</div>

				{/* Mobile Layout */}
				<AnimatePresence initial={false}>
					{isMobile && (
						<motion.div
							key={authState.getCode()}
							className="flex flex-col w-full min-h-screen items-center justify-center p-4"
							initial="initial"
							animate="in"
							exit="out"
							variants={pageVariants}
							transition={pageTransition}
						>
							{renderForm('mobile')}
							<motion.div
								className="mt-8 text-center"
								initial={{ opacity: 0 }}
								animate={{ opacity: 1 }}
								transition={{ delay: 0.3 }}
							>
								<h2 className="text-2xl font-bold text-white">Join Our Community</h2>
								<p className="text-sm text-zinc-400">Create Your High-Level Cloud Network Service!</p>
							</motion.div>
						</motion.div>
					)}
				</AnimatePresence>
			</div>
		</>
	)
}

