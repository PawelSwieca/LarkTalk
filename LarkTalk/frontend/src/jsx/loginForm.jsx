import React, { useState, useEffect } from "react";
import "../css/style.css";
import larkLogo from "../assets/logo/image.png";
import Dashboard from "./Dashboard.jsx";

export default function LoginForm() {
    const [login, setLogin] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [isLoggedIn, setIsLoggedIn] = useState(false);


    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setIsLoggedIn(true);
        }
    }, []);

    const sendRequest = async () => {
        if(!login || !password) {
            setMessage("Enter your login and passowrd");
            return;
        }

        const loginData = { login, password };

        try {
            const response = await fetch("/api/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(loginData),
            });

            if (!response.ok) {
                if (response.status === 401) throw new Error("Invalid login or password");
                throw new Error(`Server error: ${response.status}`);
            }

            const data = await response.json();


            localStorage.setItem("token", data.token);
            localStorage.setItem("userLogin", login);

            setIsLoggedIn(true);

        } catch (error) {
            console.error("Login error:", error);
            setMessage(error.message || "Signing up failed");
        }
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("userLogin");
        setIsLoggedIn(false);
        setLogin("");
        setPassword("");
        setMessage("");
    }

    if (isLoggedIn) {
        return <Dashboard onLogout={handleLogout} />;
    }

    return (
        <div className="login-container">
            { <img src={larkLogo} className="logo" alt="Lark logo" /> }

            <h1>Lark Talk</h1>
            <h3>Where memories are created</h3>

            <div className="container_input">
                <input
                    type="text"
                    className="input_place"
                    placeholder="Login"
                    value={login}
                    onChange={(e) => setLogin(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && sendRequest()}
                />
                <input
                    type="password"
                    className="input_place"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && sendRequest()}
                />
                <button className="login_button" onClick={sendRequest}>
                    Sign in
                </button>
            </div>

            {message && <p className="error-msg">{message}</p>}
        </div>
    );
}