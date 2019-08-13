Array.prototype.contains = function(obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}

var jvm = {
    requestUrl: "/api/v1/jvmInfo",

    chartUpdate : function (data) {
        var jvmFreeChart = {};
        jvmFreeChart['data'] = [];
        jvmFreeChart['label'] = [];

        var jvmUsedChart = {};
        jvmUsedChart['data'] = [];
        jvmUsedChart['label'] = [];

        var jvmCpuChart = {};
        jvmCpuChart['data'] = [];
        jvmCpuChart['label'] = [];

        var jvmThreadChart = {};
        jvmThreadChart['data'] = [];
        jvmThreadChart['label'] = [];
        for (var i = 0; i < data.length; i++) {
            var dataSource = data[i].source;
            jvmUsedChart['data'][jvmUsedChart['data'].length] = dataSource.jvmInfo.jvmUsedMemory;
            jvmUsedChart['label'][jvmUsedChart['label'].length] = dataSource.timestamp;

            jvmFreeChart['data'][jvmFreeChart['data'].length] = dataSource.jvmInfo.jvmFreeMemory;
            jvmFreeChart['label'][jvmFreeChart['label'].length] = dataSource.timestamp;

            jvmCpuChart['data'][jvmCpuChart['data'].length] = dataSource.jvmInfo.jvmCpuUsage;
            jvmCpuChart['label'][jvmCpuChart['label'].length] = dataSource.timestamp;

            jvmThreadChart['data'][jvmThreadChart['data'].length] = dataSource.jvmInfo.jvmActiveThreadCount;
            jvmThreadChart['label'][jvmThreadChart['label'].length] = dataSource.timestamp;
        }

        var drawChartData = [];
        drawChartData[0] = {};
        drawChartData[0]['label'] = jvmUsedChart['label'];
        drawChartData[0]['data'] = jvmUsedChart['data'];
        drawChartData[1] = {};
        drawChartData[1]['label'] = jvmFreeChart['label'];
        drawChartData[1]['data'] = jvmFreeChart['data'];
        drawChartData[2] = {};
        drawChartData[2]['label'] = jvmCpuChart['label'];
        drawChartData[2]['data'] = jvmCpuChart['data'];
        drawChartData[3] = {};
        drawChartData[3]['label'] = jvmThreadChart['label'];
        drawChartData[3]['data'] = jvmThreadChart['data'];

        Chart.helpers.each(Chart.instances, function (chart, index) {
            chart.data.labels = drawChartData[index].label;

            chart.data.datasets.forEach(function (dataset) {
                dataset.data = drawChartData[index].data;
            });

            chart.update();
        });
    },

    getBackgroundColor: function () {
        return [
            'rgba(255, 99, 132, 0.2)',
            'rgba(54, 162, 235, 0.2)',
            'rgba(255, 206, 86, 0.2)',
            'rgba(75, 192, 192, 0.2)',
            'rgba(153, 102, 255, 0.2)',
            'rgba(255, 159, 64, 0.2)'
        ];
    },

    getBorderColor: function () {
        return [
            'rgba(255,99,132,1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)',
            'rgba(75, 192, 192, 1)',
            'rgba(153, 102, 255, 1)',
            'rgba(255, 159, 64, 1)'
        ];
    },

    getOptions: function () {
        return {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero:true
                    }
                }]
            },
            responsive: true,
                maintainAspectRatio: true
        }
    },

    getJvmInfo : function (hostName) {
        var data = {
            esOrderType : 'desc',
            size : 10,
            from : 0,
            hostName: hostName
        };

        $.ajax({
            url: this.requestUrl,
            method: 'POST',
            contentType: "application/json",
            async: true,
            data: JSON.stringify(data),
            success: function (data) {
                if (data != null) {
                    var tmpData = data[0].source.jvmInfo;
                    for (var key in tmpData) {
                        var jvmInfoObj = $('#'+key);
                        if (jvmInfoObj != null && jvmInfoObj.length > 0) {
                            jvmInfoObj.text(tmpData[key]);
                        }
                    }

                    jvm.chartUpdate(data);
                }
            }
        });
    }
};