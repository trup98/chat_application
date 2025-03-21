import {axiosInstance} from "../../config/axios-config";


export const loginApi = async (loginDetails) => {
    return await axiosInstance.post('/api/v1/auth/login',loginDetails).then(res => res.data);
}