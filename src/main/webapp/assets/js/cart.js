// window.addEventListener("load", async () => {
//     try {
//         Notiflix.Loading.standard("Loading...", {
//             clickToClose: false,
//             svgColor: '#0284c7'
//         });
//         await addToCart();
//
//     } finally {
//         Notiflix.Loading.remove(1000);
//     }
// })
//
// async function addToCart(stockId, qty) {
//     try {
//
//         const response = await fetch(`api/carts/add-to-cart=${stockId}&qty=${qty}`);
//         if (response.ok) {
//             const data = await response.json();
//             if (data.status) {
//                 Notiflix.Notify.success(data.message, {
//                     position: 'center-top'
//                 });
//                 await loadCartItems();
//             } else {
//                 Notiflix.Notify.failure(data.message, {
//                     position: 'center-top'
//                 });
//             }
//         } else {
//             Notiflix.Notify.failure("Add to cart process failed!", {
//                 position: 'center-top'
//             });
//         }
//
//     } catch (e) {
//         Notiflix.Notify.failure(e.message, {
//             position: 'center-top'
//         });
//
//     } finally {
//         Notiflix.Loading.remove();
//     }
// }
//
// async function loadCartItems() {
//     try {
//         Notiflix.Loading.pulse("Loading...", {
//             clickToClose: false,
//             svgColor: '#0284c7'
//         });
//
//         const response = await fetch("api/carts/load-carts");
//         if(response.ok){
//             const data = await response.json();
//             if(data.status){
//                 console.log(data);
//                 Notiflix.Notify.success(data.message, {
//                     position: 'center-top'
//                 });
//             }else{
//                 Notiflix.Notify.info(data.message, {
//                     position: 'center-top'
//                 });
//             }
//         }else{
//             Notiflix.Notify.failure("Cart items loading failed!", {
//                 position: 'center-top'
//             });
//         }
//
//     } catch (e) {
//         Notiflix.Notify.failure(e.message, {
//             position: 'center-top'
//         });
//     } finally {
//         Notiflix.Loading.remove();
//     }
// }