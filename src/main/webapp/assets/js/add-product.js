//ADD NEW PRODUCT PAGE

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
            renderDropdowns(brandSelect, data.brands);
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
            const categorySelect = document.getElementById("categorySelect");

            renderDropdowns(colorSelect, data.color);
            renderDropdowns(sizeSelect, data.size);
            renderDropdowns(categorySelect, data.category);

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

function renderDropdowns(selector, list) {
    selector.innerHTML = `<option value="0">Select</option>`;
    list.forEach((item) => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item.name;
        selector.appendChild(option);
    })
}

async function saveProduct() {
    let image1 = document.getElementById("img1");
    let image2 = document.getElementById("img2");
    let image3 = document.getElementById("img3");

    if (!image1.files.length || !image2.files.length || !image3.files.length) {
        Notiflix.Notify.failure("Please select 3 product images", {
            position: 'center-top'
        });
        return;
    }

    const allowedTypes = ["image/jpeg", "image/png", "image/jpg"];

    for (let img of [image1, image2, image3]) {
        if (!allowedTypes.includes(img.files[0].type)) {
            Notiflix.Notify.failure("Only JPG or PNG images are allowed", {
                position: 'center-top'
            });
            return;
        }
    }

    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });


    let productName = document.getElementById("title");
    let brandSelect = document.getElementById("brandSelect");
    let modelSelect = document.getElementById("modelSelect");
    let colorSelect = document.getElementById("colorSelect");
    let sizeSelect = document.getElementById("sizeSelect");
    let categorySelect = document.getElementById("categorySelect");
    let price = document.getElementById("price");
    let quantity = document.getElementById("quantity");
    let description = document.getElementById("description");


    const productDataObject = {
        productName: productName.value,
        brandId: brandSelect.value,
        modelId: modelSelect.value,
        colorId: colorSelect.value,
        sizeId: sizeSelect.value,
        categoryId: categorySelect.value,
        price: parseFloat(price.value),
        quantity: parseInt(quantity.value),
        description: description.value
    };

    const formData = new FormData();
    formData.append("product", JSON.stringify(productDataObject));

    try {
        const response = await fetch("api/products/save-product", {
            method: "POST",
            body: formData
        });

        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                await uploadProductImages(data.productId);
                Notiflix.Report.success(
                    'Envy Clothings',
                    "New product has been successfully created in the system.",
                    "okay",
                    () => {
                        window.location = "add-product.html"
                    },
                );
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        } else {
            Notiflix.Notify.failure("product details adding failed", {
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

async function uploadProductImages(productId) {

    let image1 = document.getElementById("img1");
    let image2 = document.getElementById("img2");
    let image3 = document.getElementById("img3");

    const formData = new FormData();
    formData.append("images[]", image1.files[0]);
    formData.append("images[]", image2.files[0]);
    formData.append("images[]", image3.files[0]);

    try {
        const response = await fetch(`api/products/${productId}/upload-images`, {
            method: "PUT",
            body: formData
        });

        if (response.ok) {
            const data = await response.json();
            if (data.status) {

            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }

        } else {
            Notiflix.Notify.failure("product images uploading failed", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}




