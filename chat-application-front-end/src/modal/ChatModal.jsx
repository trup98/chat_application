import React, { useState, useEffect, useRef } from "react";
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
import { getChatHistory, sendChat } from "../api/auth/userApi";
import { getTokenFromCookie, getUserId } from "../config/Cookie-store";
import { Client } from "@stomp/stompjs";

const ChatModal = ({ open, onClose, user }) => {
    const [message, setMessage] = useState("");
    const [messages, setMessages] = useState([]);
    const [socket, setSocket] = useState(null);
    const messagesEndRef = useRef(null);

    const senderId = parseInt(getUserId("userId"), 10);
    const receiverId = user?.id ? parseInt(user.id, 10) : null;

    // Scroll to bottom when messages update
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    useEffect(() => {
        if (open && senderId && receiverId) {
            fetchChatHistory();
            // Only initialize the socket if it's not already active
            if (!socket) {
                initializeSocket();
            }
        }

        // Cleanup: deactivate the socket when the modal is closed
        return () => {
            if (socket) {
                socket.deactivate();
                setSocket(null);
            }
        };
    }, [open, senderId, receiverId]);

    const fetchChatHistory = async () => {
        try {
            const response = await getChatHistory(senderId, receiverId);
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

    const initializeSocket = () => {
        const token = getTokenFromCookie("token");

        const stompClient = new Client({
            brokerURL: `ws://localhost:9050/ws/websocket?token=${token}`,
            debug: (str) => console.log(str),
            reconnectDelay: 5000,
        });

        stompClient.onConnect = () => {
            // Subscribe to personal messages
            stompClient.subscribe(`/user/${senderId}/queue/messages`, (message) => {
                const newMessage = JSON.parse(message.body);
                setMessages((prevMessages) => [...prevMessages, newMessage]);
            });
        };

        stompClient.onStompError = (frame) => {
            console.error("❌ WebSocket Error:", frame);
        };

        stompClient.activate();
        setSocket(stompClient);
    };

    // Send message via REST API only.
    // The backend will persist the message and then push it to the recipient via WebSocket.
    const handleSendButton = async () => {
        if (!message.trim()) return;

        const messageBody = {
            senderId,
            receiverId,
            content: message.trim(),
        };

        try {
            const response = await sendChat(messageBody);
            if (response.status === 200) {
                setMessages((prevMessages) => [...prevMessages, messageBody]);
                // We do not call socket.publish here to avoid duplication.
                // The backend sends the message via messagingTemplate.
                setMessage("");
            }
        } catch (error) {
            console.error("Error sending message:", error);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <Box sx={{ backgroundColor: "#222", color: "#fff" }}>
                <DialogTitle sx={{ backgroundColor: "#333", color: "#fff", textAlign: "center" }}>
                    Chat with {user?.userName}
                </DialogTitle>

                <DialogContent dividers sx={{ backgroundColor: "#222", minHeight: 300, maxHeight: 400 }}>
                    <Box
                        sx={{
                            height: 300,
                            overflowY: "auto",
                            padding: 2,
                            backgroundColor: "#1e1e1e",
                            borderRadius: 2,
                            display: "flex",
                            flexDirection: "column"
                        }}
                    >
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
                        <div ref={messagesEndRef} />
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
                    <Button color="primary" variant="contained" onClick={handleSendButton}>Send</Button>
                </DialogActions>
            </Box>
        </Dialog>
    );
};

export default ChatModal;
