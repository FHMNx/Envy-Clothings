let params = new URLSearchParams(window.location.search);
const verificationInputs = document.querySelectorAll("#verificationCode input");
const code = params.get("verificationCode");
const userEmail = params.get("email");

if (code && code.length === verificationInputs.length) {
    verificationInputs.forEach((input, index) => {
        input.value = code[index];
    });
}

async function verifyAccount() {
    Notiflix.Loading.standard("loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    const enteredCode = Array.from(verificationInputs).map(i => i.value).join("");

    const verifyObject = {
        email: userEmail,
        verificationCode: enteredCode
    }

    try {
        const response = await fetch("api/verify-account", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(verifyObject)
        });


        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                Notiflix.Report.success(
                    'Envy Clothings',
                    data.message,
                    "okay",
                    () => {
                        window.location = "sign-in.html"
                    },
                );
            } else {
                Notiflix.Notify.failure(data.message,{
                    position: 'center-top'
                });
            }
        } else {
            Notiflix.Notify.failure("Verification process failed", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e, {
            position: 'center-top'
        });
    } finally {
        Notiflix.Loading.remove(1000);
    }
}