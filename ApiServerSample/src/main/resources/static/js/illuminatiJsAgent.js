var illuminatiModel = {};

var illuminatiJsAgent = {

    init : function () {
    },

    generateUDID : function () {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    },

    generateGlobalTransactionId : function () {
        var gTransactionId = [];
        for (var i=0; i<8; i++) {
            gTransactionId[gTransactionId.length] = this.generateUDID();
        }

        return gTransactionId.join('')+'-illuminatiGProcId';
    },

    getEventData : function (e) {
        var clientScreenInfo = {
            browserWidth: window.innerWidth || document.body.clientWidth,
            browserHeight: window.innerHeight || document.body.clientHeight,
            clientX: e.clientX,
            clientY: e.clientY,
            layerX: e.layerX,
            layerY: e.layerY,
            offsetX: e.offsetX,
            offsetY: e.offsetY,
            screenX: e.screenX,
            screenY: e.screenY,
            x: e.x,
            y: e.y
        };

        var eventObject = {};
        var objectAttributes = {};

        for (var i=0; i<e.target.attributes.length; i++) {
            var item = e.target.attributes.item(i);
            switch (item.name) {
                case 'id' :
                    eventObject['id'] = item.value;
                    break;

                case 'name' :
                    eventObject['name'] = item.value;
                    break;

                default:
                    objectAttributes[item.name] = item.value;
                    break;
            }
        };

        eventObject['attributes'] = objectAttributes;
        eventObject['clientScreenInfo'] = clientScreenInfo;

        console.log(eventObject);
        return eventObject;
    }
};

document.addEventListener('keydown', function(e) {
    illuminatiJsAgent.getEventData(e);
});

document.addEventListener('keyup', function(e) {
    var a = illuminatiJsAgent.getEventData(e);
    console.log(document.getElementById(a['id']).value);
});