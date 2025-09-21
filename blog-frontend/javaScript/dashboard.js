document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token");

    if(!token){
        window.location.href = "login.html";
        return;
    }

    document.getElementById("welcome-message").textContent = "Welcome back. You are logged in!";

    document.getElementById("logout-btn").addEventListener("click", () => {
        localStorage.removeItem("token");
        window.location.href = "login.html";

    });

});