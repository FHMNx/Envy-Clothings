window.addEventListener("load", async () => {
    try {
        Notiflix.Loading.standard("Loading...", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        await advancedSearchData();

    } finally {
        Notiflix.Loading.remove(1000);
    }
});

async function advancedSearchData(){
    try {
            const response = await fetch("api/advanced-search/all-data");
            if(response.ok){
                const data = await response.json();
                if(data.status){
                    console.log(data);
                }else{
                    Notiflix.Notify.failure(data.message, {
                        position: 'center-top'
                    });
                }
            }else{
                Notiflix.Notify.failure("product data failed", {
                    position: 'center-top'
                });
            }
    }catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

