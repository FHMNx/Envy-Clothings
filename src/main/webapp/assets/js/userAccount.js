window.addEventListener("load", async () => {
    Notiflix.Loading.standard("loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        await getCities();
        await loadUserData();
    } finally {
        Notiflix.Loading.remove(1000);
    }
});

async function loadUserData() {
    try {
        const response = await fetch("api/profiles/userProfile");
        if (response.ok) {
            if (response.redirected) {
                window.location.href = response.url;
                return;
            }
            const data = await response.json();
            document.getElementById("username").innerHTML = `Hello, ${data.user.firstName} ${data.user.lastName}`
            document.getElementById("firstName").value = data.user.firstName;
            document.getElementById("lastName").value = data.user.lastName;
            document.getElementById("mobile").value = data.user.mobile ? data.user.mobile : "";
            document.getElementById("lineOne").value = data.user.lineOne === undefined ? "" : data.user.lineOne;
            document.getElementById("lineTwo").value = data.user.lineTwo === undefined ? "" : data.user.lineTwo;
            document.getElementById("postalCode").value = data.user.postalCode === undefined ? "" : data.user.postalCode;
            document.getElementById("citySelect").value = data.user.cityId ? data.user.cityId : 0;
            document.getElementById("currentPassword").value = data.user.password;

        } else {
            Notiflix.Notify.failure("profile data loading failed", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

async function saveProfileChanges() {
    Notiflix.Loading.standard("loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    let firstName = document.getElementById("firstName");
    let lastName = document.getElementById("lastName");
    let lineOne = document.getElementById("lineOne");
    let lineTwo = document.getElementById("lineTwo");
    let postalCode = document.getElementById("postalCode");
    let citySelect = document.getElementById("citySelect");
    let currentPassword = document.getElementById("currentPassword");
    let newPassword = document.getElementById("newPassword");
    let confirmPassword = document.getElementById("confirmPassword");

    const userObject = {
        firstName: firstName.value,
        lastName: lastName.value,
        lineOne: lineOne.value,
        lineTwo: lineTwo.value,
        postalCode: postalCode.value,
        cityId: citySelect.value,
        currentPassword: currentPassword.value,
        newPassword: newPassword.value,
        confirmPassword: confirmPassword.value
    }

    try {
        const response = await fetch("api/profiles/updateProfile", {
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
                    "okay",
                );
            }else{
                Notiflix.Notify.failure(e.message, {
                    position: 'center-top'
                });
            }
        }else{
            Notiflix.Notify.failure("profile updated failed", {
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

async function getCities() {

    try {
        const response = await fetch("api/data/cities");

        if (response.ok) {
            const data = await response.json();
            const citySelect = document.getElementById("citySelect");

            data.cities.forEach((city) => {
                const option = document.createElement("option");
                option.value = city.id;
                option.innerHTML = city.name;
                citySelect.appendChild(option);
            })
        } else {
            Notiflix.Notify.failure("city loading failed", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

async function userLogOut() {
    Notiflix.Loading.pulse("wait...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        const response = await fetch("api/users/logout", {
            method: "GET",
            credentials: "include"
        });
        if (response.status === 200) {
            Notiflix.Report.success(
                'Envy Clothings',
                "logout successful",
                "okay",
                () => {
                    window.location = "sign-in.html"
                },
            );
        } else {
            Notiflix.Notify.failure("log out process failed", {
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