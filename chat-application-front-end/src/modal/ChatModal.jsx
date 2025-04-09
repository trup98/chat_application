import React, {useState, useEffect, useRef} from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Box,
    Typography,
    IconButton,
    Tooltip
} from "@mui/material";
import {
    getChatHistory,
    sendChat,
    unSendMessage,
    editMessage
} from "../api/auth/userApi";
import {getTokenFromCookie, getUserId} from "../config/Cookie-store";
import {Client} from "@stomp/stompjs";
import {
    getGroupChatHistory,
    getGroupMembers,
    sendMessageInGroup
} from "../api/auth/groupApi";
import dayjs from "dayjs";
import AddIcon from "@mui/icons-material/Add";
import InfoIcon from "@mui/icons-material/Info";
import MembersModal from "./MembersModal";
import AddMembersModal from "./AddMembersModal";
import EmojiPicker from 'emoji-picker-react';

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

    const [editingMessageId, setEditingMessageId] = useState(null);
    const [editedContent, setEditedContent] = useState("");

    const [showEmojiPicker, setShowEmojiPicker] = useState(false);
    const emojiPickerRef = useRef(null);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({behavior: "smooth"});
    }, [messages]);

    useEffect(() => {
        if (open) {
            fetchChatHistory();
            initializeSocket();
        } else {
            closeSocket();
        }

        return () => {
            closeSocket();
        };
    }, [open, senderId, receiverId, group]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (emojiPickerRef.current && !emojiPickerRef.current.contains(event.target)) {
                setShowEmojiPicker(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

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
            closeSocket();
        }
        const token = getTokenFromCookie("token");

        const stompClient = new Client({
            brokerURL: `ws://192.168.10.131:9050/ws/websocket?token=${token}`,
            debug: (str) => console.log(str),
            reconnectDelay: 5000,
        });

        stompClient.onConnect = () => {
            console.log("✅ Connected to WebSocket");
            stompClient.subscribe(`/user/${senderId}/queue/messages`, (message) => {
                const newMessage = JSON.parse(message.body);
                setMessages((prev) => [...prev, newMessage]);
            });
            if (group) {
                stompClient.subscribe(`/topic/group/${group}`, (message) => {
                    const newGroupMessage = JSON.parse(message.body);
                    setMessages((prev) => [...prev, newGroupMessage]);
                });
            }
        };

        stompClient.onStompError = (frame) => {
            console.error("❌ WebSocket Error:", frame);
        };

        stompClient.activate();
        stompClientRef.current = stompClient;
    };

    const handleSendButton = async () => {
        if (!message.trim()) return;

        try {
            let response;
            let newMessage;

            if (group) {
                newMessage = {groupId: group, senderId, content: message.trim()};
                response = await sendMessageInGroup(newMessage);
            } else {
                newMessage = {senderId, receiverId, content: message.trim()};
                response = await sendChat(newMessage);
            }

            if (response.status === 200) {
                setMessages((prev) => [...prev, response.data]);
                setMessage("");
            }
        } catch (error) {
            console.error("Error sending message:", error);
        }
    };

    const handleUnSendMessage = async (messageId) => {
        try {
            const response = await unSendMessage(senderId, messageId);
            if (response.status === 200) {
                setMessages((prev) => prev.filter((m) => m.id !== messageId));
            } else {
                console.error("Failed to unsend message");
            }
        } catch (err) {
            console.error("Error unsending message:", err);
        }
    };

    const handleSaveEdit = async (messageId) => {
        try {
            const payload = {
                senderId,
                messageId,
                newMessageContent: editedContent,
            };

            const response = await editMessage(payload);

            if (response.status === 200) {
                setMessages((prevMessages) =>
                    prevMessages.map((msg) =>
                        msg.id === messageId
                            ? {...msg, content: editedContent, isEdited: true}
                            : msg
                    )
                );
                setEditingMessageId(null);
                setEditedContent("");
            }
        } catch (error) {
            console.error("Error editing message:", error);
        }
    };

    const handleInfoIconClick = async () => {
        if (group) {
            try {
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
                    <DialogTitle sx={{backgroundColor: "#333", display: "flex", justifyContent: "space-between"}}>
                        <Box sx={{textAlign: "center", flexGrow: 1}}>
                            {group ? "Group Chat" : `Chat with ${user?.userName}`}
                        </Box>
                        {group && (
                            <>
                                <IconButton sx={{color: "#fff"}} onClick={handleInfoIconClick}>
                                    <InfoIcon/>
                                </IconButton>
                                <IconButton sx={{color: "#fff"}} onClick={() => setAddMembersModalOpen(true)}>
                                    <AddIcon/>
                                </IconButton>
                            </>
                        )}
                    </DialogTitle>

                    <DialogContent dividers sx={{minHeight: 300, maxHeight: 400}}>
                        <Box sx={{
                            height: 300,
                            overflowY: "auto",
                            padding: 2,
                            display: "flex",
                            flexDirection: "column"
                        }}>
                            {messages.length > 0 ? (
                                messages.map((msg, index) => (
                                    <Box
                                        key={index}
                                        sx={{
                                            padding: 1,
                                            mb: 1,
                                            backgroundColor: msg.senderId === senderId ? "#ECE5DD" : "#DCF8C6",
                                            color: "black",
                                            borderRadius: 2,
                                            maxWidth: "70%",
                                            alignSelf: msg.senderId === senderId ? "flex-end" : "flex-start",
                                            position: "relative"
                                        }}
                                    >
                                        {group && (
                                            <Typography variant="caption" sx={{color: "black"}}>
                                                {msg.senderName}:
                                            </Typography>
                                        )}

                                        {editingMessageId === msg.id ? (
                                            <Box>
                                                <TextField
                                                    fullWidth
                                                    size="small"
                                                    value={editedContent}
                                                    onChange={(e) => setEditedContent(e.target.value)}
                                                    sx={{backgroundColor: "#fff", borderRadius: 1}}
                                                />
                                                <Box sx={{display: "flex", justifyContent: "flex-end", mt: 1}}>
                                                    <Button
                                                        size="small"
                                                        variant="contained"
                                                        onClick={() => handleSaveEdit(msg.id)}
                                                        sx={{mr: 1}}
                                                    >
                                                        Save
                                                    </Button>
                                                    <Button
                                                        size="small"
                                                        variant="outlined"
                                                        onClick={() => setEditingMessageId(null)}
                                                    >
                                                        Cancel
                                                    </Button>
                                                </Box>
                                            </Box>
                                        ) : (
                                            <>
                                                <Typography>{msg.content}</Typography>
                                                {msg.isEdited && (
                                                    <Typography variant="caption" sx={{color: "gray"}}>
                                                        (edited)
                                                    </Typography>
                                                )}
                                            </>
                                        )}

                                        <Typography variant="caption" sx={{color: "black", fontSize: "0.75rem"}}>
                                            {dayjs(msg.timestamp).format("MMM D, YYYY h:mm A")}
                                        </Typography>

                                        {msg.senderId === senderId && editingMessageId !== msg.id && (
                                            <Box sx={{display: "flex", justifyContent: "flex-end", mt: 0.5}}>
                                                <Tooltip title="Edit Message" arrow>
                                                    <IconButton
                                                        size="small"
                                                        sx={{
                                                            color: "#333",
                                                            backgroundColor: "#fff",
                                                            mx: 0.5,
                                                            '&:hover': {backgroundColor: "#eee"}
                                                        }}
                                                        onClick={() => {
                                                            setEditingMessageId(msg.id);
                                                            setEditedContent(msg.content);
                                                        }}
                                                    >
                                                        ✏️
                                                    </IconButton>
                                                </Tooltip>
                                                <Tooltip title="Unsend Message" arrow>
                                                    <IconButton
                                                        size="small"
                                                        sx={{
                                                            color: "#333",
                                                            backgroundColor: "#fff",
                                                            mx: 0.5,
                                                            '&:hover': {backgroundColor: "#eee"}
                                                        }}
                                                        onClick={() => handleUnSendMessage(msg.id)}
                                                    >
                                                        🗑️
                                                    </IconButton>
                                                </Tooltip>
                                            </Box>
                                        )}
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

                    <Box sx={{padding: 2, position: "relative"}}>
                        <Box sx={{display: "flex", alignItems: "center"}}>
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
                            <IconButton
                                onClick={() => setShowEmojiPicker((prev) => !prev)}
                                sx={{color: "#fff", ml: 1}}
                            >
                                +
                            </IconButton>
                        </Box>

                        {showEmojiPicker && (
                            <Box
                                ref={emojiPickerRef}
                                sx={{
                                    position: "absolute",
                                    bottom: 65,
                                    right: 10,
                                    zIndex: 999
                                }}
                            >
                                <EmojiPicker
                                    onEmojiClick={(emojiData) => {
                                        setMessage((prev) => prev + emojiData.emoji);
                                    }}
                                    theme="dark"
                                />
                            </Box>
                        )}
                    </Box>

                    <DialogActions sx={{backgroundColor: "#333"}}>
                        <Button onClick={onClose} sx={{color: "#fff"}}>Close Chat</Button>
                        <Button color="primary" variant="contained" onClick={handleSendButton}>Send</Button>
                    </DialogActions>
                </Box>
            </Dialog>

            <MembersModal
                members={groupMembers}
                onClose={() => setMembersModalOpen(false)}
                open={membersModalOpen}
                groupId={group}
                refreshGroupMembers={handleInfoIconClick}
            />
            {addMembersModalOpen && <AddMembersModal
                open={addMembersModalOpen}
                onClose={() => setAddMembersModalOpen(false)}
                groupId={group}
                refreshGroupMembers={handleInfoIconClick}
            />}
        </>
    );
};

export default ChatModal;
