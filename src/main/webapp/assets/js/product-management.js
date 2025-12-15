window.addEventListener("load", async () => {
    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        await loadBrands();
    } finally {
        Notiflix.Loading.remove(500);
    }
});


async function loadBrands() {
    try {
        const response = await fetch("api/data/brands");

        if (response.ok) {
            const data = await response.json();
            const brandSelect = document.getElementById("brandSelect");
            data.brands.forEach((brand) => {
                const option = document.createElement("option");
                option.value = brand.id;
                option.innerHTML = brand.name;
                brandSelect.appendChild(option);
            })
        } else {
            Notiflix.Notify.failure("brands loading failed", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

async function loadModels() {
    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    const brandSelect = document.getElementById("brandSelect");

    try {
        const response = await fetch(`api/data/${brandSelect.value}/models`);

        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                const modelSelect = document.getElementById("modelSelect");

                data.models.forEach((model) => {
                    const option = document.createElement("option");
                    option.value = model.id;
                    option.innerHTML = model.name;
                    modelSelect.appendChild(option);
                })
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        } else {
            Notiflix.Notify.failure("model loading failed", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    } finally {
        Notiflix.Loading.remove(500);
    }
}