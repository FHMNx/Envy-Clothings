async function addToCart(stockId, qty) {
    try {

        const response = await fetch( `api/carts/add-to-cart?stockId=${stockId}&qty=${qty}`);
        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                Notiflix.Notify.success(data.message, {
                    position: 'center-top'
                });
                await loadCartItems();
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        } else {
            Notiflix.Notify.failure("Add to cart process failed!", {
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

async function loadCartItems() {
    try {
        Notiflix.Loading.standard("Loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        const response = await fetch("api/carts/load-carts");
        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                console.log(data);
                renderingMainPanel(data.cartItems);
            } else {
                Notiflix.Notify.info(data.message, {
                    position: 'center-top'
                });
            }
        } else {
            Notiflix.Notify.failure("Cart items loading failed!", {
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

function renderingMainPanel(cartItems) {
    const cartItemContainer = document.getElementById("cart-item-container");
    if (cartItemContainer) {
        cartItemContainer.innerHTML = "";

        let total = 0;
        let totalQty = 0;

        cartItems.forEach((cart) => {
            let itemTotal = parseFloat(cart.price) * parseInt(cart.qty);
            total += itemTotal;
            totalQty += parseInt(cart.qty);
            cartItemContainer.innerHTML += `<div class="cart-item">
            <div class="product">
                <img src="${cart.images[0]}">
                <div class="item-detail">
                    <p>${cart.productTitle}</p>
               
                </div>
            </div>

            <span class="price">Rs ${cart.price.toFixed(2)}</span>
            <div class="quantity"><input type="number" value="${cart.qty}" readonly></div>
            <span class="total-price">Rs ${itemTotal.toFixed(2)}</span>
            <button class="remove" onclick="removeCartItem(${cart.cartId});"><i class="bi bi-x-octagon"></i></button>
        </div>`;

        });

        document.getElementById("order-total-quantity").innerHTML = totalQty;
        document.getElementById("order-total-amount").innerHTML = new Intl.NumberFormat("en-US",
            {minimumFractionDigits: 2}).format(total);

    }
}

async function removeCartItem(cartId) {
    try {
        Notiflix.Loading.standard("Loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        const response = await fetch(`api/carts/remove-cart/${cartId}`, {
            method: "DELETE"
        });

        if (response.ok) {
            const data = await response.json();
            if (data.status) {
               window.location.reload();
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        } else {
            Notiflix.Notify.failure("Cart item removing failed!", {
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