import React, { useState, useEffect } from "react";
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
    Button
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { toast, Zoom } from "react-toastify";
import { callAllUser } from "../api/auth/userApi";
import DensityMediumIcon from "@mui/icons-material/DensityMedium";
import ChatModal from "../modal/ChatModal";

const Dashboard = () => {
    const navigate = useNavigate();
    const [users, setUsers] = useState({
        data: [],
        totalPages: 0,
        pageNumber: 0,
        pageSize: 10
    });

    const [value, setValues] = useState("");
    const [currentPage, setCurrentPage] = useState(0);
    const [openModal, setOpenModal] = useState(false);
    const [chatOpen, setChatOpen] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);

    // Fetch Users from API
    const getAll = (currentPage) => {
        callAllUser(value, value ? 0 : currentPage, users.pageSize)
            .then((response) => {
                setUsers({
                    data: response.data.content,
                    totalPages: response.data.totalPages,
                    pageNumber: currentPage,
                    pageSize: users.pageSize
                });
            })
            .catch((error) => {
                if (error.response && error.response.status === 401) {
                    toast.error(error.response.data.message, {
                        transition: Zoom
                    });
                }
            });
    };

    useEffect(() => {
        getAll(currentPage);
    }, [currentPage, value]);

    // Open modal when clicking the icon
    const handleOpenModal = (user) => {
        setSelectedUser(user);
        setOpenModal(true);
    };

    // Close start chat modal
    const handleCloseModal = () => {
        setOpenModal(false);
        setSelectedUser(null);
    };

    // Open Chat UI
    const handleStartChat = () => {
        setOpenModal(false); // Close start chat modal
        setChatOpen(true);   // Open chat modal
    };

    return (
        <div style={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            minHeight: "100vh",
            backgroundColor: "#000"
        }}>
            <TableContainer component={Paper} sx={{
                backgroundColor: "#222",
                color: "#fff",
                borderRadius: 2,
                minWidth: 800,
                maxWidth: 1200,
                width: "90%",
                padding: 2
            }}>
                <Table sx={{ minWidth: 800, maxWidth: 1200, width: "100%" }} aria-label="user table">
                    <TableHead>
                        <TableRow sx={{ backgroundColor: "#333" }}>
                            <TableCell sx={{ color: "#fff", fontWeight: "bold" }}>User Name</TableCell>
                            <TableCell sx={{ color: "#fff", fontWeight: "bold" }} align="right">Email</TableCell>
                            <TableCell sx={{ color: "#fff", fontWeight: "bold" }} align="right">Action</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {users.data && users.data.length > 0 ? (
                            users.data.map((user) => (
                                <TableRow
                                    key={user.id}
                                    sx={{
                                        '&:nth-of-type(odd)': { backgroundColor: "#2a2a2a" },
                                        '&:nth-of-type(even)': { backgroundColor: "#1e1e1e" },
                                        '&:hover': { backgroundColor: "#444" }
                                    }}
                                >
                                    <TableCell sx={{ color: "#fff" }} component="th" scope="row">{user.userName}</TableCell>
                                    <TableCell sx={{ color: "#fff" }} align="right">{user.email}</TableCell>
                                    <TableCell align="right">
                                        <IconButton onClick={() => handleOpenModal(user)}>
                                            <DensityMediumIcon sx={{ color: "#fff" }} />
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={3} sx={{ color: "#fff", textAlign: "center" }}>
                                    No users found
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
                        backgroundColor: "#222",  // Dark background
                        color: "#fff",             // White text
                        borderRadius: 2
                    }
                }}
            >
                <DialogTitle sx={{ backgroundColor: "#333", color: "#fff", textAlign: "center" }}>
                    Start Chat
                </DialogTitle>

                <DialogContent sx={{ backgroundColor: "#222", padding: 2 }}>
                    <p style={{ color: "#ddd" }}>
                        Would you like to start a chat with <b>{selectedUser?.userName}</b>?
                    </p>
                </DialogContent>

                <DialogActions sx={{ backgroundColor: "#333", padding: 2 }}>
                    <Button onClick={handleCloseModal} sx={{ color: "#fff", borderColor: "#555" }} variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={handleStartChat} sx={{ backgroundColor: "#007bff", color: "#fff" }} variant="contained">
                        Start
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Chat UI Modal */}
            <ChatModal open={chatOpen} onClose={() => setChatOpen(false)} user={selectedUser}  />
        </div>
    );
};

export default Dashboard;
