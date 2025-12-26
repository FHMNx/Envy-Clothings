// EDIT SELECTED PRODUCT

let params = new URLSearchParams(window.location.search);
const productId = params.get("productId");

window.addEventListener("load", async () => {
    try {
        Notiflix.Loading.standard("Loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        await loadBrands();
        await loadProductSpecifications();
        await loadSelectedProduct();

    } finally {
        Notiflix.Loading.remove(1000);
    }
});

async function loadSelectedProduct() {
    try {
        const response = await fetch(`api/products/get-selected-product?productId=${productId}`);
        if (response.ok) {

            const data = await response.json();
            const product = data.editProduct;
            console.log(product);

            product.images.forEach((image, index) => {
                const preview = document.getElementById(`previewImage${index + 1}`);
                if (preview) {
                    preview.src = image;
                }
            });

            document.getElementById("title").value = product.productName;
            document.getElementById("description").value = product.description;

            document.getElementById("price").value = product.stockDTOList[0].price;
            document.getElementById("quantity").value = product.stockDTOList[0].quantity;

            document.getElementById("brandSelect").value = product.brandId;
            await loadModels();
            document.getElementById("modelSelect").value = product.modelId;
            document.getElementById("colorSelect").value = product.colorId;
            document.getElementById("sizeSelect").value = product.sizeId;
            document.getElementById("categorySelect").value = product.categoryId;

        } else {
            Notiflix.Notify.failure("edit product loading failed", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

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


async function updateSelectedProduct() {
    let image1 = document.getElementById("img1");
    let image2 = document.getElementById("img2");
    let image3 = document.getElementById("img3");

    const allowedTypes = ["image/jpeg", "image/png", "image/jpg"];

    for (let img of [image1, image2, image3]) {
        if (img.files.length > 0) {
            if (!allowedTypes.includes(img.files[0].type)) {
                Notiflix.Notify.failure("Only JPG or PNG images are allowed", {
                    position: 'center-top'
                });
                return;
            }
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
        productId: productId,
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

        const response = await fetch("api/products/update-product", {
            method: "PUT",
            body: formData
        });

        if (response.ok) {
            const data = await response.json();
            if (data.status) {

                if (image1.files.length > 0 || image2.files.length > 0 || image3.files.length > 0) {
                    await updateProductImages(productId);
                }

                Notiflix.Report.success(
                    'Envy Clothings',
                    "product has been successfully updated in the system.",
                    "okay",
                    () => {
                        window.location = "product-listing.html"
                    },
                );
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        } else {
            Notiflix.Notify.failure("product updating failed", {
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

async function updateProductImages(productId) {

    const formData = new FormData();

    const imgInputs = [
        document.getElementById("img1"),
        document.getElementById("img2"),
        document.getElementById("img3")
    ];

    imgInputs.forEach((input, index) => {
        if (input.files.length > 0) {
            formData.append("images", input.files[0]);
            formData.append("indexes", index.toString());
        }
    });

    const response = await fetch(`api/products/${productId}/update-images`, {
        method: "PUT",
        body: formData
    });

    const data = await response.json();
    if (!data.status) {
        Notiflix.Notify.failure(data.message);
    }
}
