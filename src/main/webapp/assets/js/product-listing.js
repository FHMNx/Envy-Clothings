//PRODUCT LISTING PAGE

document.addEventListener("DOMContentLoaded", async () => {
    Notiflix.Loading.standard("Loading...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        await loadAllProducts();
    } finally {
        Notiflix.Loading.remove(500);
    }
});

async function loadAllProducts() {
    try {
        const tbody = document.getElementById("productTableBody");
        tbody.innerHTML = "";
        const response = await fetch("api/products/all");

        if (response.ok) {
            const data = await response.json();

            let no = 1;
            data.products.forEach((product) => {
                product.stockDTOList.forEach((stock) => {
                   tbody.innerHTML += ` <tr>
                                    <td>${no}</td>
                                    <td>${product.productName}</td>
                                    <td>${stock.price}</td>
                                    <td>${stock.quantity}</td>
                                    <td>${stock.createdAt}</td>
                                    <td>
                                        <span class="badge bg-light-success border border-success">Active</span>
                                    </td>
                                    <td class="text-end">
                                        <a href="edit-product.html" class="btn btn-sm btn-light-primary me-1">
                                            <i class="bi bi-pencil-square"></i>
                                        </a>
                                        <a class="btn btn-sm btn-light-danger">
                                            <i class="bi bi-trash"></i>
                                        </a>
                                    </td>
                                </tr>`;
                   no++;
                })
            })
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