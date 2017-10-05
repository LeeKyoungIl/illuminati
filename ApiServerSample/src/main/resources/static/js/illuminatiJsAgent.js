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

    getScreenInfoAtEvent : function (e) {
        console.log(e);
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

        return clientScreenInfo;
    },

    getEventData : function (e) {
        var eventObject = {};
        var objectAttributes = {};

        for (var i=0; i<e.target.attributes.length; i++) {
            var item = e.target.attributes.item(i);

            switch (item.name) {
                case 'id' :
                    eventObject['id'] = item.value;
                case 'name' :
                    eventObject['name'] = item.value;

                default:
                    objectAttributes[item.name] = item.value;
                    break;
            }
        }

        eventObject['attributes'] = objectAttributes;

        return eventObject;
    },

    getNewEventData : function (oldObject) {
        var newObject = {};

        newObject['id'] = oldObject.id;
        newObject['name'] = oldObject.name;

        var targetObject;
        if (typeof newObject['id'] != 'undefined' && newObject['id'] != null && newObject['id'].trim() != '') {
            targetObject = document.getElementById(newObject['id'].trim());
        } else if (typeof newObject['name'] != 'undefined' && newObject['name'] != null && newObject['name'].trim() != '') {
            targetObject = document.getElementsByName(newObject['name'].trim());
        } else {
            return;
        }

        var objectAttributes = {};
        for (var i=0; i<targetObject.attributes.length; i++) {
            var item = targetObject.attributes.item(i);
            objectAttributes[item.name] = eval('targetObject.' + item.name);
        }

        Object.keys(objectAttributes).map(function(objectKey, index) {
            var changedInfo = {};
            var value = objectAttributes[objectKey];

            if (oldObject.attributes.hasOwnProperty(objectKey) == true) {;
                if (oldObject.attributes[objectKey] != objectAttributes[objectKey]) {
                    changedInfo['changedValue'] = {};
                    changedInfo['changedValue']['old'] = oldObject.attributes[objectKey];
                    changedInfo['changedValue']['new'] = objectAttributes[objectKey];
                }
            } else {
                changedInfo['removedKey'] = objectKey;
            }

            if (Object.keys(changedInfo).length > 0) {
                if (objectAttributes.hasOwnProperty('changedInfo') == false) {
                    objectAttributes['changedInfo'] = [];
                }

                objectAttributes['changedInfo'][objectAttributes['changedInfo'].length] = changedInfo;
            }
        });

        return objectAttributes;
    }
};

document.addEventListener('keyup', function(e) {
    var screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
    var oldObject = illuminatiJsAgent.getEventData(e);
    var newObject = illuminatiJsAgent.getNewEventData(oldObject);

    console.log(newObject);

   // console.log(newObject);
});

document.addEventListener('click', function(e) {
    //console.log(illuminatiJsAgent.getScreenInfoAtEvent(e));
});