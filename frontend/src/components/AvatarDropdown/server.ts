import {userLogout} from "@/api/authController";

export const queryServer = {
    logOut: async () => {
        return await userLogout();
    }
}
