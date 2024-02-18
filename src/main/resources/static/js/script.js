//sidebar menu
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



//Like dislike button in home page
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




//Search functionality
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

//First request in server to create order
function openDonationPopup() {
                    document.getElementById("donatePopup").style.display = "block";
                    document.getElementById("overlay").style.display = "block";
                }

let amount; // Declare amount variable in a wider scope

const paymentstart = () => {
    console.log("Payment started...!");
    amount = $("#donationAmount").val(); // Assign value to amount
    console.log("Total Amount:" + amount);
    if (amount === '' || amount === null || amount === '0') {
        
        //It is coming from sweet alert
        swal("Failed", "Please Help the Needy...!!", "error");
        return;
    }
    closeDonationPopup();


// We will use ajax to send a request to the server
$.ajax({
    url: '/user/create_order',
    data: JSON.stringify({ amount: amount, info: 'order_request' }),
    contentType: 'application/json',
    type: 'POST',
    dataType: 'json',
    success: function (response) {
        // Invoked when success
        console.log("AJAX request successful");
        console.log(response);
        if(response.status=="created"){
            let options={
                key:"rzp_test_ditYT5Sj8tEIwg",
                amount:response.amount,
                currency:"INR",
                name:"HireMee",
                description:"Donation",
                image:"https://www.shutterstock.com/image-vector/letter-hm-rounded-shape-design-260nw-2377731993.jpg",
                order_id:response.id,
                handler: function(response){
                    console.log(response.razorpay_payment_id);
                    console.log(response.razorpay_order_id);
                    console.log(response.razorpay_signature);
                    console.log("Payment Successfull");
                    
                    UpdatePaymentOnServer(response.razorpay_payment_id,response.razorpay_order_id,"PAID");
                    //It is coming from sweet alert
                    

                },
                prefill: { //We recommend using the prefill parameter to auto-fill customer's contact information especially their phone number
                    name: "", //your customer's name
                    email: "",
                    contact: "", //Provide the customer's phone number for better conversion rates
                },
                notes: {
                    address: "HireMee , A smarter way to get hired"
                }, 
                theme: {
                    color: "#3399cc"
                }
            };

            let rzp= new Razorpay(options);
            rzp.on('payment.failed', function (response){
                console.log(response.error.code);
                console.log(response.error.description);
                console.log(response.error.source);
                console.log(response.error.step);
                console.log(response.error.reason);
                console.log(response.error.metadata.order_id);
                console.log(response.error.metadata.payment_id);
                
                swal("Failed ", "OOPS!! Payment Failed", "error");
               });
            rzp.open();
        }
    },
    error: function (error) {
        console.log(error);
        alert("Something went wrong....!!");
    }
});
};

// Close donation page
function closeDonationPopup() {
    document.getElementById("donatePopup").style.display = "none";
    document.getElementById("overlay").style.display = "none";
}

//Update payment
function UpdatePaymentOnServer(paymentid,orderid,status)
{
    $.ajax({
        url: '/user/update_order',
        data: JSON.stringify({ paymentid: paymentid, orderid: orderid,status: status }),
        contentType: 'application/json',
        type: 'POST',
        dataType: 'json',
        success: function(response){
            //It is coming from sweet alert 
            swal("Good job!", "THANK-YOU FOR YOUR SUPPORT", "success");
        },
        error: function(error){
            //It is coming from sweet alert 
            swal("Failed ", "Your payment details is not saved on server..!!", "error");
        },
    })
}