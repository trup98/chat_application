import React, { useState, useEffect } from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Box,
    Typography
} from "@mui/material";
import { getChatHistory } from "../api/auth/userApi";
import { getUserId } from "../config/Cookie-store";

const ChatModal = ({ open, onClose, user }) => {
    const [message, setMessage] = useState("");
    const [messages, setMessages] = useState([]);

    const senderId = parseInt(getUserId("userId"), 10);
    const receiverId = user?.id ? parseInt(user.id, 10) : null;

    useEffect(() => {
        if (open && senderId && receiverId) {
            fetchChatHistory();
        }
    }, [open, senderId, receiverId]);

    const fetchChatHistory = async () => {
        try {
            const response = await getChatHistory(senderId, receiverId);
            console.log("Chat History Response:", response.data);

            if (response.status === 200 && Array.isArray(response.data)) {
                setMessages(response.data);
            } else {
                setMessages([]);
            }
        } catch (error) {
            console.error("Error fetching chat history:", error);
            setMessages([]);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <Box sx={{ backgroundColor: "#222", color: "#fff" }}>
                <DialogTitle sx={{ backgroundColor: "#333", color: "#fff", textAlign: "center" }}>
                    Chat with {user?.userName}
                </DialogTitle>

                <DialogContent dividers sx={{ backgroundColor: "#222", minHeight: 300, maxHeight: 400 }}>
                    <Box sx={{ height: 300, overflowY: "auto", padding: 2, backgroundColor: "#1e1e1e", borderRadius: 2, display: "flex", flexDirection: "column" }}>
                        {messages.length > 0 ? (
                            messages.map((msg, index) => (
                                <Box
                                    key={index}
                                    sx={{
                                        padding: 1,
                                        marginBottom: 1,
                                        backgroundColor: msg.senderId === senderId ? "#007bff" : "#555",
                                        color: "#fff",
                                        borderRadius: 2,
                                        maxWidth: "70%",
                                        alignSelf: msg.senderId === senderId ? "flex-end" : "flex-start",
                                    }}
                                >
                                    <Typography>{msg.content}</Typography>
                                </Box>
                            ))
                        ) : (
                            <Typography sx={{ color: "#aaa", textAlign: "center" }}>
                                Start chatting with {user?.userName}...
                            </Typography>
                        )}
                    </Box>
                </DialogContent>

                <Box sx={{ backgroundColor: "#222", padding: 2 }}>
                    <TextField
                        fullWidth
                        placeholder="Type a message..."
                        variant="outlined"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        sx={{
                            backgroundColor: "#333",
                            input: { color: "#fff" },
                            fieldset: { borderColor: "#555" }
                        }}
                    />
                </Box>

                <DialogActions sx={{ backgroundColor: "#333" }}>
                    <Button onClick={onClose} sx={{ color: "#fff" }}>Close Chat</Button>
                    <Button color="primary" variant="contained">Send</Button>
                </DialogActions>
            </Box>
        </Dialog>
    );
};

export default ChatModal;
