import {axiosInstance} from "../../config/axios-config";


export const callAllUser = async (searchkey, pageNumber, pageSize, senderId) => {
    return await axiosInstance.get(`/api/v1/user/getAllUser?pageNo=${pageNumber}&pageSize=${pageSize}&searchKey=${searchkey}&senderId=${senderId}`).then(response => response.data)
}

export const getChatHistory = async (senderId, receiverId) => {
    return await axiosInstance.get(`/api/messages/history?senderId=${senderId}&receiverId=${receiverId}`).then(response => response.data);
}

export const sendChat = async (messageContains) => {
    return await axiosInstance.post(`/api/messages/send`, messageContains).then((response) => response.data);
}

export const deleteUser = async (id) => {
    return await axiosInstance.delete(`/api/v1/user/deleteUser/${id}`).then(response => response.data);
}

export const uploadProfilePicture = async (userId, file) => {
    const formData = new FormData();
    formData.append("file", file);

    return await axiosInstance.post(`/api/v1/user/setProfile/${userId}`, formData, {
        headers: {
            "Content-Type": "multipart/form-data"
        }
    }).then(response => response.data);
};

export const removeProfilePicture = async (userId) => {
    return await axiosInstance.delete(`/api/v1/user/remove/profile/${userId}`).then(response => response.data);
}
