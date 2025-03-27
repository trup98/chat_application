import React, {useState, useEffect} from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Checkbox
} from "@mui/material";
import {callAllUser} from "../api/auth/userApi";
import {toast, Zoom} from "react-toastify";
import {groupCrate} from "../api/auth/groupApi";

const CreateGroupModal = ({open, onClose, onCreateGroup, senderId}) => {
    const [groupName, setGroupName] = useState("");
    const [users, setUsers] = useState([]);
    const [selectedUsers, setSelectedUsers] = useState([]);

    useEffect(() => {
        if (open) {
            callAllUser("", 0, 10, senderId)
                .then((response) => {
                    if (response.status === 200) {
                        setUsers(response.data.content);
                    }
                })
                .catch((error) => {
                    toast.error("Failed to fetch users", {transition: Zoom});
                });
        }
    }, [open, senderId]);

    const handleUserSelect = (userId) => {
        setSelectedUsers((prev) =>
            prev.includes(userId) ? prev.filter((id) => id !== userId) : [...prev, userId]
        );
    };

    const handleCreateGroup = async () => {
        if (!groupName.trim()) {
            toast.error("Group name is required!", {transition: Zoom});
            return;
        }
        if (selectedUsers.length === 0) {
            toast.error("Select at least one user!", {transition: Zoom});
            return;
        }

        const newGroup = {
            name: groupName,
            members: selectedUsers
        };

        const response = await groupCrate(newGroup);
        if (response.status === 200) {
            onClose();
        }


        // onCreateGroup(newGroup);
        setGroupName("");
        setSelectedUsers([]);
        // onClose();
    };

    return (
        <Dialog
            open={open}
            onClose={onClose}
            maxWidth="sm"
            fullWidth
            sx={{
                "& .MuiPaper-root": {
                    backgroundColor: "#222",
                    color: "#fff",
                    borderRadius: 2
                }
            }}
        >
            <DialogTitle sx={{backgroundColor: "#333", color: "#fff", textAlign: "center"}}>
                Create New Group
            </DialogTitle>
            <DialogContent sx={{backgroundColor: "#222", padding: 2}}>
                <TextField
                    fullWidth
                    label="Group Name"
                    variant="outlined"
                    value={groupName}
                    onChange={(e) => setGroupName(e.target.value)}
                    sx={{
                        marginBottom: 2,
                        marginTop: 2,
                        backgroundColor: "#333",
                        input: {color: "#fff"},
                        fieldset: {borderColor: "#555"},
                        "& .MuiOutlinedInput-root": {
                            "& fieldset": {borderColor: "#555"},
                            "&:hover fieldset": {borderColor: "#777"},
                            "&.Mui-focused fieldset": {borderColor: "#007bff"},
                        }
                    }}
                />

                <TableContainer component={Paper} sx={{backgroundColor: "#222"}}>
                    <Table>
                        <TableHead>
                            <TableRow sx={{backgroundColor: "#333"}}>
                                <TableCell sx={{color: "#fff", fontWeight: "bold"}}>Select</TableCell>
                                <TableCell sx={{color: "#fff", fontWeight: "bold"}}>Username</TableCell>
                                <TableCell sx={{color: "#fff", fontWeight: "bold"}}>Email</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {users.map((user) => (
                                <TableRow
                                    key={user.id}
                                    sx={{
                                        '&:nth-of-type(odd)': {backgroundColor: "#2a2a2a"},
                                        '&:nth-of-type(even)': {backgroundColor: "#1e1e1e"},
                                        '&:hover': {backgroundColor: "#444"}
                                    }}
                                >
                                    <TableCell>
                                        <Checkbox
                                            checked={selectedUsers.includes(user.id)}
                                            onChange={() => handleUserSelect(user.id)}
                                            sx={{color: "#fff"}}
                                        />
                                    </TableCell>
                                    <TableCell sx={{color: "#fff"}}>{user.userName}</TableCell>
                                    <TableCell sx={{color: "#fff"}}>{user.email}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </DialogContent>
            <DialogActions sx={{backgroundColor: "#333", padding: 2}}>
                <Button onClick={onClose} sx={{color: "#fff", borderColor: "#555"}} variant="outlined">
                    Cancel
                </Button>
                <Button onClick={handleCreateGroup} sx={{backgroundColor: "#007bff", color: "#fff"}}
                        variant="contained">
                    Create
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default CreateGroupModal;
