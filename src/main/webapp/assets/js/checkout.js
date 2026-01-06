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

                const billing = data.billingAddress;

                document.getElementById("first-name").value = billing.firstName;
                document.getElementById("last-name").value = billing.lastName;
                document.getElementById("email").value = billing.email;
                document.getElementById("line-one").value = billing.lineOne;
                document.getElementById("line-two").value = billing.lineTwo;
                document.getElementById("postal-code").value = billing.postalCode;
                document.getElementById("mobile").value = billing.mobile;
                document.getElementById("citySelect").value = billing.cityId;

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
    const shopCityId = data.shopCityId;

    const tableBody = document.getElementById("st-tbody");
    const templateRow = document.getElementById("st-item-tr");
    const subTotalRow = document.getElementById("st-subtotal-tr");
    const shippingRow = document.getElementById("st-order-shipping-tr");
    const totalRow = document.getElementById("st-order-total-tr");
    const citySelect = document.getElementById("citySelect");

    if (!tableBody || !templateRow) {
        console.error("Checkout table structure missing");
        return;
    }

    tableBody.innerHTML = "";
    let total = 0;

    cartList.forEach(item => {
        const row = templateRow.cloneNode(true);
        row.style.display = "";

        const productName = item.productTitle || "Unknown Product";
        const price = parseFloat(item.price || 0);
        const quantity = parseInt(item.qty || 1);

        row.querySelector(".st-product-title").textContent = `${productName} × ${quantity}`;
        const subtotal = price * quantity;
        row.querySelector(".st-product-subtotal").textContent = `Rs. ${subtotal.toFixed(2)}`;

        total += subtotal;
        tableBody.appendChild(row);
    });

    subTotalRow.querySelector("#st-product-total-amount").textContent = `Rs. ${total.toFixed(2)}`;
    tableBody.appendChild(subTotalRow);

    function updateShipping() {
        const selectedCityId = parseInt(citySelect.value) || 0;
        let shippingPrice = 0;

        if (deliveryTypes.length >= 2) {
            shippingPrice = (selectedCityId === shopCityId)
                ? parseFloat(deliveryTypes[0].price || 0)
                : parseFloat(deliveryTypes[1].price || 0);
        }

        shippingRow.querySelector("#st-product-shipping-charges").textContent = `Rs. ${shippingPrice.toFixed(2)}`;
        totalRow.querySelector("#st-order-total-amount").textContent = `Rs. ${(total + shippingPrice).toFixed(2)}`;
    }

    setTimeout(updateShipping, 50);
    citySelect.addEventListener("change", updateShipping);

    tableBody.appendChild(shippingRow);
    tableBody.appendChild(totalRow);
}

async function placeOrder() {
    const firstName = document.getElementById("first-name").value;
    const lastName = document.getElementById("last-name").value;
    const lineOne = document.getElementById("line-one").value;
    const lineTwo = document.getElementById("line-two").value;
    const postalCode = document.getElementById("postal-code").value;
    const mobile = document.getElementById("mobile").value;
    const note = document.getElementById("note").value;
    const cityId = parseInt(document.getElementById("citySelect").value);

    const paymentTypeId = getSelectedPaymentType();

    const orderData = {
        firstName,
        lastName,
        lineOne,
        lineTwo,
        postalCode,
        mobile,
        note,
        cityId,
        paymentTypeId
    };

    try {
        Notiflix.Loading.standard("Loading...", {
                clickToClose: false,
                svgColor: '#0284c7'
            }
        );

        if (paymentTypeId === 2) {
            // Cash on Delivery
            const response = await fetch("api/checkouts/user-checkout", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(orderData)
            });

            const data = await response.json();
            if (data.status) {
                Notiflix.Report.success(
                    "Envy Clothings",
                    data.message,
                    "Okay",
                    () => {
                        window.location = "index.html";
                    });
            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }

        } else {
            // Card / PayHere
            const response = await fetch("api/checkouts/user-checkout", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(orderData)
            });

            const data = await response.json();
            if (data.status && data.paymentDetails) {
                payhere.startPayment(data.paymentDetails);

            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {position: 'center-top'});
    } finally {
        Notiflix.Loading.remove();
    }
}

function getSelectedPaymentType() {
    const radios = document.getElementsByName("pay");
    return radios[0].checked ? 1 : 2;
}


// Payment completed. It can be a successful failure.
payhere.onCompleted = function onCompleted(orderId) {
    console.log("Payment completed. OrderID:" + orderId);
    // Note: validate the payment and show success or failure page to the customer
    Notiflix.Report.success(
        'Envy Clothings',
        "your order has been placed successfully",
        "okay",
        () => {
            window.location = "index.html"
        },
    );
};

// Payment window closed
payhere.onDismissed = function onDismissed() {
    // Note: Prompt user to pay again or show an error page
    console.log("Payment dismissed");
};

// Error occurred
payhere.onError = function onError(error) {
    // Note: show an error page
    console.log("Error:" + error);
};
