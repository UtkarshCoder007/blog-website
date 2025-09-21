document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("login");
    const messageBox = document.getElementById("message");

    form.addEventListener("submit", async(e) => {
        e.preventDefault();
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        try{
            const response = await fetch("http://localhost:8080/api/auth/login", {
                method : "POST",
                headers : {
                    "content-type" : "application/json"
                },
                body : JSON.stringify({
                    email,
                    password
                })
            });
            if(response.ok){
                const data = await response.json();
                localStorage.setItem("token", data.token);
                messageBox.innerText = "Login successful!";
                messageBox.style.color = "green";
                window.location.href = "dashboard.html";
            }
            else{
                const error = await response.text();
                messageBox.innerText = "Error: "+error;
                messageBox.style.box = "red";
            }
        }
        catch(err){
            console.error(err);
            messageBox.innerText = "Something went wrong!";
            messageBox.style.color = "red";
        }
    });
});