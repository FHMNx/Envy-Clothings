let currentPage = 1;
const pageSize = 10;

document.addEventListener("DOMContentLoaded", async () => {
    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        await loadAllCustomers(currentPage);
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
                10: { label: "Verified", class: "bg-light-success text-success border border-success" },
                2:  { label: "Pending", class: "bg-light-warning text-warning border border-warning" },
                4:  { label: "Blocked", class: "bg-light-danger text-danger border border-danger" }
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
                        <button class="btn btn-sm btn-light-warning me-1">
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
