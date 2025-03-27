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
import {callAllGroups} from "../api/auth/groupApi";
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
                console.log("response>>>", response);
                if (response.status === 200) {
                    console.log("response in if>>>", response);
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

    const handleCreateGroup = (groupData) => {
        console.log("Group Created:", groupData);
        // Call API to create group (Not implemented here)
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
                                    <TableCell sx={{color: "#fff"}}>
                                        {tabIndex === 0 ? item.userName : item.name}
                                    </TableCell>
                                    <TableCell sx={{color: "#fff"}} align="right">
                                        {tabIndex === 0 ? item.email : item.memberCount ?? 0}
                                    </TableCell>
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
            {/* Start Chat Modal */}
            <Dialog
                open={openModal}
                onClose={handleCloseModal}
                sx={{
                    "& .MuiPaper-root": {
                        backgroundColor: "#222",
                        color: "#fff",
                        borderRadius: 2
                    }
                }}
            >
                <DialogTitle sx={{backgroundColor: "#333", color: "#fff", textAlign: "center"}}>
                    Start Chat
                </DialogTitle>
                <DialogContent sx={{backgroundColor: "#222", padding: 2}}>
                    <Typography sx={{color: "#ddd"}}>
                        Would you like to start a chat with <b>{selectedUser?.userName}</b>?
                    </Typography>
                </DialogContent>
                <DialogActions sx={{backgroundColor: "#333", padding: 2}}>
                    <Button onClick={handleCloseModal} sx={{color: "#fff", borderColor: "#555"}} variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={handleStartChat} sx={{backgroundColor: "#007bff", color: "#fff"}}
                            variant="contained">
                        Start
                    </Button>
                </DialogActions>
            </Dialog>

            <ChatModal open={chatOpen} onClose={() => setChatOpen(false)} user={selectedUser}/>
            <LogoutModal open={logoutOpen} onClose={handleCloseLogout} onLogout={handleLogout}/>
            {openGroupModal && (<CreateGroupModal
                open={openGroupModal}
                onClose={() => setOpenGroupModal(false)}
                onCreateGroup={handleCreateGroup}
                senderId={senderId}
            />)}
        </Box>
    );
};

export default Dashboard;
