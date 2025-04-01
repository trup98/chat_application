import React, {useState, useEffect} from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    Checkbox,
    FormControlLabel,
    List,
    ListItem,
    ListItemText,
    Paper, TableCell, TableRow,
} from "@mui/material";
import {addUsersToGroup, getAvailableUsers} from "../api/auth/groupApi";

const AddMembersModal = ({open, onClose, groupId, refreshGroupMembers}) => {
    console.log("groupId >>>>", groupId)
    const [users, setUsers] = useState([]);
    const [selectedUsers, setSelectedUsers] = useState([]);

    useEffect(() => {
        if (open) {
            fetchUsers();
        }
    }, [open]);

    const fetchUsers = async () => {
        try {
            const response = await getAvailableUsers(groupId);
            if (response.status === 200) {
                setUsers(response.data);
            }
        } catch (error) {
            console.error("Error fetching users:", error);
        }
    };

    const handleSelectUser = (userId) => {
        console.log("userId >>> ", userId);
        setSelectedUsers((prev) =>
            prev.includes(userId)
                ? prev.filter((id) => id !== userId)
                : [...prev, userId]
        );
    };

    const handleAddUsers = async () => {
        try {
            const validUserIds = selectedUsers.filter(id => id !== null);
            if (validUserIds.length === 0) {
                console.error("No valid users selected");
                return;
            }

            const response = await addUsersToGroup(groupId, validUserIds);
            if (response.status === 200) {
                refreshGroupMembers();
                onClose();
            }
        } catch (error) {
            console.error("Error adding users:", error);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle sx={{backgroundColor: "#333", color: "#fff"}}>
                Add Members to Group
            </DialogTitle>
            <DialogContent sx={{backgroundColor: "#222", color: "#fff"}}>
                <Paper sx={{maxHeight: 300, overflow: "auto", backgroundColor: "#444"}}>
                    <List>
                        {users && users.length ? (
                            users.map((user) => (

                                <ListItem key={user.id} dense>
                                    <FormControlLabel
                                        control={
                                            <Checkbox
                                                checked={selectedUsers.includes(user.userId)} // Correct way to check selection
                                                onChange={() => handleSelectUser(user.userId)}
                                                sx={{color: "#fff"}}
                                            />
                                        }
                                        label={<ListItemText primary={user.userName} sx={{color: "#fff"}}/>}
                                    />
                                    {console.log("user id >>>>>", user.userId)}
                                </ListItem>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={2} sx={{color: "#aaa", textAlign: "center"}}>
                                    No Members Available
                                </TableCell>
                            </TableRow>
                        )}
                    </List>

                </Paper>
            </DialogContent>
            <DialogActions sx={{backgroundColor: "#333"}}>
                <Button onClick={onClose} sx={{color: "#fff"}}>
                    Cancel
                </Button>
                <Button onClick={handleAddUsers} sx={{color: "#fff"}} disabled={selectedUsers.length === 0}>
                    Add
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default AddMembersModal;
