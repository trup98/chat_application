import React, {useState, useEffect} from "react";
import {
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    IconButton,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    Box,
    Typography,
    TextField,
    Tabs,
    Tab, Pagination, Avatar, Menu, MenuItem
} from "@mui/material";
import {useNavigate} from "react-router-dom";
import {toast, Zoom} from "react-toastify";
import {
    callAllUser,
    deleteConversation,
    deleteUser, getUnreadCount, markMessageRead,
    removeProfilePicture,
    uploadProfilePicture
} from "../api/auth/userApi";
import DensityMediumIcon from "@mui/icons-material/DensityMedium";
import ChatModal from "../modal/ChatModal";
import {getUserId, getUserName, removeUserSession} from "../config/Cookie-store";
import LogoutModal from "./auth/LogoutModal";
import {callAllGroups, changeProfilePicture, deleteGroup, removeProfilePictureInGroup} from "../api/auth/groupApi";
import AddIcon from "@mui/icons-material/Add";
import CreateGroupModal from "../modal/CreateGroupModal";
import VisibilityIcon from "@mui/icons-material/Visibility";
import DeleteIcon from "@mui/icons-material/Delete";
import PhotoCameraIcon from "@mui/icons-material/PhotoCamera";
import ClearAllIcon from '@mui/icons-material/ClearAll';
import Tooltip from '@mui/material/Tooltip';
import {
    getCachedProfileUrl,
    saveProfileUrlToCache,
    cleanupProfileCache
} from "../utils/profileCache";


