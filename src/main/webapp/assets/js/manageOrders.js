let currentPage = 1;
const pageSize = 10;
let currentOrderId = null;

document.addEventListener("DOMContentLoaded", async () => {
    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        await loadAllOrders(currentPage);
        await loadStatus();
    } finally {
        Notiflix.Loading.remove(500);
    }
});

async function loadAllOrders(page = 1) {
    try {
        const tbody = document.getElementById("order-table-body");
        tbody.innerHTML = "";

        const response = await fetch(`api/orders/all?page=${page}&size=${pageSize}`);

        if (response.ok) {
            const data = await response.json();
            if(data.status){

                const orderStatusMap = {
                    13: { label: "Paid", class: "bg-light-success text-success border border-success" },
                    2:  { label: "Pending", class: "bg-light-warning text-warning border border-warning" },
                    6:  { label: "Processing", class: "bg-light-info text-info border border-info" },
                    5:  { label: "Delivered", class: "bg-light-success text-success border border-success" },
                    9:  { label: "Cancelled", class: "bg-light-danger text-danger border border-danger" }
                };


                let no = (page - 1) * pageSize + 1;
                data.orders.forEach((order) => {
                    // console.log("Order Status:", order.statusId);

                    const status = orderStatusMap[order.statusId] ?? { label: "UNKNOWN", class: "bg-light-secondary" };

                    tbody.innerHTML += `
                <tr>
                  <td>${no}</td>
                  <td>#000${order.orderId}</td>
                  <td>${order.customerName}</td>
                  <td>Rs ${new Intl.NumberFormat("en-US", {
                        minimumFractionDigits: 2
                    }).format(order.total)}</td>
                  <td>
                      <span class="badge bg-light-primary border border-primary">
                          ${order.paymentTypeId === 1 ? 'CARD' : 'COD'}
                      </span>
                  </td>
                  <td>
                     <span class="badge ${status.class}">
                    ${status.label}
                </span>
                  </td>
                  <td>${order.createdAt}</td>
                  <td class="text-end">
                    <button class="btn btn-sm btn-light-warning me-1"  onclick="openOrderModal(${order.orderId}, 'edit')">
                      <i class="bi bi-pencil-square"></i>
                    </button>
                    <button class="btn btn-sm btn-light-danger" onclick="confirmDeleteOrder(${order.orderId});">
                      <i class="bi bi-trash"></i>
                    </button>
                  </td>
                </tr>`;
                    no++;
                });

            }else{
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }

        } else {
            Notiflix.Notify.failure("order data loading failed", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

function confirmDeleteOrder(orderId) {

    Notiflix.Confirm.show(
        'Delete Order',
        'Are you sure you want to delete this order?',
        'Delete',
        'Cancel',
        async () => {

            try {
                Notiflix.Loading.standard("Deleting...", {
                    svgColor: '#d33'
                });

                const response = await fetch(`api/orders/${orderId}/delete`, {
                    method: "DELETE"
                });

                if (response.ok) {
                    const data = await response.json();

                    if (data.status) {
                        Notiflix.Notify.success(data.message, {
                            position: 'center-top'
                        });
                        loadAllOrders(currentPage);

                    } else {
                        Notiflix.Notify.failure(data.message, {
                            position: 'center-top'
                        });
                    }

                } else {
                    Notiflix.Notify.failure("order deletion failed", {
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

        },
        () => {
            //CANCEL the Callback
        }
    );
}

async function openOrderModal(orderId) {
    currentOrderId = orderId;

    const modal = new bootstrap.Modal(document.getElementById("orderModal"));
    document.getElementById("orderModalTitle").innerText = "Order Information";
    document.getElementById("updateOrderBtn").style.display = "inline-block";

    await loadOrderDetails(orderId);
    modal.show();
}

async function loadOrderDetails(orderId) {
    try {
        const response = await fetch(`api/orders/${orderId}/OrderInfo`);

        if (response.ok) {
            const data = await response.json();

            if(data.status){
                // console.log(data);
                renderOrderDetails(data.order);

            }else{
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }

        }else{
            Notiflix.Notify.failure("order details loading failed", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

function renderOrderDetails(order) {

    document.getElementById("mOrderId").value = "#000" + order.orderId;
    document.getElementById("mCustomerName").value = order.customerName;
    document.getElementById("mMobile").value = order.mobile;
    document.getElementById("mPostalCode").value = order.postalCode;
    document.getElementById("mAddress1").value = order.addressLine1;
    document.getElementById("mAddress2").value = order.addressLine2;

    if (order.statusId) {
        document.getElementById("statusSelect").value = order.statusId;
    }

    const tbody = document.getElementById("orderItemsBody");
    tbody.innerHTML = "";

    order.items.forEach(item => {
        tbody.innerHTML += `
            <tr>
                <td>${item.productName}</td>
                <td class="text-center">${item.quantity}</td>
                <td>
                    Rs ${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price)}
                </td>
            </tr>
        `;
    });
}

async function loadStatus() {
    try {
        const response = await fetch("api/data/status");

        if (response.ok) {
            const data = await response.json();
            const statusSelect = document.getElementById("statusSelect");

            statusSelect.innerHTML = `<option value="" disabled selected>Select status</option>`;

            data.status.forEach((status) => {
                const option = document.createElement("option");
                option.value = status.id;
                option.textContent = status.name;
                statusSelect.appendChild(option);
            });

        } else {
            Notiflix.Notify.failure("Status loading failed", {
                position: "center-top"
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: "center-top"
        });
    }
}

document.getElementById("updateOrderBtn").addEventListener("click", async () => {
    try {
        const payload = {
            statusId: parseInt(document.getElementById("statusSelect").value)
        };

        const response = await fetch(`api/orders/${currentOrderId}/updateOrder`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (data.status) {
            Notiflix.Notify.success(data.message, {
                position: "center-top"
            });

            loadAllOrders(currentPage);
            bootstrap.Modal.getInstance(orderModal).hide();

        } else {
            Notiflix.Notify.failure(data.message, {
                position: "center-top"
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: "center-top"
        });
    }
});

