import {axiosInstance} from "../../config/axios-config";


export const callAllGroups = async (userId) => {
    return await axiosInstance.get(`/api/group/messages/getGroup/user/${userId}`).then(response => response.data)
}

export const groupCrate = async (groupDetails) => {
    return await axiosInstance.post(`/api/group/messages/crete`, groupDetails).then(response => response.data)
}