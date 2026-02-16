import React, { useState } from "react";
import larkLogo from "../assets/logo/image.png";
import "../css/style.css";

export default function SignUpForm({ onSwitchToLogin }) {
    const [formData, setFormData] = useState({
        login: "",
        nickname: "",
        email: "",
        password: ""
    });

    const [errors, setErrors] = useState({});

    const [serverMessage, setServerMessage] = useState("");
    const [isSuccess, setIsSuccess] = useState(false);

    const handleValidation = (data) => {
        let newErrors = {};

        for (let key in data) {
            if (!data[key]) {
                newErrors[key] = "This field is required!";
            }
        }

        if (data.login && !/^[A-Za-z][A-Za-z\d.-]{0,19}$/.test(data.login)) {
            newErrors.login = "Login must start from a letter (max 20 chars).";
        }
        if (data.nickname && !/^[\w-.]{3,20}$/.test(data.nickname)) {
            newErrors.nickname = "Nickname: 3-20 characters (letters, digits, -, _).";
        }
        if (data.email && !/^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/.test(data.email)) {
            newErrors.email = "Invalid email address!";
        }
        if (data.password && !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(data.password)) {
            newErrors.password = "Password: min. 8 characters, capital and small letter, digit and special character (no '.').";
        }

        return newErrors;
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });

        if (errors[name]) {
            setErrors({ ...errors, [name]: null });
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setServerMessage("");

        const validationErrors = handleValidation(formData);
        setErrors(validationErrors);

        if (Object.keys(validationErrors).length === 0) {
            console.log("Validation succeeded!", formData);

            try {
                const response = await fetch("/api/signup", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(formData),
                });

                if (response.ok) {
                    setIsSuccess(true);
                    setServerMessage("Account successfully created!...");

                    setTimeout(() => {
                        onSwitchToLogin();
                    }, 1000);
                } else {
                    const errorText = await response.text();
                    setIsSuccess(false);
                    setServerMessage("Error: " + errorText);
                }
            } catch (error) {
                console.error("Error:", error);
                setIsSuccess(false);
                setServerMessage("Server connection error.");
            }
        }
    };

    return (
        <div className="login-container">
            <img src={larkLogo} className="logo" alt="Lark logo" />

            <h1>Create profile</h1>
            <h3>First step towards magic!</h3>

            <form className="container_input" onSubmit={handleSubmit}>

                <div className="input-group">
                    <input
                        type="text"
                        name="login"
                        className={`input_place ${errors.login ? "input-error" : ""}`}
                        placeholder="Login"
                        value={formData.login}
                        onChange={handleChange}
                    />
                    {errors.login && <span className="field-error">{errors.login}</span>}
                </div>

                <div className="input-group">
                    <input
                        type="text"
                        name="nickname"
                        className={`input_place ${errors.nickname ? "input-error" : ""}`}
                        placeholder="Nickname"
                        value={formData.nickname}
                        onChange={handleChange}
                    />
                    {errors.nickname && <span className="field-error">{errors.nickname}</span>}
                </div>

                <div className="input-group">
                    <input
                        type="email"
                        name="email"
                        className={`input_place ${errors.email ? "input-error" : ""}`}
                        placeholder="Email"
                        value={formData.email}
                        onChange={handleChange}
                    />
                    {errors.email && <span className="field-error">{errors.email}</span>}
                </div>

                <div className="input-group">
                    <input
                        type="password"
                        name="password"
                        className={`input_place ${errors.password ? "input-error" : ""}`}
                        placeholder="Password"
                        value={formData.password}
                        onChange={handleChange}
                    />
                    {errors.password && <span className="field-error">{errors.password}</span>}
                </div>

                <button type="submit" className="login_button">
                    Create!
                </button>
            </form>

            <p className="switch-text">
                Return? <span onClick={onSwitchToLogin} className="link-btn">Sign in</span>
            </p>

            {serverMessage && (
                <p className="server-msg" style={{ color: isSuccess ? "#4ade80" : "#fbbf24" }}>
                    {serverMessage}
                </p>
            )}
        </div>
    );
}