import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import { FidgetSpinner } from "react-loader-spinner"; // Import loader spinner

export const Login = () => {
    const [loginDetail, setLoginDetail] = useState({
        email: '',
        password: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const navigate = useNavigate();
    const location = useLocation();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");

        try {
            const response = await axios.post("http://localhost:9050/api/v1/auth/login", loginDetail);
            console.log("Login Success:", response.data);

            if (response.data.data.token) {
                localStorage.setItem("authToken", response.data.data.token);
            }

            navigate("/dashboard");

        } catch (err) {
            console.error("Login Failed:", err);
            setError(err.response?.data?.message || "Login failed. Try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            {/* Full-screen loader */}
            {loading && (
                <div className="fullscreen-loader">
                    <FidgetSpinner width="200" color="#ffffff" />
                </div>
            )}

            <div className="container min-vh-100 d-flex align-items-center justify-content-center">
                <div className="row w-100">
                    <div className="col-md-6 offset-md-3">
                        <div className="card my-5 shadow-lg bg-dark text-light">
                            <form className="card-body p-lg-5" onSubmit={handleSubmit}>
                                <div className="text-center">
                                    <img
                                        src="https://img.freepik.com/free-photo/view-3d-boy-using-laptop_23-2150709886.jpg"
                                        className="img-fluid profile-image-pic img-thumbnail rounded-circle my-3"
                                        width="200px"
                                        alt="profile"
                                    />
                                </div>

                                {error && <div className="alert alert-danger">{error}</div>}

                                <div className="mb-3">
                                    <input
                                        type="email"
                                        className="form-control bg-light text-black border-0"
                                        placeholder="Email"
                                        value={loginDetail.email}
                                        onChange={(e) => setLoginDetail({ ...loginDetail, email: e.target.value })}
                                        required
                                    />
                                </div>
                                <div className="mb-3">
                                    <input
                                        type="password"
                                        className="form-control bg-light text-black border-0"
                                        placeholder="Password"
                                        value={loginDetail.password}
                                        onChange={(e) => setLoginDetail({ ...loginDetail, password: e.target.value })}
                                        required
                                    />
                                </div>
                                <div className="text-center">
                                    <button type="submit" className="btn btn-outline-danger w-100" disabled={loading}>
                                        Login
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            {/* CSS Styles */}
            <style>
                {`
                    .fullscreen-loader {
                        position: fixed;
                        top: 0;
                        left: 0;
                        width: 100%;
                        height: 100%;
                        background: rgba(0, 0, 0, 0.8);
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        z-index: 1000;
                    }
                `}
            </style>
        </>
    );
};
