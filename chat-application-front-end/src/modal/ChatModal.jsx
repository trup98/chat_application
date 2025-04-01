import React, {useState, useEffect, useRef} from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Box,
    Typography, IconButton
} from "@mui/material";
import {getChatHistory, sendChat} from "../api/auth/userApi";
import {getTokenFromCookie, getUserId} from "../config/Cookie-store";
import {Client} from "@stomp/stompjs";
import {getGroupChatHistory, getGroupMembers, sendMessageInGroup} from "../api/auth/groupApi";
import dayjs from "dayjs";
import AddIcon from "@mui/icons-material/Add";
import InfoIcon from '@mui/icons-material/Info';
import MembersModal from "./MembersModal";
import AddMembersModal from "./AddMembersModal";

const ChatModal = ({open, onClose, user, group}) => {
    const [message, setMessage] = useState("");
    const [messages, setMessages] = useState([]);
    const messagesEndRef = useRef(null);
    const stompClientRef = useRef(null);
    const senderId = parseInt(getUserId("userId"), 10);
    const receiverId = user?.id ? parseInt(user.id, 10) : null;
    const [groupMembers, setGroupMembers] = useState([]);
    const [membersModalOpen, setMembersModalOpen] = useState(false);
    const [addMembersModalOpen, setAddMembersModalOpen] = useState(false);

    // Scroll to bottom when messages update
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({behavior: "smooth"});
    }, [messages]);

    useEffect(() => {
        if (open) {
            fetchChatHistory();
            initializeSocket();
        } else {
            closeSocket(); // Close socket when modal is closed
        }

        return () => {
            closeSocket(); // Ensure socket is cleaned up
        };
    }, [open, senderId, receiverId, group]);

    const closeSocket = () => {
        if (stompClientRef.current) {
            stompClientRef.current.deactivate();
            stompClientRef.current = null;
        }
    };

    const fetchChatHistory = async () => {
        try {
            let response;
            if (group) {
                response = await getGroupChatHistory(group);
            } else {
                response = await getChatHistory(senderId, receiverId);
            }

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
        if (stompClientRef.current) {
            closeSocket(); // Close existing connection before opening a new one
        }
        const token = getTokenFromCookie("token");

        const stompClient = new Client({
            brokerURL: `ws://192.168.10.131:9050/ws/websocket?token=${token}`,
            debug: (str) => console.log(str),
            reconnectDelay: 5000,
        });

        stompClient.onConnect = () => {
            console.log("✅ Connected to WebSocket");
            // Subscribe to personal messages
            stompClient.subscribe(`/user/${senderId}/queue/messages`, (message) => {
                const newMessage = JSON.parse(message.body);
                setMessages((prevMessages) => [...prevMessages, newMessage]);
            });
            // Subscribe to group messages if chatting in a group
            if (group) {
                stompClient.subscribe(`/topic/group/${group}`, (message) => {
                    const newGroupMessage = JSON.parse(message.body);
                    setMessages((prevMessages) => [...prevMessages, newGroupMessage]);
                });
            }
        };

        stompClient.onStompError = (frame) => {
            console.error("❌ WebSocket Error:", frame);
        };

        stompClient.activate();
        stompClientRef.current = stompClient;
    };
    // Send message via REST API only.
    // The backend will persist the message and then push it to the recipient via WebSocket.
    const handleSendButton = async () => {
        if (!message.trim()) return;


        try {
            let response;
            let newMessage;

            if (group) {
                newMessage = {groupId: group, senderId, content: message.trim()};
                response = await sendMessageInGroup(newMessage);
                if (response.status === 200) {
                    setMessage("");
                }
            } else {
                newMessage = {senderId, receiverId, content: message.trim()};
                response = await sendChat(newMessage);
                if (response.status === 200) {
                    setMessages((prevMessages) => [...prevMessages, newMessage]);
                    setMessage("");
                }
            }
        } catch (error) {
            console.error("Error sending message:", error);
        }
    };

    const handleInfoIconClick = async () => {
        if (group) {
            try {
                console.log("group>>>>>", group)
                const response = await getGroupMembers(group);
                if (response.status === 200) {
                    setGroupMembers(response.data);
                    setMembersModalOpen(true);
                }

            } catch (error) {
                console.error("Error fetching group members:", error);
            }
        }
    };

    return (
        <>
            <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
                <Box sx={{backgroundColor: "#222", color: "#fff"}}>
                    <Box>
                        <DialogTitle
                            sx={{
                                backgroundColor: "#333",
                                color: "#fff",
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "space-between",
                                padding: "8px 16px",
                            }}
                        >
                            <Box sx={{flexGrow: 1, textAlign: "center"}}>
                                {group ? "Group Chat" : `Chat with ${user?.userName}`}
                            </Box>

                            {group && (
                                <>
                                    <IconButton sx={{color: "#fff"}} onClick={handleInfoIconClick}>
                                        <InfoIcon/>
                                    </IconButton>
                                    <IconButton sx={{ color: "#fff" }} onClick={() => setAddMembersModalOpen(true)}>
                                        <AddIcon />
                                    </IconButton>
                                </>
                            )}
                        </DialogTitle>
                    </Box>


                    <DialogContent dividers sx={{backgroundColor: "#222", minHeight: 300, maxHeight: 400}}>
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
                                            backgroundColor: msg.senderId === senderId ? "#ECE5DD" : "#DCF8C6",
                                            color: "black",
                                            borderRadius: 2,
                                            maxWidth: "70%",
                                            alignSelf: msg.senderId === senderId ? "flex-end" : "flex-start",
                                        }}
                                    >
                                        {group && (
                                            <Typography variant="caption" sx={{color: "black"}}>
                                                {msg.senderName}:
                                            </Typography>
                                        )}
                                        <Typography>{msg.content}</Typography>
                                        <Typography variant="caption"
                                                    sx={{color: "black", fontSize: "0.75rem", textAlign: "right"}}>
                                            {dayjs(msg.timestamp).format("MMM D, YYYY h:mm A")}
                                        </Typography>
                                    </Box>
                                ))
                            ) : (
                                <Typography sx={{color: "#aaa", textAlign: "center"}}>
                                    Start chatting with {user?.userName}...
                                </Typography>
                            )}
                            <div ref={messagesEndRef}/>
                        </Box>
                    </DialogContent>

                    <Box sx={{backgroundColor: "#222", padding: 2}}>
                        <TextField
                            fullWidth
                            placeholder="Type a message..."
                            variant="outlined"
                            value={message}
                            onChange={(e) => setMessage(e.target.value)}
                            sx={{
                                backgroundColor: "#333",
                                input: {color: "#fff"},
                                fieldset: {borderColor: "#555"}
                            }}
                        />
                    </Box>

                    <DialogActions sx={{backgroundColor: "#333"}}>
                        <Button onClick={onClose} sx={{color: "#fff"}}>Close Chat</Button>
                        <Button color="primary" variant="contained" onClick={handleSendButton}>Send</Button>
                    </DialogActions>
                </Box>
            </Dialog>

            {/* Show Group Members Modal */}
            <MembersModal
                members={groupMembers}
                onClose={() => setMembersModalOpen(false)}
                open={membersModalOpen}
                groupId={group}
                refreshGroupMembers={handleInfoIconClick}
            />
            <AddMembersModal
                open={addMembersModalOpen}
                onClose={() => setAddMembersModalOpen(false)}
                groupId={group}
                refreshGroupMembers={handleInfoIconClick}
            />
        </>


    );
};

export default ChatModal;
