let currentPage = 1;
const pageSize = 10;

document.addEventListener("DOMContentLoaded", async () => {
    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        await loadAllProducts(currentPage);
    } finally {
        Notiflix.Loading.remove(500);
    }
});

async function loadAllProducts(page = 1) {
    try {
        const tbody = document.getElementById("productTableBody");
        tbody.innerHTML = "";
        const response = await fetch(`api/products/all?page=${page}&size=${pageSize}`);

        if (response.ok) {
            const data = await response.json();
            console.log(data);

            let no = (page - 1) * pageSize + 1;
            data.products.forEach((product) => {
                product.stockDTOList.forEach((stock) => {
                    console.log("Stock Status:", stock.status);
                    tbody.innerHTML += ` <tr>
                                    <td>${no}</td>
                                    <td>${product.productName}</td>
                                    <td>${stock.price}</td>
                                    <td>${stock.quantity}</td>
                                    <td>${stock.createdAt}</td>
                                    <td>
                                        <span class="badge ${stock.statusId === 15 ? 'bg-light-success border border-success' : 'bg-light-danger border border-danger text-danger'} ">
                                            ${stock.statusId === 15 ? 'IN STOCK' : 'OUT OF STOCK'}
                                        </span>
                                    </td>
                                    <td class="text-end">
                                        <a href="edit-product.html?productId=${product.productId}" class="btn btn-sm btn-light-primary me-1">
                                            <i class="bi bi-pencil-square"></i>
                                        </a>
                                        <a class="btn btn-sm btn-light-danger" onclick="confirmDeleteProduct(${product.productId});">
                                            <i class="bi bi-trash"></i>
                                        </a>
                                    </td>
                                </tr>`;
                    no++;
                });
            });

            renderAdminPagination(data.currentPage, data.totalPages);

        } else {
            Notiflix.Notify.failure("product data loading failed", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

function renderAdminPagination(current, total) {
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

function confirmDeleteProduct(productId) {

    Notiflix.Confirm.show(
        'Delete Product',
        'Are you sure you want to delete this product?',
        'Delete',
        'Cancel',
        async () => {

            try {
                Notiflix.Loading.standard("Deleting...", {
                    svgColor: '#d33'
                });

                const response = await fetch(`api/products/${productId}/delete`, {
                    method: "DELETE"
                });

                if (response.ok) {
                    const data = await response.json();

                    if (data.status) {
                        Notiflix.Notify.success(data.message, {
                            position: 'center-top'
                        });
                        loadAllProducts(currentPage);

                    } else {
                        Notiflix.Notify.failure(data.message, {
                            position: 'center-top'
                        });
                    }

                } else {
                    Notiflix.Notify.failure("product deletion failed", {
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


