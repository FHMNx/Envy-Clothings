function changeView() {
    var signInBox = document.getElementById("signInBox");
    var signUpBox = document.getElementById("signUpBox");

    signInBox.classList.toggle("hidden");
    signUpBox.classList.toggle("hidden");
}

async function signUp() {

    Notiflix.Loading.standard("loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    let firstName = document.getElementById("firstName");
    let lastName = document.getElementById("lastName");
    let email = document.getElementById("email");
    let password = document.getElementById("password");

    const user = {
        firstName: firstName.value,
        lastName: lastName.value,
        email: email.value,
        password: password.value
    }

    try {
        const response = await fetch("api/users", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(user)
        });

        if (response.ok) {

            const data = await response.json();
            if (data.status) {
                Notiflix.Report.success(
                    'Envy Clothings',
                    'Welcome to Envy Clothings!<br><br>Your account has been created successfully.<br>We have sent a verification link to your email.<br>Please verify your account before logging in.',
                    "Okay",
                    () => {
                        window.location = "sign-in.html";
                    },
                );
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }

        } else {
            Notiflix.Notify.failure("Invalid user credentials", {
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

async function signIn() {
    Notiflix.Loading.standard("loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    let email = document.getElementById("email_input");
    let password = document.getElementById("password_input");

    const userLoginObj = {
        email: email.value,
        password: password.value
    }

    try {
        const response = await fetch("api/users/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userLoginObj)
        });

        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                Notiflix.Report.success(
                    'Envy Clothings',
                    data.message,
                    "okay",
                    () => {
                        window.location = "index.html"
                    },
                );
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        } else {
            Notiflix.Notify.failure("Login failed! please try again", {
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