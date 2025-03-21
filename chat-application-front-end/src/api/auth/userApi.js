import {axiosInstance} from "../../config/axios-config";

export const callAllUser = async (searchkey, pageNumber, pageSize) => {
    return await axiosInstance.get(`/api/v1/user/getAllUser?pageNo=${pageNumber}&pageSize=${pageSize}&searchKey=${searchkey}`).then(response => response.data)
}

export const getChatHistory = async (senderId,receiverId) => {
    return await axiosInstance.get(`/api/messages/history?senderId=${senderId}&receiverId=${receiverId}`).then(response => response.data);
}