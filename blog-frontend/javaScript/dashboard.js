    document.addEventListener("DOMContentLoaded", () => {
        const token = localStorage.getItem("token");

        if(!token){
            window.location.href = "login.html";
            return;
        }

        document.getElementById("welcome-message").textContent = "Welcome back. You are logged in!";

        const profileButton = document.getElementById("profile-btn");
        const postsContainer = document.getElementById("posts-container");
        const postForm = document.getElementById("post-form");

        async function loadPosts() {
            try{
                const response = await fetch("http://localhost:8080/api/posts", {
                    method : "GET",
                    headers : {
                        "Content-Type" : "application/json"
                    }
                });
                if(!response.ok){
                    console.log("Failed to load posts");
                }

                const posts = await response.json();
                if(posts.length === 0){
                    postsContainer.innerHTML = `
                        <div class="empty-state">
                            <div class="empty-state-icon">📝</div>
                            <h3>No posts yet!</h3>
                            <p>Be the first to share something amazing with the community.</p>
                        </div>
                    `;
                    return;
                }
                postsContainer.innerHTML = "";

                posts.forEach(post => {
                    const div = document.createElement("div");
                    div.className = "post-card";

                    div.innerHTML = `
                        <div class="post-card-header">
                            <h4 class="post-card-title">${post.title}</h4>
                            <div class="post-card-meta">
                                <span class="post-author">${post.authorUsername || "Unknown"}</span>
                                <span class="post-date">${new Date(post.createdAt).toLocaleString()}</span>
                            </div>
                        </div>
                        <div class="post-card-body">
                            <p class="post-card-content">${post.content}</p>
                        </div>
                        <div class="post-card-footer">
                            <div class="post-actions">
                                <button class="like-btn" data-id="${post.id}">Like</button>
                                <button class="unlike-btn" data-id="${post.id}">Unlike</button>
                            </div>
                            <div class="post-stats">
                                <span class="like-count">❤️ ${post.likeCount || 0}</span>
                            </div>
                        </div>
                    `;
                    postsContainer.appendChild(div);
                });

                document.querySelectorAll(".like-btn").forEach(btn => {
                    btn.addEventListener("click", async () => {
                        const postId = btn.getAttribute("data-id");
                        await likeOrUnlike(postId, "like");
                    });
                });

                document.querySelectorAll(".unlike-btn").forEach(btn => {
                    btn.addEventListener("click", async () => {
                        const postId = btn.getAttribute("data-id");
                        await likeOrUnlike(postId, "unlike");
                    });
                });
            }
            catch(error){
                postsContainer.innerHTML = `<p style = "color:red;">${error.message}</p>`;
            }
        }
        loadPosts();

        async function likeOrUnlike(postId, action) {
            const token = localStorage.getItem("token");
            try{
                const response = await fetch(`http://localhost:8080/api/posts/${postId}/${action}`, {
                    method: "POST",
                    headers : {
                        "Authorization" : `Bearer ${token}`,
                        "Content-Type" : "application/json"
                    }
                });

                if(!response.ok){
                    const errText = await response.text();
                    alert(`Failed to ${action} post: ${errText}`);
                    return;
                }
                loadPosts();
            }
            catch(err){
                throw new Error(err.message);
            }
            
        }
        
        profileButton.addEventListener("click", () => {
            window.location.href = "profile.html";
        }); 
    });