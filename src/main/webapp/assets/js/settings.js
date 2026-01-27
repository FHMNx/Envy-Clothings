async function changePassword() {
    Notiflix.Loading.standard("loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    let currentPassword = document.getElementById("currentPw");
    let newPassword = document.getElementById("newPw");
    let confirmPassword = document.getElementById("confirmPw");

    const userObject = {
        password: currentPassword.value,
        newPassword: newPassword.value,
        confirmPassword: confirmPassword.value,
    }

    try {
        const response = await fetch("api/admin/changePassword", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userObject)
        });

        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                Notiflix.Report.success(
                    'Envy Clothings',
                    data.message,
                    'Okay',
                    () => {
                        window.location.reload();
                    }
                );
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        } else {
            Notiflix.Notify.failure("password reset failed", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    } finally {
        Notiflix.Loading.remove(1000);
    }
}

async function forgotAdminPassword() {
    const email = document.getElementById("email").value;

    if (!email) {
        Notiflix.Notify.failure("Please enter your email address", {
            position: 'center-top'
        });
        return;
    }

    Notiflix.Loading.standard("Sending reset email...");

    try {
        const response = await fetch("api/admin/forgot-password", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({email})
        })

        const data = await response.json();
        if (data.status) {
            Notiflix.Report.success(
                "Envy Clothings",
                "A password reset link has been sent to your email.<br>Please check your inbox.",
                "Okay"
            );
        } else {
            Notiflix.Notify.failure(data.message, {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure("Something went wrong. Please try again.");
    } finally {
        Notiflix.Loading.remove();
    }
}

async function resetAdminPassword(){

    const newPassword = document.getElementById("newPassword").value.trim();
    const confirmPassword = document.getElementById("confirmPassword").value.trim();

    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get("token");

    if(!token){
        Notiflix.Notify.failure("Invalid or expired reset link");
        return;
    }
    if(newPassword == "" || confirmPassword == ""){
        Notiflix.Notify.failure("please fill all fields", {
            position: 'center-top'
        });
        return;
    }
    if (newPassword.length < 6) {
        Notiflix.Notify.failure("password must be at least 6 characters", {
            position: 'center-top'
        });
        return;
    }

    if (newPassword !== confirmPassword) {
        Notiflix.Notify.failure("password do not match", {
            position: 'center-top'
        });
        return;
    }

    try {

        const response = await fetch("api/admin/reset-password", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({token: token, newPassword: newPassword})
        });

        const data = await response.json();
        if(response.ok && data.status){
            Notiflix.Report.success(
                "Envy Clothings",
                "Your password has been reset successfully.<br>Please sign in with your new password.",
                "OK",
                () => {
                    window.location.href = "admin-sign-in.html";
                }
            );
        }else{
            Notiflix.Notify.failure(data.message, {
                position: 'center-top'
            });
        }

    }catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    } finally {
        Notiflix.Loading.remove();
    }
}
