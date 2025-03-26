import React from "react";
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, Typography } from "@mui/material";
import {getUserName} from "../../config/Cookie-store";


const LogoutModal = ({ open, onClose, onLogout }) => {
    return (
        <Dialog
            open={open}
            onClose={onClose}
            sx={{
                "& .MuiPaper-root": {
                    backgroundColor: "#222",
                    color: "#fff",
                    borderRadius: 2
                }
            }}
        >
            <DialogTitle sx={{ backgroundColor: "#333", color: "#fff", textAlign: "center" }}>
                Logout
            </DialogTitle>
            <DialogContent sx={{ backgroundColor: "#222", padding: 2 }}>
                <Typography sx={{ color: "#ddd" }}>
                    Are you sure you want to logout, <b>{getUserName("userName")}</b>?
                </Typography>
            </DialogContent>
            <DialogActions sx={{ backgroundColor: "#333", padding: 2 }}>
                <Button onClick={onClose} sx={{ color: "#fff", borderColor: "#555" }} variant="outlined">
                    Cancel
                </Button>
                <Button
                    onClick={onLogout}
                    sx={{ backgroundColor: "#d32f2f", color: "#fff" }}
                    variant="contained"
                >
                    Logout
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default LogoutModal;
