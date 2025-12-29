let billingAddressGlobal = null;
let shippingAddressGlobal = null;

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

document.getElementById("editBilling").addEventListener("click", () => {
    openAccountDetailsTab("billing");
})

document.getElementById("editShipping").addEventListener("click", () => {
    openAccountDetailsTab("shipping");
});

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

async function loadUserData() {
    try {
        const response = await fetch("api/profiles/userProfile");

        if (!response.ok) {
            Notiflix.Notify.failure("profile data loading failed", {
                position: 'center-top'
            });
            return;
        }

        if (response.redirected) {
            window.location.href = response.url;
            return;
        }

        const data = await response.json();
        const fullName = `${data.user.firstName} ${data.user.lastName}`;

        // Billing info
        document.getElementById("bill_name").innerText = fullName;
        document.getElementById("bill_line1").innerText = data.billingAddress?.lineOne ?? "";
        document.getElementById("bill_line2").innerText = data.billingAddress?.lineTwo ?? "";
        document.getElementById("bill_city").innerText = data.billingAddress?.cityName ?? "";

        billingAddressGlobal = data.billingAddress;
        shippingAddressGlobal = data.shippingAddress;

        // Shipping info
        document.getElementById("ship_name").innerText = fullName;
        document.getElementById("ship_line1").innerText = data.shippingAddress?.lineOne ?? "";
        document.getElementById("ship_line2").innerText = data.shippingAddress?.lineTwo ?? "";
        document.getElementById("ship_city").innerText = data.shippingAddress?.cityName ?? "";

        // Form fields
        document.getElementById("username").innerHTML = `Hello, ${data.user.firstName} ${data.user.lastName}`
        document.getElementById("firstName").value = data.user.firstName;
        document.getElementById("lastName").value = data.user.lastName;
        document.getElementById("mobile").value = data.billingAddress.mobile ?? "";

        document.getElementById("email").value = data.user.email ?? "";
        document.getElementById("lineOne").value = data.billingAddress?.lineOne ?? "";
        document.getElementById("lineTwo").value = data.billingAddress?.lineTwo ?? "";
        document.getElementById("postalCode").value = data.billingAddress?.postalCode ?? "";

        const citySelect = document.getElementById("citySelect");
        citySelect.value = data.billingAddress?.cityId ? String(data.billingAddress.cityId) : "";

        document.getElementById("currentPassword").value = data.user.password;

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

function toggleAddressType() {
    const selectedType = document.querySelector("input[name='addressType']:checked").value;

    let address = selectedType === "billing" ? billingAddressGlobal : shippingAddressGlobal;

    if (!address) {
        address = {
            lineOne: "",
            lineTwo: "",
            postalCode: "",
            mobile: "",
            cityId: ""
        };
    }

    document.getElementById("lineOne").value = address.lineOne ?? "";
    document.getElementById("lineTwo").value = address.lineTwo ?? "";
    document.getElementById("postalCode").value = address.postalCode ?? "";
    document.getElementById("mobile").value = address.mobile ?? "";

    const citySelect = document.getElementById("citySelect");
    citySelect.value = address.cityId ? String(address.cityId) : "";
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
    const cityValue = citySelect.value === "" ? null : parseInt(citySelect.value, 10);

    let mobile = document.getElementById("mobile");

    const addressTypeElem = document.querySelector("input[name='addressType']:checked");
    let addressType = addressTypeElem ? addressTypeElem.value : null;

    let currentPassword = document.getElementById("currentPassword");
    let newPassword = document.getElementById("newPassword");
    let confirmPassword = document.getElementById("confirmPassword");

    const userObject = {
        firstName: firstName.value,
        lastName: lastName.value,
        lineOne: lineOne.value,
        lineTwo: lineTwo.value,
        postalCode: postalCode.value,
        mobile: mobile.value,
        cityId: cityValue,
        password: currentPassword.value,
        newPassword: newPassword.value,
        confirmPassword: confirmPassword.value,
        addressType: addressType
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
                await loadUserData();
                toggleAddressType();
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        } else {
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

function openAccountDetailsTab(type) {
    document.querySelector('button[data-tab="general"]').click();
    document.querySelector(`input[name="addressType"][value="${type}"]`).checked = true;
    toggleAddressType();
}

async function userLogOut() {
    Notiflix.Loading.pulse("wait...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        const response = await fetch("api/users/logout", {
            method: "POST",
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