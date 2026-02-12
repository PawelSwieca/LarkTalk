import React, {useEffect, useState} from "react";
import "../css/dashboard.css";

import just_chatting from "../assets/chat_logo/just_chatting.jpg"
import games from "../assets/chat_logo/games.jpg"
import history from "../assets/chat_logo/history.jpg"
import programming from "../assets/chat_logo/programming.jpg"

export default function Dashboard({ onLogout }) {

    const [userData, setUserData] = useState(null); // null - not yet available
    const [showProfile, setShowProfile] = useState(false);
    const [showLogoutConfirm, setShowLogoutConfirm] = useState(false);

    const userLogin = localStorage.getItem("userLogin") || "User";
    const storedLogin = localStorage.getItem("userLogin");

    useEffect(() => {
        if (storedLogin) {
            fetch(`/api/profile?login=${storedLogin}`)
                .then(res => {
                    if (!res.ok) throw new Error("Profile load error");
                    return res.json();
                })
                .then(data => {
                    setUserData(data);
                })
                .catch(err => console.error(err));
        }
    }, [storedLogin]);

    const chats = [
        { id: 1, name: "All in One!", img: just_chatting },
        { id: 2, name: "History", img: history },
        { id: 3, name: "Programming", img: programming },
        { id: 4, name: "Video games", img: games },
    ];

    const handleLogoutClick = () => {
        setShowLogoutConfirm(true);
    };

    const confirmLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("userLogin");

        onLogout();
    };

    const cancelLogout = () => {
        setShowLogoutConfirm(false);
    };

    const toggleProfile = () => {
        setShowProfile(!showProfile);
    }


    return (
        <div className="dashboard">
            <div className="dashboard-menu">
                <span className="welcome-text">Welcome, <b>{userLogin}</b></span>
                <div>
                    <button className="menu-button" id="profile" onClick={toggleProfile}>
                        👤 Profile
                    </button>
                    <button className="menu-button logout-btn" onClick={handleLogoutClick}>
                        🚪 Log out
                    </button>
                </div>
            </div>
            {showProfile && userData && (
                <div className="modal-overlay" onClick={toggleProfile}>
                    <div className="modal-content profile-card" onClick={(e) => e.stopPropagation()}>
                        <div className="profile-header">
                            <div className="profile-avatar">
                                {userData.login.charAt(0).toUpperCase()}
                            </div>
                            <h2>{userData.nickname}</h2>
                            <p className="profile-role">User</p>
                        </div>

                        <div className="profile-details">
                            <div className="detail-row">
                                <span className="label">Login:</span>
                                <span className="value">{userData.login}</span>
                            </div>
                            <div className="detail-row">
                                <span className="label">Email:</span>
                                <span className="value">{userData.email}</span>
                            </div>
                            <div className="detail-row">
                                <span className="label">Created at:</span>
                                <span className="value">
                                    {new Date(userData.createdAt).toLocaleDateString()}
                                </span>
                            </div>
                        </div>
                        <div className="profile-buttons">
                            <button className="modal-btn confirm" onClick={toggleProfile}>Close</button>
                        </div>
                    </div>
                </div>
            )}
            <h1 className="dashboard-title">Select room</h1>
            <div className="chat-grid">
                {chats.map((chat) => (
                    <div className="chat-card" key={chat.id}>
                        <div className="chat-image-wrapper">
                            <img
                                src={chat.img}
                                alt={chat.name}
                                className="chat-image"
                            />
                        </div>
                        <h2 className="chat-name">{chat.name}</h2>
                    </div>
                ))}
            </div>

            {showLogoutConfirm && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>Do you wish to exit?</h3>
                        <p>You will get logged out</p>
                        <div className="modal-actions">
                            <button className="modal-btn cancel" onClick={cancelLogout}>Cancel</button>
                            <button className="modal-btn confirm" onClick={confirmLogout}>Log out</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
