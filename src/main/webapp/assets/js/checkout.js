window.addEventListener("load", async () => {
    try {
        Notiflix.Loading.standard("Loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        await loadCities();
        await loadCheckout();

    } finally {
        Notiflix.Loading.remove(1000);
    }
});

async function loadCheckout() {
    try {

        const response = await fetch("api/checkouts/user-checkout-data");

        if (response.redirected) {
            Notiflix.Report.info(
                'Envy Clothings',
                "please login first before proceed to checkout",
                "okay",
                () => {
                    window.location = "sign-in.html"
                },
            );
            return;
        }

        if (response.ok) {

            const data = await response.json();
            if (data.status) {
                console.log(data);
                makeOrderSummery(data);
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }

        } else {
            Notiflix.Notify.failure("checkout data loading failed", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

function makeOrderSummery(data) {
    const cartList = data.cartList;
    const deliveryTypes = data.deliveryTypes;
    const adminList = data.adminList;

    let tableBody = document.getElementById("st-tbody");
    let itemRow = document.getElementById("st-item-tr");
    let subTotalRow = document.getElementById("st-subtotal-tr");
    let orderShippingRow = document.getElementById("st-order-shipping-tr");
    let orderTotalRow = document.getElementById("st-order-total-tr");

    tableBody.innerHTML = "";
    let total = 0;
    let itemCount = 0;

    cartList.forEach((item) => {
        let itemRowClone = itemRow.cloneNode(true);
        itemRowClone.querySelector("#st-product-title").textContent = item.productName;
        itemRowClone.querySelector("#st-product-qty").textContent = item.quantity;
        let subTotal = parseFloat(item.price) * parseInt(item.quantity);

        itemRowClone.querySelector("#st-product-price").textContent = new Intl.NumberFormat("en-US", {
            minimumFractionDigits: 2
        }).format(subTotal);
        tableBody.appendChild(itemRowClone);
        total += subTotal;
        itemCount += item.quantity;
    });

    subTotalRow.querySelector("#st-product-total-amount").textContent = new Intl.NumberFormat("en-US", {
        minimumFractionDigits: 2
    }).format(total);

    let citySelect = document.getElementById("citySelect");
    citySelect.addEventListener("change", () => {
        let shippingCharges = 0;
        let cityName = citySelect.options[citySelect.selectedIndex]?.text || "";

        adminList.forEach((admin) => {
            if (cityName === admin.cityDTO.name) {
                //withing city
                shippingCharges += deliveryTypes[0].price;
            } else {
                //out of city
                shippingCharges += deliveryTypes[1].price;
            }
        });

        orderShippingRow.querySelector("#st-product-shipping-charges").textContent = new Intl.NumberFormat("en-US", {
            minimumFractionDigits: 2
        }).format(shippingCharges);

        orderTotalRow.querySelector("#st-order-total-amount").textContent = new Intl.NumberFormat("en-US", {
            minimumFractionDigits: 2
        }).format(total + shippingCharges);

    });

    tableBody.appendChild(subTotalRow);
    tableBody.appendChild(orderShippingRow);
    tableBody.appendChild(orderTotalRow);
}

async function loadCities() {
    try {

        const response = await fetch("api/data/cities");
        if (response.ok) {
            const data = await response.json();
            let citySelect = document.getElementById("citySelect");
            citySelect.innerHTML = `<option value="0">select</option>`

            data.cities.forEach((city) => {
                let option = document.createElement("option");
                option.value = city.id;
                option.innerHTML = city.name;
                citySelect.appendChild(option);
            });

        } else {
            Notiflix.Notify.failure("city loading failed", {
                position: 'center-top'
            });
        }


    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

