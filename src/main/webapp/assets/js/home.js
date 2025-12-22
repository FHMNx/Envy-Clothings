document.addEventListener("DOMContentLoaded", async () => {
    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        await loadNewArrivals();
    } finally {
        Notiflix.Loading.remove(500);
    }
})

async function loadNewArrivals() {
    try {
        const response = await fetch("api/data/new-arrivals");
        if (response.ok) {
            const data = await response.json();
            renderNewArrivals(data.newArrivals);

        } else {
            Notiflix.Notify.failure("new arrivals loading failed", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

function renderNewArrivals(productList) {
    console.log(productList);

    const newArrival = document.getElementById("new-arrival");
    newArrival.innerHTML = "";

    productList.forEach((product) => {
        product.stockDTOList.forEach((stock) => {

            newArrival.innerHTML += `<div class="pro">
            <a href="single-product.html?productId=${product.productId}">
             <img src="${product.images[0]}" alt="">
            </a>
            <div class="des">
                <span>Addidas</span>
                <h5>${product.productName}</h5>
                <div class="star">
                    <i class="bx bx-star"></i>
                    <i class="bx bx-star"></i>
                    <i class="bx bx-star"></i>
                    <i class="bx bx-star"></i>
                    <i class="bx bx-star"></i>
                </div>
                <h4>Rs. ${new Intl.NumberFormat("en-US", {
                    minimumFractionDigits:2,}).format(stock.price)}</h4>
            </div>
            <a href="cart.html"><i class="bx bx-cart cart"></i></a>
        </div>`
        });
    });

    refreshAnimations();
}

function refreshAnimations() {
    if (typeof sal === "function") {
        sal();
    }
    if (typeof $ !== "undefined") {
        $('.categrie-product-activation').slick('refresh');
    }
}