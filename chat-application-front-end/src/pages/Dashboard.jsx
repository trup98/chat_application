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
    Tab
} from "@mui/material";
import {useNavigate} from "react-router-dom";
import {toast, Zoom} from "react-toastify";
import {callAllUser} from "../api/auth/userApi";
import DensityMediumIcon from "@mui/icons-material/DensityMedium";
import ChatModal from "../modal/ChatModal";
import {getUserId, getUserName, removeUserSession} from "../config/Cookie-store";
import LogoutModal from "./auth/LogoutModal";
import {callAllGroups, deleteGroup} from "../api/auth/groupApi";
import AddIcon from "@mui/icons-material/Add";
import CreateGroupModal from "../modal/CreateGroupModal";


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

    // Fetch Users
    const getAllUsers = (currentPage) => {
        callAllUser(value, value ? 0 : currentPage, pageSize, senderId)
            .then((response) => {
                if (response.status === 200) {
                    console.log("response in if>>>", response);
                    setUsers(response.data.content);
                    setTotalPages(response.data.totalPages);
                }
            })
            .catch((error) => {
                toast.error(error.data.message, {transition: Zoom});
                setUsers([]);
            });
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
                toast.error(error.data.message, {transition: Zoom});
                setGroups([]);
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

    const handleTabChange = (event, newValue) => {
        setTabIndex(newValue);
        setCurrentPage(0);
    };

    const handleOpenModal = (user) => {
        setSelectedUser(user);
        setIsGroup(!!user.memberCount);
        setOpenModal(true);
    };

    const handleCloseModal = () => {
        setOpenModal(false);
        setSelectedUser(null);
    };

    const handleStartChat = () => {
        setOpenModal(false);
        setChatOpen(true);
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

    const handleDeleteGroup = async () => {
        console.log("Handle Delete Group>>>>", selectedUser);
        if (!selectedUser?.id) return;

        try {
            const response = await deleteGroup(selectedUser.id);
            if (response.status === 200) {
                toast.success("Group deleted successfully!", {
                    transition: Zoom
                });
                getAllGroups();
                setOpenModal(false);
            }

        } catch (error) {
            toast.error("Failed to delete group", {
                transition: Zoom
            });
        }
    };

    const handleUserDelete = () => {

    }

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
                minWidth: 800,
                maxWidth: 1200,
                width: "90%",
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

                <Table sx={{minWidth: 800, maxWidth: 1200, width: "100%"}} aria-label="table">
                    <TableHead>
                        <TableRow sx={{backgroundColor: "#333"}}>
                            <TableCell sx={{color: "#fff", fontWeight: "bold"}}>Index</TableCell>
                            <TableCell sx={{color: "#fff", fontWeight: "bold"}}>
                                {tabIndex === 0 ? "User Name" : "Group Name"}
                            </TableCell>
                            <TableCell sx={{color: "#fff", fontWeight: "bold"}} align="right">
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
                                    <TableCell sx={{color: "#fff"}}>{index + 1}</TableCell>

                                    {/*{userName or group name}*/}
                                    <TableCell sx={{color: "#fff"}}>
                                        {tabIndex === 0 ? item.userName : item.name}
                                    </TableCell>

                                    {/*{email or member count}*/}
                                    <TableCell sx={{color: "#fff"}} align="right">
                                        {tabIndex === 0 ? item.email : item.memberCount ?? 0}
                                    </TableCell>

                                    {/*{action button}*/}
                                    <TableCell align="right">
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
            </TableContainer>
            {/* Conditional Dialog */}
            <Dialog open={openModal} onClose={handleCloseModal}
                    sx={{"& .MuiPaper-root": {backgroundColor: "#222", color: "#fff"}}}>
                <DialogTitle>{isGroup ? "Delete Group" : "Start Chat"}</DialogTitle>
                <DialogContent>
                    <Typography>{isGroup ? "Are you sure you want to delete this group?" : `Start chat with ${selectedUser?.userName}?`}</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseModal} variant="outlined">Cancel</Button>
                    <Button onClick={isGroup ? handleDeleteGroup : handleStartChat} variant="contained">
                        {isGroup ? "Delete" : "Start"}
                    </Button>
                </DialogActions>
            </Dialog>

            <ChatModal open={chatOpen} onClose={() => setChatOpen(false)} user={selectedUser}/>
            <LogoutModal open={logoutOpen} onClose={handleCloseLogout} onLogout={handleLogout}/>
            {openGroupModal && (<CreateGroupModal
                open={openGroupModal}
                onClose={handleCreateModalClose}
                senderId={senderId}
            />)}
        </Box>
    );
};

export default Dashboard;
