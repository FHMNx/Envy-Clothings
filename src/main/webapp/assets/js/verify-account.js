async function verifyAccount() {

    let verificationCode = document.querySelectorAll("#verificationCode input");

    try {
        const response = await fetch("api/verify-account?verificationCode=" + verificationCode.value);

        Notiflix.Loading.standard("loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                Notiflix.Loading.remove();
                Notiflix.Report.success(
                    'Envy Clothings',
                    data.message,
                    "okay",
                    () => {
                        window.location = "sign-in.html"
                    },
                );
            } else {

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
    }
}