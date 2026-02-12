import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import '../dump/index.css'
import LoginForm from "./loginForm.jsx";

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <LoginForm />
  </StrictMode>,
)
