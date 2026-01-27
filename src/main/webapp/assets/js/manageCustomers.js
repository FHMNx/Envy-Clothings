let currentPage = 1;
const pageSize = 10;
let currentCustomerId = null;


document.addEventListener("DOMContentLoaded", async () => {
    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        await loadAllCustomers(currentPage);
        await loadStatus();
    } finally {
        Notiflix.Loading.remove(500);
    }
});

async function loadAllCustomers(page = 1) {
    try {
        const tbody = document.getElementById("cus-table-body");
        tbody.innerHTML = "";

        const response = await fetch(`api/users/all?page=${page}&size=${pageSize}`);

        if (response.ok) {
            const data = await response.json();
            console.log(data);

            const userStatusMap = {
                10: {label: "Verified", class: "bg-light-success text-success border border-success"},
                2: {label: "Pending", class: "bg-light-warning text-warning border border-warning"},
                4: {label: "Blocked", class: "bg-light-danger text-danger border border-danger"}
            };

            let no = (page - 1) * pageSize + 1;

            data.users.forEach((user) => {

                const status = userStatusMap[user.statusId] ?? {
                    label: "Unknown",
                    class: "bg-light-secondary text-secondary border border-secondary"
                };

                const mobile = user.setAddressDTOList?.length > 0 ? user.setAddressDTOList[0].mobile : "-";

                tbody.innerHTML += `
                 <tr>
                    <td>${no}</td>
                    <td>${user.firstName} ${user.lastName}</td>
                    <td>${user.email}</td>
                    <td>${mobile}</td>
                    <td>
                        <span class="badge ${status.class}">
                            ${status.label}
                        </span>
                    </td>
                    <td>${user.createdAt}</td>
                    <td class="text-end">
                        <button class="btn btn-sm btn-light-warning me-1"  onclick="openCustomerModal(${user.id}, 'edit')">
                            <i class="bi bi-pencil-square"></i>
                        </button>
                    </td>
                </tr>`;
                no++;
            });

            renderUserPagination(data.currentPage, data.totalPages);

        } else {
            Notiflix.Notify.failure("customer data loading failed", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

function renderUserPagination(current, total) {
    const pagination = document.querySelector(".pagination");
    pagination.innerHTML = "";

    pagination.innerHTML += `
        <li class="page-item ${current === 1 ? "disabled" : ""}">
            <a class="page-link" onclick="changePage(${current - 1})">Previous</a>
        </li>
    `;

    for (let i = 1; i <= total; i++) {
        pagination.innerHTML += `
            <li class="page-item ${i === current ? "active" : ""}">
                <a class="page-link" onclick="changePage(${i})">${i}</a>
            </li>
        `;
    }

    pagination.innerHTML += `
        <li class="page-item ${current === total ? "disabled" : ""}">
            <a class="page-link" onclick="changePage(${current + 1})">Next</a>
        </li>
    `;
}

function changePage(page) {
    currentPage = page;
    loadAllProducts(currentPage);
}

async function openCustomerModal(id) {
    currentCustomerId = id;

    const modal = new bootstrap.Modal(document.getElementById("customerModal"));
    document.getElementById("customerModalTitle").innerText = "Customer Information";
    document.getElementById("updateCustomerBtn").style.display = "inline-block";

    await loadCustomerDetails(id);
    modal.show();
}

async function loadCustomerDetails(id) {
    try {
        const response = await fetch(`api/users/${id}/userInfo`);

        if (response.ok) {
            const data = await response.json();

            if (data.status) {
                console.log(data);
                renderCustomerDetails(data.customer);

            } else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }

        } else {
            Notiflix.Notify.failure("customer details loading failed", {
                position: 'center-top'
            });
        }

    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

function renderCustomerDetails(customer) {
    document.getElementById("mCustomerId").value = "#000" + customer.id;
    document.getElementById("mPostalCode").value = customer.setAddressDTOList[0].postalCode;
    document.getElementById("mAddress1").value = customer.setAddressDTOList[0].lineOne;
    document.getElementById("mAddress2").value = customer.setAddressDTOList[0].lineTwo;
    document.getElementById("statusSelect").value = customer.statusId;
}

async function loadStatus() {
    try {
        const response = await fetch("api/users/status");

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

document.getElementById("updateCustomerBtn").addEventListener("click", async () => {
    try {
        const payload = {
            statusId: parseInt(document.getElementById("statusSelect").value)
        };

        const response = await fetch(`api/users/${currentCustomerId}/updateCustomer`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (data.status) {
            Notiflix.Notify.success(data.message, {
                position: "center-top"
            });

            loadAllCustomers(currentPage);
            bootstrap.Modal.getInstance(customerModal).hide();

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


