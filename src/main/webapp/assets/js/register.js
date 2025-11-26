function changeView() {
    var signInBox = document.getElementById("signInBox");
    var signUpBox = document.getElementById("signUpBox");

    signInBox.classList.toggle("hidden");
    signUpBox.classList.toggle("hidden");
}

async function signUp() {
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
            console.log(data);
        } else {
            Notiflix.Notify.failure("Something went wrong", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}