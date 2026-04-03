import React, {useEffect, useRef, useState} from "react"
import "../css/dashboard.css"
import "../css/chat.css"

import just_chatting from "../assets/chat_logo/just_chatting.jpg"
import games from "../assets/chat_logo/games.jpg"
import history from "../assets/chat_logo/history.jpg"
import programming from "../assets/chat_logo/programming.jpg"

export default function Dashboard({ onLogout }) {

    const [userData, setUserData] = useState(null); // null - not yet available
    const [showProfile, setShowProfile] = useState(false);
    const [showLogoutConfirm, setShowLogoutConfirm] = useState(false);

    const userLogin = localStorage.getItem("userLogin") || "User";
    const userNickname = localStorage.getItem("nickname") || "Nickname";
    const storedLogin = localStorage.getItem("userLogin");

    const [activeChat, setActiveChat] = useState(null);


    const [messageInput, setMessageInput] = useState("");

    // Simulation of messages
    const [messages, setMessages] = useState([]);

    const messagesEndRef = useRef(null);

    useEffect(() => {
        if (storedLogin) {
            fetch(`/api/profile?login=${storedLogin}`)
                .then(res => {
                    if (!res.ok) throw new Error("Profile load error");
                    return res.json();
                })
                .then(data => {
                    setUserData(data);
                    console.log(data);
                })
                .catch(err => console.error(err));
        }
    }, [storedLogin]);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages, activeChat]);

    const getChannelImage = (channelId, channelName) => {
        switch(channelId) {
            case 1: return just_chatting;
            case 2: return history;
            case 3: return programming;
            case 4: return games;
            default: return just_chatting;
        }
    };

    const [chats, setChats] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) return;

        fetch("/api/channels/my", {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` }
        })
            .then(res => {
                if (!res.ok) throw new Error("Loading channels failed: " + res.statusText);
                return res.json();
            })
            .then(data => {
                const channelsWithImages = data.map(channel => ({
                    id: channel.id,
                    name: channel.name,
                    img: getChannelImage(channel.id, channel.name)
                }));

                setChats(channelsWithImages);
            })
            .catch(err => console.error(err));

    }, []);

    const loadMessages = async (chatId) => {
        try {
            const token = localStorage.getItem("token");
            const userLogin = localStorage.getItem("userLogin");

            const response = await fetch(`/api/messages?chatId=${chatId}`, {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error(`Server error: ${response.status}`);
            }

            const data = await response.json();

            return data.map(msg => ({
                sender: msg.userName,
                text: msg.content,
                date: new Date(msg.timestamp).toLocaleString([], {
                    day: '2-digit',
                    month: '2-digit',
                    year: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                }),
                isMe: msg.userName === userLogin
            }));

        } catch (err) {
            console.error("Messages loading error: ", err);
            return [];
        }
    }

    const openChat = (chat) => {
        setActiveChat(chat);
        loadMessages(chat.id).then(messages => {
            setMessages(messages);
        });
    };

    const closeChat = () => {
        setActiveChat(null);
        setMessages([]);
    };

    const handleSendMessage = async (e) => {
        e.preventDefault();

        if (!messageInput.trim()) return;

        // Payload for backend - active chat + content
        // Sender and data will be set by Spring controller
        const backendPayload = {
            chatId: activeChat.id,
            content: messageInput
        };

        try {
            const token = localStorage.getItem("token");

            const response = await fetch("/api/messages", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(backendPayload)
            });

            if (!response.ok) {
                throw new Error(`Server error: ${response.status}`);
            }

            // --- SUCCESS! ---

            // Get date from database
            const data = await response.json();

            const newMessageLocal = {
                sender: userNickname,
                date: new Date(data.timestamp).toLocaleString([], {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            }),
                text: messageInput,
                isMe: true
            };

            setMessages([...messages, newMessageLocal]);
            setMessageInput("");

        } catch(err) {
            console.error("Message sending error: ", err);
            alert("Message sending error! Try again!");
        }
    };

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
                <span className="welcome-text">Welcome, <b>{userNickname}</b></span>
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
                            <p className="profile-role">{userData.roles}</p>
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
            {activeChat && (
                <div className="modal-overlay" onClick={closeChat}>
                    <div className="modal-content chat-window" onClick={(e) => e.stopPropagation()}>

                        <div className="chat-window-header">
                            <div className="chat-header-info">
                                <img src={activeChat.img} alt="icon" className="mini-chat-icon"/>
                                <h3>{activeChat.name}</h3>
                            </div>
                            <button className="close-chat-btn" onClick={closeChat}>✖</button>
                        </div>

                        <div className="chat-messages-area">
                            {messages.map((msg, index) => (
                                <div key={index} className={`message-bubble ${msg.isMe ? "my-message" : "other-message"}`}>
                                    <span className="msg-sender">{msg.sender}</span>
                                    <span className="msg-date">{msg.date}</span>
                                    <p className="msg-text">{msg.text}</p>
                                </div>
                            ))}
                            <div ref={messagesEndRef} />
                        </div>

                        <form className="chat-input-bar" onSubmit={handleSendMessage}>
                            <input
                                type="text"
                                placeholder="Write a message..."
                                value={messageInput}
                                onChange={(e) => setMessageInput(e.target.value)}
                                autoFocus
                            />
                            <button type="submit" className="send-btn">➤</button>
                        </form>
                    </div>
                </div>
            )}
            <h1 className="dashboard-title">Select room</h1>
            <div className="chat-grid">
                {chats.map((chat) => (
                    <div className="chat-card" key={chat.id} onClick={() => openChat(chat)}>
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
