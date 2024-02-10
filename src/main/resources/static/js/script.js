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




function toggleLike(button) {
    const likesCount = parseInt(button.getAttribute('data-likesCount'));
    let liked = button.getAttribute('data-liked') === 'true';

    liked = !liked;

    if (liked) {
        button.setAttribute('data-likesCount', likesCount + 1);
    } else {
        button.setAttribute('data-likesCount', likesCount - 1);
    }

    button.setAttribute('data-liked', liked);
    
    // Update the displayed likes count
    button.nextElementSibling.textContent = button.getAttribute('data-likesCount');
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
                text += `<a  href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'> ${contact.company} </a>`;
            });
            text += `</div>`;
            $(".search-result").html(text);
            $(".search-result").show();
        });
    }
}

