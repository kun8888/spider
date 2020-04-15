function getDate() {
    $("#btn-div").attr("disabled","disabled");
    alert("开始爬啦！");
    $.ajax({
        url: "/spider/getKtggDatas",
        type: "post",
        dataType: "json",
        success: function (result) {
            console.log(result);
            if(result.success == true) {
                alert("爬取成功！");
            } else {
                alert("爬取失败！");
            }
        }
    })
}