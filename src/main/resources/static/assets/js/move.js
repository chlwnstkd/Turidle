
$(document).ready(function() {
    $("#index").on("click", function() {
        location.href = "/trader/traderIndex";
    })

    $("#traderIndex").on("click", function() {
    location.href = "/trader/traderIndex";
})

    $("#shopInfo").on("click", function() {
    location.href = "/trader/shopInfo";
})

    $("#traderInfo").on("click", function() {
    location.href = "/trader/traderInfo";
})

    $("#goodsMng").on("click", function() {
    location.href = "/goods/goodsMng";
})

    $("#reviewMng").on("click", function() {
    location.href = "/review/reviewMng";
})

    $("#reservMng").on("click", function() {
    location.href = "/reservation/reservMng";
})

    $("#chat").on("click", function() {
    location.href = "/chat/intro";
})

    $("#customerService").on("click", function() {
    location.href = "/trader/customerService";
})

    $("#goodsBuyInfo").on("click", function() {
    location.href = "/goods/goodsBuyInfo";
})
});
