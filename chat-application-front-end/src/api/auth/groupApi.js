import {axiosInstance} from "../../config/axios-config";


export const callAllGroups = async (userId) => {
    return await axiosInstance.get(`/api/group/messages/getGroup/user/${userId}`).then(response => response.data)
}

export const groupCrate = async (groupDetails) => {
    return await axiosInstance.post(`/api/group/messages/crete`, groupDetails).then(response => response.data)
}

export const deleteGroup = async (groupId,senderId) => {
    return await axiosInstance.delete(`/api/group/messages/deleteGroup/${groupId}/${senderId}`).then(response => response.data)
}

export const getGroupChatHistory = async (groupId) => {
    return await axiosInstance.get(`/api/group/messages/getGroupMessage/${groupId}`).then(response => response.data)
}

export const sendMessageInGroup = async (messageContains) => {
    return await axiosInstance.post(`/api/group/messages/send`, messageContains).then(response => response.data)
}