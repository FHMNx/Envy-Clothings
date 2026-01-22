document.addEventListener("DOMContentLoaded", async () => {
    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        await loadWomenSection();
        await loadMenSection();
        await loadKidsSection();
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

async function renderNewArrivals(productList) {
    console.log(productList);

    const newArrival = document.getElementById("new-arrival");
    newArrival.innerHTML = "";

    productList.forEach((product) => {
        product.stockDTOList.forEach((stock) => {

            newArrival.innerHTML += `<div class="pro">
            <a href="single-product.html?productId=${product.productId}">
             <img src="${product.images[0]}" alt="">
              <a onclick="toggleWishList(this, '${stock.stockId}')" class="like-btn" data-stock-id="${stock.stockId}">
                <i class='bx bxs-heart'></i>
              </a>
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
                minimumFractionDigits: 2,
            }).format(stock.price)}</h4>
            </div>
            <a onclick="addToCart('${stock.stockId}', 1);"><i class="bx bx-cart cart"></i></a>
        </div>`
        });
    });

    await syncWishListIcons();

}

async function loadWomenSection() {
    try {

        const response = await fetch("api/data/women-section");
        if (response.ok) {
            const data = await response.json();
            renderWomenSection(data.womenSection);
        } else {
            Notiflix.Notify.failure("woman section loading failed", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

async function renderWomenSection(productList) {
    console.log(productList);

    const womenSection = document.getElementById("women-section");
    womenSection.innerHTML = "";

    productList.forEach((product) => {
        product.stockDTOList.forEach((stock) => {

            womenSection.innerHTML += `<div class="pro">
            <a href="single-product.html?productId=${product.productId}">
             <img src="${product.images[0]}" alt="">
              <a onclick="toggleWishList(this, '${stock.stockId}');" class="like-btn" data-stock-id="${stock.stockId}"><i class='bx bxs-heart'></i></a>
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
                minimumFractionDigits: 2,
            }).format(stock.price)}</h4>
            </div>
            <a onclick="addToCart(${stock.stockId}, 1)"><i class="bx bx-cart cart"></i></a>
        </div>`
        });
    });

    await syncWishListIcons();
}

async function loadKidsSection() {
    try {

        const response = await fetch("api/data/kids-section");
        if (response.ok) {
            const data = await response.json();
            renderKidsSection(data.kidsSection);
        } else {
            Notiflix.Notify.failure("kids section loading failed", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

async function renderKidsSection(productList) {
    console.log(productList);

    const kidsSection = document.getElementById("kids-section");
    kidsSection.innerHTML = "";

    productList.forEach((product) => {
        product.stockDTOList.forEach((stock) => {

            kidsSection.innerHTML += `<div class="pro">
            <a href="single-product.html?productId=${product.productId}">
             <img src="${product.images[0]}" alt="">
              <a onclick="toggleWishList(this, '${stock.stockId}');" class="like-btn" data-stock-id="${stock.stockId}">
                <i class='bx bxs-heart'></i>
              </a>
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
                minimumFractionDigits: 2,
            }).format(stock.price)}</h4>
            </div>
            <a onclick="addToCart('${stock.stockId}', 1);"><i class="bx bx-cart cart"></i></a>
        </div>`
        });
    });

    await syncWishListIcons();

}

async function loadMenSection() {
    try {

        const response = await fetch("api/data/men-section");
        if (response.ok) {
            const data = await response.json();
            renderMenSection(data.menSection);
        } else {
            Notiflix.Notify.failure("Men section loading failed", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

async function renderMenSection(productList) {
    console.log(productList);

    const menSection = document.getElementById("men-section");
    menSection.innerHTML = "";

    productList.forEach((product) => {
        product.stockDTOList.forEach((stock) => {

            menSection.innerHTML += `<div class="pro">
            <a href="single-product.html?productId=${product.productId}">
             <img src="${product.images[0]}" alt="">
              <a onclick="toggleWishList(this, '${stock.stockId}');" class="like-btn" data-stock-id="${stock.stockId}">
                <i class='bx bxs-heart'></i>
              </a>
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
                minimumFractionDigits: 2,
            }).format(stock.price)}</h4>
            </div>
            <a onclick="addToCart('${stock.stockId}', 1)"><i class="bx bx-cart cart"></i></a>
        </div>`
        });
    });

    await syncWishListIcons();

}



