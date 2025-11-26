class HeaderContent extends HTMLElement{
    connectedCallback(){
        this.innerHTML = `<nav>
        <div class="container">

            <!-- BRAND / LOGO -->
            <a href="#" class="brand">
                <img src="http://localhost:8080/Envy_Clothings/assets/images/logo/envy.png" style="height:60px;" />
            </a>

            <!-- MENU -->

            <ul class="nav-menu">

                <li><a href="#">New Arrivals</a></li>

                <li>
                    <a href="#">Men <i class='bx bx-chevron-down'></i></a>

                    <div class="dropdown-menu">
                        <div class="container">
                            <div class="left-section">
                                <span class="dropdown-close"><i class='bx bx-chevron-left'></i> Back</span>
                                <h1>Men's Collection</h1>
                                <p>Shop the latest for men – clothing, footwear, accessories and more.</p>
                                <a href="#" class="btn-see-all">Shop All Men</a>
                            </div>

                            <div class="right-section">
                                <h3>Categories</h3>
                                <ul class="dropdown-links">

                                    <li><a href="#"><i class='bx bx-male'></i>
                                            <div>
                                                <h5>Clothing</h5>
                                                <p>T-shirts, Jeans, Jackets, Formals</p>
                                            </div>
                                        </a></li>

                                    <li><a href="#"><i class='bx bx-run'></i>
                                            <div>
                                                <h5>Footwear</h5>
                                                <p>Sneakers, Sandals, Formal Shoes</p>
                                            </div>
                                        </a></li>

                                    <li><a href="#"><i class='bx bx-watch'></i>
                                            <div>
                                                <h5>Accessories</h5>
                                                <p>Watches, Wallets, Belts</p>
                                            </div>
                                        </a></li>

                                    <li><a href="#"><i class='bx bx-trophy'></i>
                                            <div>
                                                <h5>Sportswear</h5>
                                                <p>Active wear and gym essentials</p>
                                            </div>
                                        </a></li>

                                </ul>
                            </div>
                        </div>
                    </div>
                </li>


                <li>
                    <a href="#">Women <i class='bx bx-chevron-down'></i></a>

                    <div class="dropdown-menu">
                        <div class="container">
                            <div class="left-section">
                                <span class="dropdown-close"><i class='bx bx-chevron-left'></i> Back</span>
                                <h1>Women's Collection</h1>
                                <p>Explore the latest styles – clothing, beauty, footwear and more.</p>
                                <a href="#" class="btn-see-all">Shop All Women</a>
                            </div>

                            <div class="right-section">
                                <h3>Categories</h3>
                                <ul class="dropdown-links">

                                    <li><a href="#"><i class='bx bx-female'></i>
                                            <div>
                                                <h5>Clothing</h5>
                                                <p>Dresses, Tops, Sarees, Kurtas</p>
                                            </div>
                                        </a></li>

                                    <li><a href="#"><i class='bx bx-diamond'></i>
                                            <div>
                                                <h5>Accessories</h5>
                                                <p>Jewelry, Bags, Sunglasses</p>
                                            </div>
                                        </a></li>

                                    <li><a href="#"><i class='bx bx-brush'></i>
                                            <div>
                                                <h5>Beauty</h5>
                                                <p>Cosmetics, Skincare, Haircare</p>
                                            </div>
                                        </a></li>

                                    <li><a href="#"><i class='bx bx-walk'></i>
                                            <div>
                                                <h5>Footwear</h5>
                                                <p>Heels, Sandals, Sneakers</p>
                                            </div>
                                        </a></li>

                                </ul>
                            </div>
                        </div>
                    </div>
                </li>


                <li><a href="#">Kids</a></li>
                <li><a href="#">Offers</a></li>


                <!-- NEW PROFILE DROPDOWN -->
                <li>
                    <a href="#">Profile <i class='bx bx-chevron-down'></i></a>

                    <div class="dropdown-menu">
                        <div class="container">
                            <div class="left-section">
                                <span class="dropdown-close"><i class='bx bx-chevron-left'></i> Back</span>
                                <h1>Your Account</h1>
                                <p>Manage your personal details, orders, addresses and more.</p>
                                <a href="#" class="btn-see-all">Manage Profile</a>
                            </div>

                            <div class="right-section">
                                <h3>Account Menu</h3>
                                <ul class="dropdown-links">

                                    <li><a href="sign-in.html"><i class='bx bx-user'></i>
                                            <div>
                                                <h5>Login</h5>
                                                <p>Access your account.</p>
                                            </div>
                                        </a></li>

                                    <li><a href="sign-in.html"><i class='bx bx-user-plus'></i>
                                            <div>
                                                <h5>Create Account</h5>
                                                <p>Join and save your preferences.</p>
                                            </div>
                                        </a></li>

                                    <li><a href="#"><i class='bx bx-package'></i>
                                            <div>
                                                <h5>Orders</h5>
                                                <p>Track and manage your orders.</p>
                                            </div>
                                        </a></li>

                                    <li><a href="#"><i class='bx bx-heart'></i>
                                            <div>
                                                <h5>Wishlist</h5>
                                                <p>Your saved products.</p>
                                            </div>
                                        </a></li>

                                    <li><a href="#"><i class='bx bx-home'></i>
                                            <div>
                                                <h5>Addresses</h5>
                                                <p>Manage delivery locations.</p>
                                            </div>
                                        </a></li>

                                    <li><a href="#"><i class='bx bx-cog'></i>
                                            <div>
                                                <h5>Account Settings</h5>
                                                <p>Update profile settings.</p>
                                            </div>
                                        </a></li>

                                </ul>
                            </div>
                        </div>
                    </div>

                </li>

                <li><a href="#">Contact Us</a></li>

            </ul>


            <!-- RIGHT SIDE ACTION ICONS -->
            <div class="nav-icons">
                <i class='bx bx-search'></i>
                <i class='bx bx-heart'></i>

                <!-- Cart with badge -->
                <div class="cart-icon">
                    <i class='bx bx-cart'></i>
                    <span class="cart-badge">3</span>
                </div>
            </div>

            <i class='bx bx-menu toggle-navbar'></i>
        </div>
    </nav>`
    }
}

customElements.define("header-content" , HeaderContent);