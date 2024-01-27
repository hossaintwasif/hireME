console.log("This is base page");

const toggleSidebar = () => {
    if ($(".sidebar").is(":visible")) {
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    } else {
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};

let likesCount = 0;
let liked = false;

function toggleLike() {
    liked = !liked;

    if (liked) {
        likesCount++;
    } else {
        likesCount--;
    }

    document.querySelector('.likes-count').textContent = likesCount;
}

const search = () => {
    console.log("searching..");
    let query = $("#search-input").val();

    if (query == "") {
        $(".search-result").hide();
    } else {
        console.log(query);
        let url = `http://localhost:8080/search/${query}`;
        fetch(url).then((response) => {
            return response.json();
        }).then((data) => {
            // Handle the data
            console.log(data);

            let text = `<div class='list-group'>`;
            data.forEach((contact) => { // Corrected method name: forEach
                text += `<a  href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'> ${contact.name} </a>`;
            });
            text += `</div>`;
            $(".search-result").html(text);
            $(".search-result").show();
        });
    }
}

