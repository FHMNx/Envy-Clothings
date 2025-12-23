let params = new URLSearchParams(window.location.search);
const productId = params.get("productId");

window.addEventListener("load", async () => {
    try {
        Notiflix.Loading.standard("Loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        await loadSingleProduct();

    } finally {
        Notiflix.Loading.remove(1000);
    }
});

async function loadSingleProduct() {
    try {
        const response = await fetch(`api/products/single-product?productId=${productId}`);
        if (response.ok) {
            const data = await response.json();

            const product = data.singleProduct;
            console.log(product);
            product.images.forEach((image, index) => {
                const mainImage = document.getElementById("image1");
                const thumbImage = document.getElementById(`thumb-image${index + 1}`);

                if (mainImage && index === 0) {
                    mainImage.src = image;
                }
                if (thumbImage) {
                    thumbImage.src = image;
                }
            });

            document.getElementById("title").innerHTML = product.productName;
            document.getElementById("price").innerHTML = new Intl.NumberFormat("en-US", {
                minimumFractionDigits: 2
            }).format(product.stockDTOList[0].price);

            document.getElementById("brand").innerHTML = product.brandName;
            document.getElementById("desc").innerHTML = product.description;
            document.getElementById("color-options").innerHTML = "Color : " + product.colorName;

            const sizeBox = document.getElementById("size-options");
            sizeBox.innerHTML = "";

            const sizeBtn = document.createElement("button");
            sizeBtn.innerText = product.sizeName;
            sizeBtn.classList.add("selected");
            sizeBox.appendChild(sizeBtn);



            const stockQty = product.stockDTOList[0].quantity;
            document.getElementById("stock-info").innerText = `Only ${stockQty} items available`;

            qtyPlus.addEventListener("click", () => {
                if (parseInt(qtyInput.value) >= stockQty) {
                    Notiflix.Notify.warning("Maximum quantity reached", {
                        position: "center-top"
                    });
                    qtyPlus.disable();
                }
            });

        } else {
            Notiflix.Notify.failure("single product loading failed", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}