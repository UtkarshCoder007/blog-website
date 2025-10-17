document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const welcomeMessage = document.getElementById("welcome-message");
    const username = document.getElementById("username");
    const email = document.getElementById("email");
    const role = document.getElementById("role");
    const deleteButton = document.getElementById("delete-button");
    const dashboardButton = document.getElementById("dashboard-btn");
    const logoutButton = document.getElementById("logout-btn");
    const message = document.getElementById("message");
    const myPostsContainer = document.getElementById("my-posts");
    const createPostSection = document.getElementById("create-post-section");
    const createPostForm = document.getElementById("create-post-form");

    if(!token){
        alert("You must be logged in to view this page.");
        window.location.href = "login.html";
        return;
    }

    let user;
    try{
        const res  = await fetch("http://localhost:8080/api/auth/users/me", {
            method : "GET",
            headers : {
                "Authorization" : `Bearer ${token}`,
                "Content-Type" : "application/json"
            }
        })
        
        if(!res.ok){
            throw new Error("Failed to load profile.");
        }
        user = await res.json();
    
        welcomeMessage.innerText = `Hello, ${user.username}`;
        username.innerText = `Username: ${user.username}`;
        email.innerText = `Email: ${user.email}`;
        role.innerHTML = `Role: ${user.roles.map(r => r.name).join(",")}`;

        const hasPermission = user.roles.some(r =>
            r.name === "ROLE_AUTHOR" || r.name === "ROLE_ADMIN"
        );
        if(hasPermission)
            createPostSection.style.display = "block";
        loadMyPosts();
    }
    catch(err){
        console.error(err);
        alert("Error loading profile: "+err.message);
        window.location.href = "login.html";
    }

    async function loadMyPosts() {
        try{
            const res = await fetch("http://localhost:8080/api/posts/my-posts", {
                method: "GET",
                headers:{
                    "Authorization": `Bearer ${token}`
                }
            });
            if(!res.ok)
                throw new Error("Failed to load Posts!");

            const posts = await res.json();

            myPostsContainer.innerHTML = "";

            if(posts.length === 0){
                myPostsContainer.innerHTML = `
                    <div class="empty-posts">
                        <div class="empty-posts-icon">📝</div>
                        <h3>No posts yet!</h3>
                        <p>Start sharing your thoughts with the community.</p>
                    </div>
                `;
                return;
            }

            posts.forEach(post => {
                const div = document.createElement("div");
                div.className = "my-post-card";
                div.innerHTML = `
                    <div class="my-post-card-header">
                        <h4 class="my-post-title">${post.title}</h4>
                        <span class="my-post-date">${new Date(post.createdAt).toLocaleString()}</span>
                    </div>
                    <div class="my-post-card-body">
                        <p class="my-post-content">${post.content}</p>
                        <div class="my-post-actions">
                            <button class="edit-button" data-id="${post.id}">Edit</button>
                            <button class="delete-button" data-id="${post.id}">Delete</button>
                        </div>
                    </div>
                `;
                myPostsContainer.appendChild(div);
            });
            attachPostActions();
        }
        catch(err){
            console.error(err);
            myPostsContainer.innerHTML = `<p>Error loading Posts: ${err.message}</p>`;
        }
    }
    function attachPostActions(){
        document.querySelectorAll(".edit-button").forEach(btn => {
            btn.addEventListener("click", async e => {
                  const id = e.target.dataset.id;
                  const newTitle = prompt("Enter new Title: ");
                  const newContent = prompt("Enter new Content: ");
                  if(!newTitle || !newContent) return;
                  try{
                    const res = await fetch(`http://localhost:8080/api/posts/${id}`, {
                        method: "PUT",
                        headers: {
                            "Authorization" : `Bearer ${token}`,
                            "Content-Type" : "application/json"
                        },
                        body: JSON.stringify({title : newTitle, content : newContent})
                    });
                    if(!res.ok)
                        throw new Error("Failed to update Post.");
                    alert("Post updated!");
                    loadMyPosts();
                  }
                  catch(err){
                    alert("Error updating Post: "+err.message);
                  }
            });
        });

        document.querySelectorAll(".delete-button").forEach(btn => {
            btn.addEventListener("click", async e => {
                const id = e.target.dataset.id;
                if(!confirm("Are you sure you want to delete this post?")) return;
                try{
                    const res = await fetch(`http://localhost:8080/api/posts/${id}`,{
                        method: "DELETE",
                        headers: {
                            "Authorization" : `Bearer ${token}`
                        }
                    });
                    if(!res.ok)
                        throw new Error("Failed to delete post.");
                    alert("Post deleted.");
                    loadMyPosts();
                }
                catch(err){
                    alert("Error deleting Post: "+err.message);
                }
            });
        });
    }

    createPostForm.addEventListener("submit", async e => {
        e.preventDefault();
        const title = document.getElementById("title").value.trim();
        const content = document.getElementById("content").value.trim();

        if(!title || !content){
            alert("All fields are required!");
            return;
        }

        try{
            const res = await fetch("http://localhost:8080/api/posts", {
                method: "POST",
                headers:{
                    "Authorization" : `Bearer ${token}`,
                    "Content-Type" : "application/json"
                },
                body: JSON.stringify({
                    title, content})
            });
            if(!res.ok)
                throw new Error("Failed to create Post.");

            createPostForm.reset();
            loadMyPosts();
        }
        catch(err){
            alert("Error creating post: "+err.message);
        }
    });

    deleteButton.addEventListener("click", () => {
        if(!confirm("Are you sure you want to delete your account? This can't be undone.")){
            return;
        }

        fetch("http://localhost:8080/api/auth/me", {
            method : "DELETE",
            headers : {
                "Authorization" : `Bearer ${token}`
            }
        })
        .then(res => {
            if(res.ok){
                alert("Account Deleted Successfully.");
                localStorage.removeItem("token");
                window.location.href = "login.html";
            }
            else{
                return res.text().then(text => {throw new Error(text)});
            }
        })
        .catch(err => {
            console.error(err);
            alert("Error deleting account: "+err.message);
        });
        
    });
    dashboardButton.addEventListener("click", () => {
        window.location.href = "dashboard.html";
    });

    logoutButton.addEventListener("click", () => {
        localStorage.removeItem("token");
        alert("Logged out successfully!");
        window.location.href = "login.html";
    });
});