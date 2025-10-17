document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("register-form");
    const messageBox = document.getElementById("message");

    form.addEventListener("submit", async(e) => {
        e.preventDefault();
        const username = document.getElementById("username").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        try{
            const response = await fetch("http://localhost:8080/api/auth/register", {
                method : "POST",
                headers: {
                    "Content-Type" : "application/json"
                },
                body : JSON.stringify({
                    username,
                    email, 
                    password
                })
            });

            if(response.ok){
                messageBox.innerText = "Registration Successful. You can login now!";
                messageBox.className = "message message-success";
                form.reset();
            }
            else{
                const error = await response.text();
                messageBox.innerText = "Error: " +error;
                messageBox.className = "message message-error";
            }

        }
        catch(err){
            console.error(err);
            messageBox.innerText = "Something went wrong";
            messageBox.className = "message message-error";
        }
    });
});