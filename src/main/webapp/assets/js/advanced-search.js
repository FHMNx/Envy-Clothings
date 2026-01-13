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
            optionBox.querySelectorAll("li").forEach(x => {
                x.classList.remove("chosen");
            });
            li.classList.add("chosen");
        });

        optionBox.appendChild(li);

        li.addEventListener("click", (e) => {
            e.preventDefault();

            optionBox.querySelectorAll("li").forEach(x => {
                x.classList.remove("chosen");
            });

            li.classList.add("chosen");

            searchProduct(0); // auto refresh with filter
        });

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
}

async function searchProduct(firstResult) {
    try {
        Notiflix.Loading.standard("Loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        const brandName = document.getElementById("brand-options")
            .querySelector(".chosen")?.querySelector("a").innerHTML;

        const categoryOption = document.getElementById("category-options")
            .querySelector(".chosen")?.querySelector("a").innerHTML;

        const colorOption = document.getElementById("color-options")
            .querySelector(".chosen")?.querySelector("a").innerHTML;

        const sizeOption = document.getElementById("size-options")
            .querySelector(".chosen")?.querySelector("a").innerHTML;

        const priceStart = 0;
        const priceEnd = document.getElementById("amount").value;

        const sortProduct = document.getElementById("sort").value;

        const searchData = {
            firstResult: firstResult,
            brandName: brandName,
            categoryName: categoryOption,
            colorName: colorOption,
            sizeName: sizeOption,
            priceStart: priceStart,
            priceEnd: priceEnd,
            sortProduct: sortProduct
        };

        const searchDataJson = JSON.stringify(searchData);

        const response = await fetch("api/advanced-search/search-data", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: searchDataJson
        })

        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                console.log(data);
                updateProductView(data.productList);
                document.getElementById("all-item-count").innerText = data.allProductCount;
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }

        } else {
            Notiflix.Notify.failure("Search operation failed!", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    } finally {
        Notiflix.Loading.remove(1000);
    }
}

const priceSlider = document.getElementById("amount");
const priceMaxLabel = document.getElementById("price-max");

priceSlider.addEventListener("input", () => {
    priceMaxLabel.innerText = "Rs." + Number(priceSlider.value).toLocaleString();
    searchProduct(0);
});


function resetFilters() {
    const prefixArray = ["brand", "category", "color", "size"];
    prefixArray.forEach(prefix => {
        document.querySelectorAll(`#${prefix}-options li`)
            .forEach(li => li.classList.remove("chosen"));
    });
    const priceSlider = document.getElementById("amount");
    priceSlider.value = priceSlider.max;
    document.getElementById("price-max").innerText = "Rs." + Number(priceSlider.max).toLocaleString();

    searchProduct(0);
}