const Dashboard = () => {
    const navigate = useNavigate();
    const [users, setUsers] = useState([]);
    const [groups, setGroups] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize] = useState(10);
    const [value, setValues] = useState("");
    const [openModal, setOpenModal] = useState(false);
    const [chatOpen, setChatOpen] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [logoutOpen, setLogoutOpen] = useState(false);
    const [tabIndex, setTabIndex] = useState(0);
    const senderId = parseInt(getUserId("userId"), 10);
    const [openGroupModal, setOpenGroupModal] = useState(false);
    const [isGroup, setIsGroup] = useState(false);
    const [anchorEl, setAnchorEl] = useState(null);
    const [selectedProfile, setSelectedProfile] = useState(null);
    const [openDialog, setOpenDialog] = useState(false);
    const [selectedUserId, setSelectedUserId] = useState(null);
    const [deleteChatUser, setDeleteChatUser] = useState(null);
    const [openDeleteChatDialog, setOpenDeleteChatDialog] = useState(false);
    const [unreadCounts, setUnreadCounts] = useState({});

    useEffect(() => {
        cleanupProfileCache();
    }, []);

    // Fetch Users
    const getAllUsers = async (currentPage) => {
        try {
            const response = await callAllUser(value, value ? 0 : currentPage, pageSize, senderId);
            if (response.status === 200) {
                const users = response.data.content;

                // Apply cached URLs or cache fresh ones
                const updatedUsers = users.map(user => {
                    let cachedUrl = getCachedProfileUrl(user.id);

                    // If not cached and S3 URL exists, cache it
                    if (!cachedUrl && user.userProfileS3Link) {
                        cachedUrl = user.userProfileS3Link;
                        saveProfileUrlToCache(user.id, cachedUrl);
                    }

                    return {
                        ...user,
                        userProfileS3Link: cachedUrl || user.userProfileS3Link
                    };
                });

                setUsers(updatedUsers);
                setTotalPages(response.data.totalPages);

                // Fetch unread count
                const unreadRes = await getUnreadCount(senderId);
                const unreadList = unreadRes.data.data;

                const countMap = {};
                unreadList.forEach(({senderId, unreadCount}) => {
                    countMap[senderId] = unreadCount;
                });

                setUnreadCounts(countMap);
            }
        } catch (error) {
            if (error?.status === 403) {
                toast.error(error.message, {transition: Zoom});
                setUsers([]);
                handleLogout();
            }
        }
    };

    // Fetch Groups
    const getAllGroups = () => {
        callAllGroups(senderId)
            .then((response) => {
                if (response.status === 200) {
                    setGroups(response.data);
                    setTotalPages(response.data.totalPages);
                }
            })
            .catch((error) => {
                if (error?.status === 403) {
                    toast.error(error.message, {transition: Zoom});
                    setUsers([]);
                    handleLogout();
                }
            });
    };

    // Call appropriate API on tab change
    useEffect(() => {
        if (tabIndex === 0) {
            getAllUsers(currentPage);
        } else {
            getAllGroups();
        }
    }, [currentPage, value, tabIndex]);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    const handleTabChange = (event, newValue) => {
        setTabIndex(newValue);
        setCurrentPage(0);
    };

    const handleCloseModal = () => {
        setOpenModal(false);
        setSelectedUser(null);
    };

    const handleOpenModal = (item) => {
        setSelectedUser(item);
        setIsGroup(!!item.memberCount);
        setOpenModal(true);
    };

    const handleStartChatModal = (item) => {
        setSelectedUser(item);
        setIsGroup(item.memberCount ? item.id : null);
        setChatOpen(true);
        if (tabIndex === 0) {
            // This is a one-on-one chat
            const receiverId = senderId;      // Current logged-in user
            const sender = item.id;           // Person clicked

            markMessagesAsRead(sender, receiverId);
        }
    };

    const markMessagesAsRead = async (senderId, receiverId) => {
        try {
            await markMessageRead(senderId, receiverId);
        } catch (error) {
            console.error("Error marking messages as read", error);
        }
    };

    const handleOpenLogout = () => {
        setLogoutOpen(true);
    };

    const handleCloseLogout = () => {
        setLogoutOpen(false);
    };

    const handleLogout = () => {
        removeUserSession();
        navigate("/");
    };

    const handleCreateModalClose = () => {
        setOpenGroupModal(false);
        getAllGroups();
    }

    const handleDelete = async () => {
        if (!selectedUser?.id) return;

        try {
            if (isGroup) {
                const response = await deleteGroup(selectedUser.id, senderId);
                if (response.status === 200) {
                    toast.success("Group deleted successfully!", {transition: Zoom});
                    getAllGroups();
                }
            } else {
                const userToDelete = selectedUser.id;
                const response = await deleteUser(userToDelete);
                if (response.status === 200) {
                    toast.success(response.data.message, {
                        transition: "Zoom"
                    })
                    getAllUsers(currentPage);
                }
            }
            setOpenModal(false);
        } catch (error) {
            toast.error(error.data?.message || "Failed to delete", {transition: Zoom});
            setOpenModal(false);
        }
    };

    const handleChatModalClose = () => {
        setChatOpen(false);
        getAllGroups();
        getAllUsers(currentPage);
    }

    const handleAvatarClick = (event, item) => {
        setAnchorEl(event.currentTarget);
        setSelectedProfile(tabIndex === 0 ? item.userProfileS3Link : item.groupImage);
        setSelectedUserId(item.id);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleViewProfile = () => {
        setOpenDialog(true);
        handleClose();
    };

    const handleDeleteProfile = async () => {
        try {
            let response;
            if (tabIndex === 1) {
                response = await removeProfilePictureInGroup(selectedUserId)
            } else {
                response = await removeProfilePicture(selectedUserId);
            }
            if (response.status === 200) {
                getAllUsers(currentPage);
                getAllGroups();
                toast.success(response.message)
            }

        } catch (error) {
            toast.error("Failed to delete profile picture.");
        }
        handleClose();
    };

    const handleChangeProfile = () => {
        document.getElementById("fileInput").click();
        handleClose();
    };

    const handleFileUpload = async (event) => {

        const file = event.target.files[0];

        if (file && selectedUserId) {

            try {
                let response;
                if (tabIndex === 1) {
                    response = await changeProfilePicture(selectedUserId, file);
                } else {
                    response = await uploadProfilePicture(selectedUserId, file);
                }
                if (response.status === 200) {
                    getAllUsers(currentPage);
                    getAllGroups();
                    toast.success(response.message);
                }

            } catch (error) {
                console.error("Error uploading file:", error);
                toast.error("Failed to update profile picture.");
            }
        } else {
            toast.error("No file selected or user ID missing.");
        }
    };

    const handleDeleteChatHistory = (user) => {
        setDeleteChatUser(user);
        setOpenDeleteChatDialog(true);
    };

    const confirmDeleteChatHistory = async () => {
        try {
            const response = await deleteConversation(senderId, deleteChatUser.id);
            if (response.status === 200) {
                toast.success(`Chat history with ${deleteChatUser.userName} deleted successfully!`);
            }
            setOpenDeleteChatDialog(false);
        } catch (error) {
            toast.error("Failed to delete chat history.");
            setOpenDeleteChatDialog(false);
        }
    };


    return (
        <Box sx={{position: "relative", backgroundColor: "#000", minHeight: "100vh", padding: 2}}>
            <Box sx={{position: "absolute", top: 16, right: 16}}>
                <Typography
                    sx={{
                        color: "#fff",
                        fontWeight: "bold",
                        cursor: "pointer",
                        transition: "color 0.3s ease",
                        "&:hover": {color: "#1e90ff"},
                    }}
                    onClick={handleOpenLogout}
                >
                    Welcome {getUserName("userName")}
                </Typography>
            </Box>

            <TableContainer component={Paper} sx={{
                backgroundColor: "#222",
                color: "#fff",
                borderRadius: 2,
                minWidth: 1000,
                maxWidth: "95vw",
                width: "95%",
                padding: 2,
                margin: "0 auto",
                marginTop: 20
            }}>
                <Box sx={{display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 2}}>
                    {/* Left: Tabs */}
                    <Tabs
                        value={tabIndex}
                        onChange={handleTabChange}
                        textColor="inherit"
                        indicatorColor="primary"
                        sx={{
                            "& .MuiTab-root": {
                                color: "#fff",
                                textTransform: "none",
                                fontSize: "16px",
                                "&.Mui-selected": {color: "#007bff"},
                            }
                        }}
                    >
                        <Tab label="Chats"/>
                        <Tab label="Groups"/>
                    </Tabs>

                    {/*{add button}*/}
                    <Box sx={{display: "flex", alignItems: "center"}}>
                        {tabIndex === 1 && (
                            <IconButton onClick={() => setOpenGroupModal(true)} sx={{color: "#fff", marginLeft: 2}}>
                                <AddIcon/>
                            </IconButton>
                        )}
                        <TextField
                            variant="outlined"
                            placeholder="Search..."
                            value={value}
                            onChange={(e) => setValues(e.target.value)}
                            sx={{
                                backgroundColor: "#333",
                                input: {color: "#fff"},
                                fieldset: {borderColor: "#555"},
                                "& .MuiOutlinedInput-root": {
                                    "& fieldset": {borderColor: "#555"},
                                    "&:hover fieldset": {borderColor: "#777"},
                                    "&.Mui-focused fieldset": {borderColor: "#007bff"},
                                },
                                minWidth: 200
                            }}
                            InputProps={{
                                sx: {height: "35px", minHeight: "35px"}
                            }}
                        />
                    </Box>
                </Box>

                <Table sx={{minWidth: 1000, width: "100%"}} aria-label="table">
                    <TableHead>
                        <TableRow sx={{backgroundColor: "#333"}}>
                            <TableCell sx={{color: "#fff", fontWeight: "bold"}} align="left">Index</TableCell>
                            <TableCell sx={{color: "#fff", fontWeight: "bold"}} align="left">
                                {tabIndex === 0 ? "User Name" : "Group Name"}
                            </TableCell>
                            <TableCell sx={{color: "#fff", fontWeight: "bold"}} align="left">
                                {tabIndex === 0 ? "Email" : "Members"}
                            </TableCell>
                            <TableCell sx={{color: "#fff", fontWeight: "bold"}} align="right">Action</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {(tabIndex === 0 ? users : groups)?.length > 0 ? (
                            (tabIndex === 0 ? users : groups).map((item, index) => (
                                <TableRow key={item.id} sx={{
                                    '&:nth-of-type(odd)': {backgroundColor: "#2a2a2a"},
                                    '&:nth-of-type(even)': {backgroundColor: "#1e1e1e"},
                                    '&:hover': {backgroundColor: "#444"}
                                }}>

                                    <TableCell sx={{color: "#fff"}} align="left">{index + 1}</TableCell>

                                    {/* Profile Picture + Name in One Line */}
                                    <TableCell sx={{color: "#fff", display: "flex", alignItems: "center", gap: "8px"}}
                                               align="left">
                                        {/* Profile Picture */}
                                        <Avatar
                                            src={tabIndex === 0 ? item.userProfileS3Link : item.groupImage}
                                            alt={tabIndex === 0 ? item.userName : item.name}
                                            sx={{
                                                width: 40,
                                                height: 40,
                                                bgcolor: "#555",
                                                color: "#fff",
                                                cursor: "pointer"
                                            }}
                                            onClick={(e) => handleAvatarClick(e, item)}
                                        >
                                            {(!item.userProfileS3Link && tabIndex === 0) ? item.userName.charAt(0).toUpperCase() : ""}
                                            {(!item.groupImage && tabIndex === 1) ? item.name.charAt(0).toUpperCase() : ""}
                                        </Avatar>

                                        {/* Name and Unread message*/}
                                        <Box
                                            sx={{
                                                display: "flex",
                                                alignItems: "center",
                                                cursor: "pointer",
                                            }}
                                            onClick={() => handleStartChatModal(item)}
                                        >
                                            <Typography
                                                sx={{
                                                    fontWeight: tabIndex === 0 && unreadCounts[item.id] > 0 ? "bold" : "normal",
                                                    color: "#fff",
                                                }}
                                            >
                                                {tabIndex === 0 ? item.userName : item.name}
                                            </Typography>

                                            {tabIndex === 0 && unreadCounts[item.id] > 0 && (
                                                <Box
                                                    sx={{
                                                        backgroundColor: "#25D366",
                                                        color: "#fff",
                                                        fontSize: "12px",
                                                        fontWeight: "bold",
                                                        minWidth: 20,
                                                        height: 20,
                                                        borderRadius: "50%",
                                                        display: "flex",
                                                        alignItems: "center",
                                                        justifyContent: "center",
                                                        marginLeft: 1,
                                                        paddingX: 1,
                                                    }}
                                                >
                                                    {unreadCounts[item.id]}
                                                </Box>
                                            )}
                                        </Box>


                                    </TableCell>

                                    {/* Email or Member Count */}
                                    <TableCell sx={{color: "#fff"}} align="left">
                                        {tabIndex === 0 ? item.email : item.memberCount ?? 0}
                                    </TableCell>

                                    {/* Action and Delete conversation Button */}
                                    <TableCell align="right">
                                        <Tooltip title="Clear Chat" arrow>
                                            <IconButton
                                                onClick={() => handleDeleteChatHistory(item)}
                                                sx={{color: "#ff4d4f", marginRight: 1}}
                                            >
                                                <ClearAllIcon/>
                                            </IconButton>
                                        </Tooltip>
                                        <IconButton onClick={() => handleOpenModal(item)}>
                                            <DensityMediumIcon sx={{color: "#fff"}}/>
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={4} sx={{color: "#fff", textAlign: "center"}}>
                                    No {tabIndex === 0 ? "users" : "groups"} found
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>

                <div className="d-flex justify-content-center">
                    <Pagination>
                        {[...Array(users.totalPages)].map((_, index) => (
                            <Pagination.Item
                                key={index}
                                active={index === users.pageNumber}
                                onClick={() => handlePageChange(index)}
                            >
                                {index + 1}
                            </Pagination.Item>
                        ))}
                    </Pagination>
                </div>
            </TableContainer>

            {/* Conditional Dialog */}
            <Dialog open={openModal} onClose={handleCloseModal}
                    sx={{"& .MuiPaper-root": {backgroundColor: "#222", color: "#fff"}}}>
                <DialogTitle>Delete {isGroup ? "Group" : "User"}</DialogTitle>
                <DialogContent>
                    <Typography>
                        Are you sure you want to delete this {isGroup ? "group" : "user"}?
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseModal} variant="outlined">Cancel</Button>
                    <Button onClick={handleDelete} variant="contained" color="error">
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>


            {chatOpen &&
                <ChatModal open={chatOpen} onClose={handleChatModalClose} user={selectedUser} group={isGroup}/>}
            <LogoutModal open={logoutOpen} onClose={handleCloseLogout} onLogout={handleLogout}/>
            {openGroupModal && (<CreateGroupModal
                open={openGroupModal}
                onClose={handleCreateModalClose}
                senderId={senderId}
            />)}
            <Dialog
                open={openDeleteChatDialog}
                onClose={() => setOpenDeleteChatDialog(false)}
                sx={{"& .MuiPaper-root": {backgroundColor: "#222", color: "#fff"}}}
            >
                <DialogTitle>Clear Conversation</DialogTitle>
                <DialogContent>
                    <Typography>
                        Are you sure you want to clear the conversation
                        with <strong>{deleteChatUser?.userName}</strong>?
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenDeleteChatDialog(false)} variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={confirmDeleteChatHistory} variant="contained" color="error">
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>


            {/* Profile Picture Menu */}
            <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleClose}>
                <MenuItem onClick={handleViewProfile}>
                    <VisibilityIcon sx={{mr: 1}}/> View Profile Picture
                </MenuItem>
                <MenuItem onClick={handleDeleteProfile}>
                    <DeleteIcon sx={{mr: 1}}/> Delete Profile Picture
                </MenuItem>
                <MenuItem onClick={handleChangeProfile}>
                    <PhotoCameraIcon sx={{mr: 1}}/> Change Profile Picture
                </MenuItem>
            </Menu>

            {/* Profile Picture Dialog */}
            <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
                <DialogContent>
                    <img src={selectedProfile} alt="Profile" style={{maxWidth: "100%", height: "auto"}}/>
                </DialogContent>
            </Dialog>

            {/* Hidden File Input */}
            <input
                type="file"
                id="fileInput"
                style={{display: "none"}}
                accept="image/*"
                onChange={handleFileUpload}
            />


        </Box>
    );
};

export default Dashboard;
