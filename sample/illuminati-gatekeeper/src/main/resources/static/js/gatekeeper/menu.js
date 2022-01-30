var menu = {
    basicApiServerUrl : '/api/v1/',

    initSystemStatus : function (requestUrl) {
        $.ajax({
            url: menu.basicApiServerUrl + requestUrl,
            method: 'GET',
            contentType: 'application/json',
            async: true,
            success: function (data) {
                var systemStatusMenuObj = $('#systemStatusMenu');
                var html = [];
                for (var index = 0; index < data.length; index++) {
                    html[html.length] = '<li><a href="/system/view/summary/'+data[index].key+'"><i class="fa fa-circle-o text-red"></i> HostName : <b>'+data[index].key+'</b></a></li>';
                }
                systemStatusMenuObj.html(html.join(''));
            }
        })
    }
};