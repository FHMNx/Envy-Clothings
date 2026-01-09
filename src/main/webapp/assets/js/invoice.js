const params = new URLSearchParams(window.location.search);
const orderId = params.get("orderId");

window.addEventListener("load", async () => {
    if (orderId) {
        await loadInvoiceData(orderId);
    }
})

async function loadInvoiceData(orderId) {
    try {

        Notiflix.Loading.standard("Loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        const response = await fetch(`api/invoices/user-invoice?orderId=${orderId}`);
        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                // console.log(data);
                const invoice = data.invoiceData;

                document.getElementById("invoice-no").innerHTML = `#${invoice.invoiceNo}`;
                document.getElementById("invoice-date").innerHTML = invoice.invoiceDate;
                document.getElementById("buyer-name").innerHTML = invoice.buyerName;
                document.getElementById("buyer-address").innerHTML = invoice.address;
                document.getElementById("city-name").innerHTML = invoice.cityName;
                document.getElementById("country-name").innerHTML = invoice.countryName;
                document.getElementById("buyer-email").innerHTML = invoice.email;

                const currencyFormatter = new Intl.NumberFormat("en-US", {
                    minimumFractionDigits: 2
                });

                let subTotal = 0;
                const itemBody = document.getElementById("tbody");
                invoice.invoiceItemDTOList.forEach((item, index) => {
                    const totalItemPrice = item.itemPrice * item.itemQty;
                    subTotal += totalItemPrice;

                    itemBody.innerHTML += `
                             <tr>
                                <td>${index + 1}</td>
                                <td>${item.itemName}</td>
                                <td>${item.itemQty}</td>
                                <td>Rs. ${currencyFormatter.format(item.itemPrice)}</td>
                                <td>Rs. ${currencyFormatter.format(totalItemPrice)}</td>
                            </tr>
                        `;
                    });

                document.getElementById("subTotal").innerHTML = `Rs. ${currencyFormatter.format(subTotal)}`;

                document.getElementById("shippingCost").innerHTML = `Rs. ${currencyFormatter.format(invoice.shippingCost)}`;

                document.getElementById("total").innerHTML = `Rs. ${currencyFormatter.format(subTotal + invoice.shippingCost)}`;

            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        } else {
            Notiflix.Notify.failure("invoice data loading failed", {
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