'use client'

import {Stepper} from "@stepperize/core";
import React from "react";

interface ModifyEmailContext {
	formData?: API.UserResetEmailRequest;
	setFormData?: React.Dispatch<React.SetStateAction<API.UserResetEmailRequest>>;
	stepper?: Stepper<never>;
	setVisible?: (state: boolean) => void;
}

export const ModifyEmailContext = React.createContext<ModifyEmailContext | null>({});

export const useModifyEmailContext = () => {
	const context = React.useContext(ModifyEmailContext);
	if (!context) {
		throw new Error("useModifyEmailContext must be used within a ModifyEmailProvider");
	}
	return context;
}