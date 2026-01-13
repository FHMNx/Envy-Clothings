class HeaderContent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <nav>
            <input type="checkbox" id="search-toggle" hidden>

            <div class="header-wrapper">

                <a href="index.html" class="brand">
                    <img src="http://localhost:8080/Envy_Clothings/assets/images/logo/envy.png" style="height:60px;" />
                </a>

                <!-- MENU -->
                <ul class="nav-menu">

                    <li><a href="#">New Arrivals</a></li>

                    <!-- MEN -->
                    <li>
                        <a href="#">Men <i class='bx bx-chevron-down'></i></a>
                        <div class="dropdown-menu">
                            <div class="header-wrapper">
                                <div class="left-section">
                                    <span class="dropdown-close"><i class='bx bx-chevron-left'></i> Back</span>
                                    <h1>Men's Collection</h1>
                                    <p>Shop the latest for men – clothing, footwear, accessories and more.</p>
                                    <a href="#" class="btn-see-all">Shop All Men</a>
                                </div>

                                <div class="right-section">
                                    <h3>Categories</h3>
                                    <ul class="dropdown-links">
                                        <li>
                                            <a href="#"><i class='bx bx-male'></i>
                                                <div>
                                                    <h5>Clothing</h5>
                                                    <p>T-shirts, Jeans, Jackets, Formals</p>
                                                </div>
                                            </a>
                                        </li>
                                        <li>
                                            <a href="#"><i class='bx bx-run'></i>
                                                <div>
                                                    <h5>Footwear</h5>
                                                    <p>Sneakers, Sandals, Formal Shoes</p>
                                                </div>
                                            </a>
                                        </li>
                                        <li>
                                            <a href="#"><i class='bx bx-watch'></i>
                                                <div>
                                                    <h5>Accessories</h5>
                                                    <p>Watches, Wallets, Belts</p>
                                                </div>
                                            </a>
                                        </li>
                                        <li>
                                            <a href="#"><i class='bx bx-trophy'></i>
                                                <div>
                                                    <h5>Sportswear</h5>
                                                    <p>Active wear and gym essentials</p>
                                                </div>
                                            </a>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </li>

                    <!-- WOMEN -->
                    <li>
                        <a href="#">Women <i class='bx bx-chevron-down'></i></a>
                        <div class="dropdown-menu">
                            <div class="header-wrapper">
                                <div class="left-section">
                                    <span class="dropdown-close"><i class='bx bx-chevron-left'></i> Back</span>
                                    <h1>Women's Collection</h1>
                                    <p>Explore the latest styles – clothing, beauty, footwear and more.</p>
                                    <a href="#" class="btn-see-all">Shop All Women</a>
                                </div>

                                <div class="right-section">
                                    <h3>Categories</h3>
                                    <ul class="dropdown-links">
                                        <li>
                                            <a href="#"><i class='bx bx-female'></i>
                                                <div>
                                                    <h5>Clothing</h5>
                                                    <p>Dresses, Tops, Sarees, Kurtas</p>
                                                </div>
                                            </a>
                                        </li>
                                        <li>
                                            <a href="#"><i class='bx bx-diamond'></i>
                                                <div>
                                                    <h5>Accessories</h5>
                                                    <p>Jewelry, Bags, Sunglasses</p>
                                                </div>
                                            </a>
                                        </li>
                                        <li>
                                            <a href="#"><i class='bx bx-brush'></i>
                                                <div>
                                                    <h5>Beauty</h5>
                                                    <p>Cosmetics, Skincare, Haircare</p>
                                                </div>
                                            </a>
                                        </li>
                                        <li>
                                            <a href="#"><i class='bx bx-walk'></i>
                                                <div>
                                                    <h5>Footwear</h5>
                                                    <p>Heels, Sandals, Sneakers</p>
                                                </div>
                                            </a>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </li>

                    <li><a href="#">Kids</a></li>
                    <li><a href="#">Offers</a></li>

                    <!-- PROFILE -->
                    <li>
                        <a href="#">Profile <i class='bx bx-chevron-down'></i></a>
                        <div class="dropdown-menu">
                            <div class="header-wrapper">
                                <div class="left-section">
                                    <span class="dropdown-close"><i class='bx bx-chevron-left'></i> Back</span>
                                    <h1>Your Account</h1>
                                    <p>Manage your personal details, orders, addresses and more.</p>
                                    <a href="userAccount.html" class="btn-see-all">Manage Profile</a>
                                </div>

                                <div class="right-section">
                                    <h3>Account Menu</h3>
                                    <ul class="dropdown-links">
                                        <li>
                                            <a href="sign-in.html"><i class='bx bx-user'></i>
                                                <div>
                                                    <h5>Login</h5>
                                                    <p>Access your account.</p>
                                                </div>
                                            </a>
                                        </li>

                                        <li id="admin-link" style="display:none;">
                                            <a href="admin-sign-in.html">
                                                <i class='bx bx-user-plus'></i>
                                                <div>
                                                    <h5>Admin Account</h5>
                                                    <p>Manage everything at one place</p>
                                                </div>
                                            </a>
                                        </li>

                                        <li>
                                            <a href="#"><i class='bx bx-package'></i>
                                                <div>
                                                    <h5>Orders</h5>
                                                    <p>Track and manage your orders.</p>
                                                </div>
                                            </a>
                                        </li>

                                        <li>
                                            <a href="wishList.html"><i class='bx bx-heart'></i>
                                                <div>
                                                    <h5>Wishlist</h5>
                                                    <p>Your saved products.</p>
                                                </div>
                                            </a>
                                        </li>

                                        <li>
                                            <a href="#"><i class='bx bx-home'></i>
                                                <div>
                                                    <h5>Addresses</h5>
                                                    <p>Manage delivery locations.</p>
                                                </div>
                                            </a>
                                        </li>

                                        <li>
                                            <a href="userAccount.html"><i class='bx bx-cog'></i>
                                                <div>
                                                    <h5>Account Settings</h5>
                                                    <p>Update profile settings.</p>
                                                </div>
                                            </a>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </li>

                    <li><a href="#">Contact Us</a></li>
                </ul>

                <!-- RIGHT ICONS -->
                <div class="nav-icons">
                    <label for="search-toggle" class="search-icon">
                        <i class='bx bx-search'></i>
                    </label>

                   <a href="wishList.html"> <i class='bx bx-heart'></i></a>

                    <div class="cart-icon">
                        <a href="cart.html"><i class='bx bx-cart'></i></a>
                        <span class="cart-badge">3</span>
                    </div>
                </div>

                <i class='bx bx-menu toggle-navbar'></i>
            </div>

            <div class="search-overlay">
                <div class="search-container">
                    <div class="search-header">
                        <input id="prod-search" type="text" placeholder="Search products..." onkeyup="basicSearch(event);" autofocus>
                        <label for="search-toggle" class="close-search">
                            <i class='bx bx-x'></i>
                        </label>
                    </div>

                    <div class="search-results">
                        <p class="result-count"><strong id="result-count">0</strong> Result Found</p>

                        <div id="basic-search-result">
                            
                        </div>
                    </div>
                    
                </div>
            </div>

        </nav>`;
        this.checkAdminStatus();
    }


    async checkAdminStatus() {
        try {
            const response = await fetch("api/auth/admin-status");
            if (!response.ok) return;

            const data = await response.json();
            const adminLink = this.querySelector("#admin-link");
            if (!adminLink) return;

            adminLink.style.display = data.isAdmin === true ? "block" : "none";

        } catch (e) {
            Notiflix.Notify.failure(e.message, {position: 'center-top'});
        }
    }
}

customElements.define("header-content", HeaderContent);


async function basicSearch(event) {
    let searchInput = document.getElementById("prod-search");
    if (event.code === "Enter") {

        try {
            Notiflix.Loading.standard("Loading...", {
                clickToClose: false,
                svgColor: '#0284c7'
            });

            const response = await fetch(`api/products/basic-search?title=${searchInput.value}`);

            if (response.ok) {
                const data = await response.json();

                if (data.status) {
                    console.log(data.basicSearchData.length);
                    const searchData = data.basicSearchData;

                    document.getElementById("result-count").innerHTML = searchData.length;
                    const resultBox = document.getElementById("basic-search-result");
                    resultBox.innerHTML = "";

                    searchData.forEach((item) => {
                        resultBox.innerHTML += `
                            <div class="result-item">
                                <a href="single-product.html?productId=${item.stockId}">
                                    <img src="${item.image}">
                                </a>
                    
                                <div class="result-info">
                                    <h4>${item.title}</h4>
                                    <p>Rs. ${new Intl.NumberFormat("en-US", {
                                                minimumFractionDigits: 2
                                            }).format(item.price)}</p>
                                </div>
                    
                                <div class="result-actions">
                                    <a onclick="addToCart(${item.stockId}, 1);">
                                        <i class='bx bx-cart'></i>
                                    </a>
                                    <a><i class='bx bx-heart'></i></a>
                                </div>
                            </div>
                        `;
                    });


                } else {
                    Notiflix.Notify.failure(data.message, {
                        position: 'center-top'
                    });
                }

            } else {
                Notiflix.Notify.failure("products searching failed", {
                    position: 'center-top'
                });
            }

        } catch (e) {
            Notiflix.Notify.failure(e.message, {
                position: 'center-top'
            });
        } finally {
            Notiflix.Loading.remove();
        }
    }
}