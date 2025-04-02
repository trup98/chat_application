import {axiosInstance} from "../../config/axios-config";


export const callAllGroups = async (userId) => {
    return await axiosInstance.get(`/api/group/messages/getGroup/user/${userId}`).then(response => response.data)
}

export const groupCrate = async (groupDetails) => {
    return await axiosInstance.post(`/api/group/messages/crete`, groupDetails).then(response => response.data)
}

export const deleteGroup = async (groupId, senderId) => {
    return await axiosInstance.delete(`/api/group/messages/deleteGroup/${groupId}/${senderId}`).then(response => response.data)
}

export const getGroupChatHistory = async (groupId) => {
    return await axiosInstance.get(`/api/group/messages/getGroupMessage/${groupId}`).then(response => response.data)
}

export const sendMessageInGroup = async (messageContains) => {
    return await axiosInstance.post(`/api/group/messages/send`, messageContains).then(response => response.data)
}

export const getGroupMembers = async (groupId) => {
    return await axiosInstance.get(`/api/group/messages/getMembers/${groupId}`).then(response => response.data)
}

export const getAvailableUsers = async (groupId) => {
    return axiosInstance.get(`/api/group/messages/available/users/${groupId}`).then(response => response.data);
};

export const addUsersToGroup = async (groupId, userIds) => {
    return axiosInstance.post(`/api/group/messages/addUser/group/${groupId}`, userIds).then(response => response.data);
};

export const removeUserFromGroup = async (groupId, userId) => {
    return axiosInstance.delete(`/api/group/messages/delete/user/${groupId}/${userId}`).then(response => response.data);
}