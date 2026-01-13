document.addEventListener("DOMContentLoaded", async () => {
    await loadWishListItems();
})

async function toggleWishList(element, stockId) {
    try {
        const response = await fetch(`api/wishList/toggle?stockId=${stockId}`, {
            method: "POST"
        });

        const data = await response.json();

        if (!data.status) {
            Notiflix.Notify.failure(data.message, {
                position: 'center-top'
            });
            return;
        }

        document
            .querySelectorAll(`.like-btn[data-stock-id="${stockId}"]`)
            .forEach(btn => btn.classList.toggle("active"));

        const isActive = element.classList.contains("active");

        if (isActive) {
            Notiflix.Notify.success(data.message, {
                position: 'center-top'
            });
        } else {
            Notiflix.Notify.success(data.message, {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure("Something went wrong!", {
            position: 'center-top'
        });
        console.error(e);
    }
}

async function syncWishListIcons() {
    const response = await fetch("api/wishList/ids");
    const data = await response.json();

    if (!data.status) {
        return;
    }

    data.ids.forEach(stockId => {
        document
            .querySelectorAll(`.like-btn[data-stock-id="${stockId}"]`)
            .forEach(btn => btn.classList.add("active"));
    });
}

async function loadWishListItems() {
    try {
        Notiflix.Loading.standard("Loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        const response = await fetch("api/wishList/load-wishList");
        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                console.log(data.wishListItems);
                renderingWishListPanel(data.wishListItems);
            } else {

            }

        } else {
            Notiflix.Notify.failure("wishList items loading failed", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    } finally {
        Notiflix.Loading.remove();
    }
}

async function renderingWishListPanel(wishListItems) {
    const wishListItemContainer = document.getElementById("wishList-item-container");
    if (!wishListItemContainer) return;

    wishListItemContainer.innerHTML = "";

    wishListItems.forEach((wishList) => {
        wishListItemContainer.innerHTML += `<div class="wishlist-item">
            <div class="product">
                <img src="${wishList.image}">
                <div class="item-detail">
                    <p>${wishList.productName}</p>
                </div>
            </div>

            <span class="price">Rs ${new Intl.NumberFormat("en-US",{minimumFractionDigits:2}).format(wishList.price)}</span>

            <a class="add-cart-btn" onclick="addToCart('${wishList.stockId}', 1)">
                <i class="bi bi-cart-plus"></i> Add to Cart
            </a>

            <button class="remove" data-stock-id="${wishList.stockId}" data-wishlist-id="${wishList.wishListId || ''}" 
                onclick="removeWishListItem('${wishList.wishListId || wishList.stockId}', this)">
                <i class="bi bi-x-octagon"></i>
            </button>
        </div>`;
    });
}

async function removeWishListItem(wishListId,element) {
    try {
        Notiflix.Loading.standard("Loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        const response = await fetch(`api/wishList/remove-wishList/${wishListId}`, {
            method: "DELETE"
        });

        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                element.closest(".wishlist-item").remove();
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }

        } else {
            Notiflix.Notify.failure("WishList item removing failed!", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    } finally {
        Notiflix.Loading.remove();
    }
}
