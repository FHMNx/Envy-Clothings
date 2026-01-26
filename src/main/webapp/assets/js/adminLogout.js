async function adminLogout(){
    Notiflix.Loading.pulse("wait...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        const response = await fetch("api/admin/logout", {
            method: "POST",
            credentials: "include"
        });

        if (response.status === 200) {
            Notiflix.Report.success(
                'Envy Clothings',
                "logout successful",
                "okay",
                () => {
                    window.location = "index.html"
                },
            );
        } else {
            Notiflix.Notify.failure("log out process failed", {
                position: 'center-top'
            });
        }

    }catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    } finally {
        Notiflix.Loading.remove(1000);
    }
}