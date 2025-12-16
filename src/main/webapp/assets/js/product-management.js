window.addEventListener("load", async () => {
    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        await loadBrands();
        await loadProductSpecifications();
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
            brandSelect.innerHTML = `<option value="0">Select</option>`;

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

    const brandSelect = document.getElementById("brandSelect");
    const modelSelect = document.getElementById("modelSelect");

    modelSelect.innerHTML = `<option value="0">Select Model</option>`;

    if (brandSelect.value === "0") {
        return;
    }

    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        const response = await fetch(`api/data/${brandSelect.value}/models`);

        if (!response.ok) {
            Notiflix.Notify.failure("Model loading failed", {
                position: 'center-top'
            });
            return;
        }

        const data = await response.json();

        if (!data.status) {
            Notiflix.Notify.failure(data.message, {
                position: 'center-top'
            });
            return;
        }

        data.models.forEach((model) => {
            const option = document.createElement("option");
            option.value = model.id;
            option.textContent = model.name;
            modelSelect.appendChild(option);
        });

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    } finally {
        Notiflix.Loading.remove(500);
    }
}

async function loadProductSpecifications() {
    try {
        const response = await fetch("api/data/specifications");

        if (response.ok) {
            const data = await response.json();
            const colorSelect = document.getElementById("colorSelect");
            const sizeSelect = document.getElementById("sizeSelect");

            renderDropdowns(colorSelect, data.color);
            renderDropdowns(sizeSelect, data.size);

        } else {
            Notiflix.Notify.failure("product specification loading failed", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

function renderDropdowns(selector, list, suffix) {
    selector.innerHTML = `<option value="0">Select</option>`;
    list.forEach((item) => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item.name;
        selector.appendChild(option);
    })
}

async function saveProduct() {
    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    } finally {
        Notiflix.Loading.remove(500);
    }
}
