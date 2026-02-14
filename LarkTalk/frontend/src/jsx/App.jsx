import React, { useState } from "react";
import LoginForm from "./LoginForm";
import SignUpForm from "./SignUpForm";

export default function App() {
    const [currentView, setCurrentView] = useState("login");

    const toggleView = () => {
        setCurrentView(currentView === "login" ? "signup" : "login");
    };

    return (
        <div id="app">
            {currentView === "login" ? (
                <LoginForm onSwitchToSignup={toggleView} />
            ) : (
                <SignUpForm onSwitchToLogin={toggleView} />
            )}
        </div>
    );
}