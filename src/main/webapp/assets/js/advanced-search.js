let allProducts = [];
let currentPage = 1;
const productsPerPage = 9;


window.addEventListener("load", async () => {
    try {
        Notiflix.Loading.standard("Loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        await advancedSearchData();

    } finally {
        Notiflix.Loading.remove(1000);
    }
});

async function advancedSearchData() {
    try {
        const response = await fetch("api/advanced-search/all-data");
        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                console.log(data);
                renderingOptions("brand", data.brandList, "name");
                renderingOptions("category", data.categoryList, "name");
                renderingOptions("color", data.colorList, "name");
                renderingOptions("size", data.sizeList, "name");
                // updateProductView(data.productList)
                allProducts = data.productList;
                document.getElementById("all-item-count").innerText = allProducts.length;
                renderPage(1);

            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        } else {
            Notiflix.Notify.failure("product data loading failed", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

function renderPage(page) {
    currentPage = page;

    const start = (page - 1) * productsPerPage;
    const end = start + productsPerPage;

    const pageProducts = allProducts.slice(start, end);

    updateProductView(pageProducts);
    renderPagination();
}

function renderPagination() {
    const container = document.getElementById("pagination-container");
    container.innerHTML = "";

    const totalPages = Math.ceil(allProducts.length / productsPerPage);

    // PREVIOUS
    const prev = document.createElement("a");
    prev.innerText = "‹";
    prev.href = "#";
    prev.className = "px-3 py-2 border";
    if (currentPage === 1) prev.style.pointerEvents = "none";
    prev.onclick = (e) => {
        e.preventDefault();
        renderPage(currentPage - 1);
    };
    container.appendChild(prev);

    // PAGE NUMBERS
    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement("a");
        btn.innerText = i;
        btn.href = "#";
        btn.className = "px-3 py-2 border";

        if (i === currentPage) {
            btn.classList.add("bg-black", "text-white");
        }

        btn.onclick = (e) => {
            e.preventDefault();
            renderPage(i);
        };

        container.appendChild(btn);
    }

    // NEXT
    const next = document.createElement("a");
    next.innerText = "›";
    next.href = "#";
    next.className = "px-3 py-2 border";
    if (currentPage === totalPages) next.style.pointerEvents = "none";
    next.onclick = (e) => {
        e.preventDefault();
        renderPage(currentPage + 1);
    };
    container.appendChild(next);
}

function renderingOptions(prefix, dataList, property) {

    const optionBox = document.getElementById(prefix + "-options");
    optionBox.innerHTML = "";

    dataList.forEach((item) => {
        const li = document.createElement("li");
        const a = document.createElement("a");

        a.href = "#";
        a.innerText = item[property];
        // a.dataset.id = item.id;
        li.appendChild(a);

        li.addEventListener("click", (e) => {
            e.preventDefault();
            optionBox.querySelectorAll("li").forEach(x => x.classList.remove("chosen"));
            li.classList.add("chosen");
        });
        optionBox.appendChild(li);
    });
}

function updateProductView(dataList) {
    const productContainer = document.getElementById("product-container");
    productContainer.innerHTML = "";

    dataList.forEach((item) => {
        productContainer.innerHTML += `<div class="pro">
                    <a href="single-product.html?productId=${item.productId}">
                        <img src="${item.images[0]}" alt="">
                    </a>
                    <a href="#" class="like-btn">
                        <i class="bx bxs-heart"></i>
                    </a>

                    <div class="des">
                        <span>Addidas</span>
                        <h5>${item.productName}</h5>
                        <div class="star">
                            <i class="bx bx-star"></i>
                            <i class="bx bx-star"></i>
                            <i class="bx bx-star"></i>
                            <i class="bx bx-star"></i>
                            <i class="bx bx-star"></i>
                        </div>
                        <h4>Rs ${new Intl.NumberFormat("en-US", {
            minimumFractionDigits: 2
        }).format(item.price)}</h4>
                    </div>

                    <a href="#" onclick="addToCart(${item.stockId}, 1);"><i class="bx bx-cart cart"></i></a>
                </div>`;
    });

    //PAGINATION

    // let pagination_container = document.getElementById("pagination-container");
    // pagination_container.innerText = "";
    //
    // let allProductCount = dataList.allProductCount;
    // document.getElementById("all-item-count").innerHTML = allProductCount;
    // let product_per_page = data.maxResult;
    // let pages = Math.ceil(allProductCount / product_per_page);
    //
    // if (currentPage !== 0) {
    //     let previousBtn = stPaginationBtn.cloneNode(true);
    //     previousBtn.innerHTML = "<";
    //     previousBtn.addEventListener("click", async (evt) => {
    //         currentPage--;
    //         await searchProduct(currentPage * product_per_page);
    //         evt.preventDefault();
    //     });
    //
    //     pagination_container.appendChild(previousBtn);
    // }
    //
    // for (let i = 0; i < pages; i++) {
    //     let paginationBtn = stPaginationBtn.cloneNode(true);
    //     paginationBtn.innerHTML = i + 1;
    //     paginationBtn.addEventListener("click", async (evt) => {
    //         currentPage = i;
    //         await searchProduct(i * product_per_page);
    //         evt.preventDefault();
    //     });
    //
    //     if (i === parseInt(currentPage)) {
    //         paginationBtn.className = "";
    //     } else {
    //         paginationBtn.className = "";
    //     }
    //
    //     pagination_container.appendChild(paginationBtn);
    // }
    //
    // if (currentPage !== (pages - 1)) {
    //     let nextBtn = stPaginationBtn.cloneNode(true);
    //     nextBtn.innerText = ">";
    //     nextBtn.addEventListener("click", async (evt) => {
    //         currentPage++;
    //         await searchProduct(currentPage * product_per_page);
    //         evt.preventDefault();
    //         pagination_container.appendChild(nextBtn);
    //     });
    // }
}

async function searchProduct(firstResult) {

}
