import React from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Button,
    Typography
} from "@mui/material";
import {removeUserFromGroup} from "../api/auth/groupApi";
import {toast} from "react-toastify";

// MembersModal Component
const MembersModal = ({open, onClose, members, groupId, refreshGroupMembers}) => {


    const handleRemoveUser = async (userId) => {
        console.log("userId >>>>", userId);
        try {
            const response = await removeUserFromGroup(groupId, userId);
            if (response.status === 200) {
                refreshGroupMembers();
                toast.success(response.data.message, {
                    transition: "Zoom"
                });
            }
        } catch (error) {
            console.error("Error removing user:", error);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle sx={{backgroundColor: "#333", color: "#fff"}}>
                Group Members
            </DialogTitle>
            <DialogContent sx={{backgroundColor: "#222", color: "#fff"}}>
                <TableContainer component={Paper} sx={{backgroundColor: "#333", marginTop: "12px"}}>
                    <Table sx={{minWidth: 450}} aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <TableCell sx={{color: "#fff", fontWeight: "bold"}}>Member Name</TableCell>
                                <TableCell sx={{color: "#fff", fontWeight: "bold"}}>Action</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {members && members.length > 0 ? (
                                members.map((member, index) => (
                                    <TableRow key={index} sx={{backgroundColor: "#333"}}>
                                        <TableCell sx={{color: "#fff"}}>
                                            {member.userName}
                                        </TableCell>
                                        <TableCell sx={{color: "#fff"}}>
                                            <Button
                                                variant="contained"
                                                color="error"
                                                sx={{
                                                    backgroundColor: "#d32f2f",
                                                    "&:hover": {
                                                        backgroundColor: "#c62828",
                                                    },
                                                }}
                                                onClick={() => handleRemoveUser(member.id)}
                                            >
                                                Remove
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                ))
                            ) : (
                                <TableRow sx={{backgroundColor: "#333"}}>
                                    <TableCell colSpan={2} sx={{color: "#aaa", textAlign: "center"}}>
                                        No Members Found
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            </DialogContent>
            <DialogActions sx={{backgroundColor: "#333"}}>
                <Button onClick={onClose} sx={{color: "#fff"}}>
                    Close
                </Button>
            </DialogActions>
        </Dialog>

    );
};

export default MembersModal;
