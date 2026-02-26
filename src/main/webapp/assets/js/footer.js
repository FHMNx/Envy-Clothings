class FooterContent extends  HTMLElement{
    connectedCallback(){
        this.innerHTML = `
    <footer class="footer">
        <div class="footer-container">

            <!-- BRAND / ABOUT -->
            <div class="footer-section about">
                <img src="http://localhost:8080/Envy_Clothings/assets/images/logo/envy.png" class="footer-logo" alt="Logo">
                <p>
                    Your clothing brand offers a world-class retail experience with
                    the latest fashion and accessories focused on high-quality design.
                </p>
            </div>

            <!-- INFORMATION LINKS -->
            <div class="footer-section">
                <h3>INFORMATION</h3>
                <ul>
                    <li><a href="#">FAQ</a></li>
                    <li><a href="contactUs.html">Contact Us</a></li>
                    <li><a href="privacy.html">Privacy Policy</a></li>
                    <li><a href="terms.html">Terms & Conditions</a></li>
                    <li><a href="#">Delivery Details</a></li>
                    <li><a href="#">Return Policy</a></li>
                    <li><a href="#">Store Locations</a></li>
                    <li><a href="#">Promotions</a></li>
                </ul>
            </div>

            <!-- CONTACT / SOCIAL -->
            <div class="footer-section">
                <h3>GET IN TOUCH</h3>
                <ul class="social-list">
                    <li><i class='bx bxl-instagram'></i> Instagram</li>
                    <li><i class='bx bxl-facebook'></i> Facebook</li>
                    <li><i class='bx bxl-whatsapp'></i> WhatsApp</li>
                    <li><i class='bx bxl-tiktok'></i> TikTok</li>
                </ul>

                <div class="contact-info">
                    <p><strong>Online Store</strong></p>
                    <small>No.10, Kawdana Road, Dehiwala.</small>
                    <small>077 3540 816</small>
                    <small>care@yourstore.lk</small>
                </div>
            </div>

            <!-- NEWSLETTER -->
            <div class="footer-section newsletter">
                <h3>SUBSCRIBE TO NEWSLETTER</h3>

                <form>
                    <input type="email" placeholder="Email">
                    <button type="submit">Subscribe</button>
                </form>

                <p class="note">
                    By subscribing, you agree to receive automated promotional emails.
                </p>

                <div class="payments">
                    <img src="http://localhost:8080/Envy_Clothings/assets/images/logo/visa.png" alt="">
                    <img src="http://localhost:8080/Envy_Clothings/assets/images/logo/mastercard.png" alt="">
                    <img src="http://localhost:8080/Envy_Clothings/assets/images/logo/paypal.png" alt="">
                    <img src="http://localhost:8080/Envy_Clothings/assets/images/logo/american-express.png" alt="">
                </div>
            </div>
        </div>

        <div class="footer-bottom">
            <p>© 2025 Envy Clothings. All Rights Reserved.</p>
        </div>
    </footer>`
    }
}

customElements.define("footer-content" , FooterContent);