document.addEventListener("DOMContentLoaded", () => {

    const submitBtn = document.getElementById("submit");

    submitBtn.addEventListener("click", async (event) => {

        Notiflix.Loading.standard("loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        event.preventDefault();

        const fullName = document.getElementById("fullName").value;
        const phone = document.getElementById("phone").value;
        const email = document.getElementById("email").value;
        const topic = document.getElementById("topic").value;
        const message = document.getElementById("message").value;

        const messageObj = {
            fullName: fullName,
            phone: phone,
            email: email,
            topic: topic,
            message: message
        }

        try {
            const response = await fetch("api/users/messages",{
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(messageObj)
            });
            if (response.ok) {
                const data = await response.json();
                if (data.status) {
                    console.log(data);
                    Notiflix.Report.success(
                        'Envy Clothings',
                        data.message,
                        "okay",
                    );
                    window.location.reload();
                } else {
                    Notiflix.Notify.failure(data.message, {
                        position: 'center-top'
                    });
                }
            } else {
                Notiflix.Notify.failure("Message submission failed", {
                    position: 'center-top'
                });
            }

        } catch (error) {
            Notiflix.Notify.failure(error.message, {
                position: 'center-top'
            });
        } finally {
            Notiflix.Loading.remove(1000);
        }
    })

})