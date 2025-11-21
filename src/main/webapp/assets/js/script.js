const sliderTabs = document.querySelectorAll(".slider-tab");
const sliderIndicator = document.querySelector(".slider-indicator");
const sliderControls = document.querySelector(".slider-controls");

//update the indicatoir height and width
const updateIndicator = (tab, index) => {
    sliderIndicator.style.transform = `translateX(${tab.offsetLeft - 20}px)`;
    sliderIndicator.style.width = `${tab.getBoundingClientRect().width}px`;

    //calculate the scroll position and scroll smoothly
    const scrollLeft = sliderTabs[index].offsetLeft - sliderControls.
        offsetWidth / 2 + sliderTabs[index].offsetWidth / 2;
    sliderControls.scrollTo({ left: scrollLeft, behavior: "smooth" });
}

//initialize swiper instance
const swiper = new Swiper(".slider-container", {
    effect: "fade",
    speed: 1300,
    autoplay: { delay: 4000 },
    navigation: {
        prevEl: "#slider-prev",
        nextEl: "#slider-next",
    },
    on: {
        //update pagination on slide change
        slideChange: () => {
            const currentTabIndex = [...sliderTabs].indexOf
            (sliderTabs[swiper.activeIndex]);
            updateIndicator(sliderTabs[swiper.activeIndex], currentTabIndex);
        },
        reachEnd: () => swiper.autoplay.stop()
    }
});


//update the slide on tab click and pagination
sliderTabs.forEach((tab, index) => {
    tab.addEventListener("click", () => {
        swiper.slideTo(index);
        updateIndicator(tab, index);
    });
});

updateIndicator(sliderTabs[0], 0);
window.addEventListener("resize", () => updateIndicator(sliderTabs[swiper.activeIndex], 0));


$('.testimonials-container').owlCarousel({
    loop: true,
    autoplay: true,
    autoplayTimeout: 5000,
    margin: 15,
    nav: true,
    navText: [
        "<i class='fa-solid fa-arrow-left'></i>",
        "<i class='fa-solid fa-arrow-right'></i>"
    ],
    responsive: {
        0: { items: 1, nav: false },
        600: { items: 1 },
        768: { items: 2 },
        1024: { items: 3 }
    }
});

