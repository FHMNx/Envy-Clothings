let currentPage = 1;
const pageSize = 10;

document.addEventListener("DOMContentLoaded", async () => {
    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        await loadAllOrders(currentPage);
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
            console.log(data);

            const orderStatusMap = {
                13: { label: "Paid", class: "bg-light-success text-success border border-success" },
                2:  { label: "Pending", class: "bg-light-warning text-warning border border-warning" },
                6:  { label: "Processing", class: "bg-light-info text-info border border-info" },
                5:  { label: "Delivered", class: "bg-light-success text-success border border-success" },
                9:  { label: "Cancelled", class: "bg-light-danger text-danger border border-danger" }
            };


            let no = (page - 1) * pageSize + 1;
            data.orders.forEach((order) => {
                console.log("Order Status:", order.statusId);

                const status = orderStatusMap[order.statusId] ?? { label: "UNKNOWN", class: "bg-light-secondary" };

                tbody.innerHTML += `
                <tr>
                  <td>${no}</td>
                  <td>#000${order.orderId}</td>
                  <td>${order.customerName}</td>
                  <td>Rs ${new Intl.NumberFormat("en-US",{
                      minimumFractionDigits :2
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
                    <button class="btn btn-sm btn-light-primary me-1">
                      <i class="bi bi-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-light-warning me-1">
                      <i class="bi bi-pencil-square"></i>
                    </button>
                    <button class="btn btn-sm btn-light-danger">
                      <i class="bi bi-trash"></i>
                    </button>
                  </td>
                </tr>`;
                no++;
            });

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