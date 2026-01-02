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
    let firstName = document.getElementById("first-name");
    let lastName = document.getElementById("last-name");
    let email = document.getElementById("email");
    let lineOne = document.getElementById("line-one");
    let lineTwo = document.getElementById("line-two");
    let postalCode = document.getElementById("postal-code");
    let mobile = document.getElementById("mobile");
    let citySelect = document.getElementById("citySelect");

    const orderData = {
        firstName: firstName.value,
        lastName: lastName.value,
        email: email.value,
        lineOne: lineOne.value,
        lineTwo: lineTwo.value,
        postalCode: postalCode.value,
        mobile: mobile.value,
        citySelect: citySelect.value
    }

    const orderJsonData = JSON.stringify(orderData);

    try {
        Notiflix.Loading.standard("Loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        const response = await fetch("api/checkouts/user-checkout" , {
            method: "POST",
            headers : {
                "Content-Type":"application/json"
            },
            body: orderJsonData
        })

        if(response.ok){

            const data = await response.json();
            if(data.status){
                console.log(data)

            }else{
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }

        }else{
            Notiflix.Notify.failure("order placing failed", {
                position: 'center-top'
            });
        }

    }catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });

    } finally {
        Notiflix.Loading.remove();
    }
}



