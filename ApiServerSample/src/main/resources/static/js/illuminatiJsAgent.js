Array.prototype.inArrayCheck = function (needle, haystack) {
    var length = haystack.length;
    for (var i = 0; i < length; i++) {
        if (haystack[i] === needle) {
            return true;
        }
    }
    return false;
};

var illuminatiJsAgent = {

    passElementType : ['input', 'select', 'textarea'],

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

    getElementUniqueId : function (elementObj) {
        return (elementObj.id !== 'undefined' && elementObj.id !== null && elementObj.id.trim() !== '') ? elementObj.id : elementObj.name;
    },

    checkPassElement : function (elem) {
        if (Array.prototype.inArrayCheck(elem.localName, this.passElementType) === false) {
            return true;
        }

        if (elem.getAttribute('id') === 'undefined' && elem.getAttribute('name') === 'undefined') {
            return true;
        }

        var isUniqueIdEmptyCheck = false;

        if (elem.getAttribute('id') !== 'undefined' && elem.getAttribute('id') != null && elem.getAttribute('id').trim() === '') {
            isUniqueIdEmptyCheck = true;
        }

        if (elem.getAttribute('name') !== 'undefined' && elem.getAttribute('name') != null  && elem.getAttribute('name').trim() === '') {
            isUniqueIdEmptyCheck = true;
        }

        if (isUniqueIdEmptyCheck === true) {
            return true;
        }

        return false;
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

        if (eventObject['attributes'].hasOwnProperty('type') === true && eventObject['attributes']['type'] === 'checkbox') {
            eventObject['checked'] = e.target.checked;
        }

        eventObject['elementUniqueId'] = this.getElementUniqueId(eventObject);

        eventObject['target'] = e.target;

        return eventObject;
    },

    getNewEventData : function (oldObject) {
        var newObject = {};

        var targetObject = oldObject.target;

        newObject['id'] = targetObject.getAttribute('id');
        newObject['name'] = targetObject.getAttribute('name');

        if (typeof newObject['id'] !== 'undefined' && newObject['id'] !== null && newObject['id'].trim() !== '') {
            console.log('q1');
            targetObject = document.getElementById(newObject['id'].trim());
        } else if (typeof newObject['name'] !== 'undefined' && newObject['name'] !== null && newObject['name'].trim() !== '') {
            targetObject = document.getElementsByName(newObject['name'].trim());
            console.log('q2');
        }

        var objectAttributes = {};
        for (var i=0; i<targetObject.attributes.length; i++) {
            var item = targetObject.attributes.item(i);
            objectAttributes[item.name] = eval('targetObject.' + item.name);
        }

        Object.keys(objectAttributes).map(function(objectKey, index) {
            var changedInfo = {};
            var value = objectAttributes[objectKey];

            if (oldObject.attributes.hasOwnProperty(objectKey) === true) {;
                if (oldObject.attributes[objectKey] !== objectAttributes[objectKey]) {
                    changedInfo['changedValue'] = {};
                    changedInfo['changedValue']['old'] = oldObject.attributes[objectKey];
                    changedInfo['changedValue']['new'] = objectAttributes[objectKey];
                }
            } else {
                changedInfo['removedKey'] = objectKey;
            }

            if (Object.keys(changedInfo).length > 0) {
                if (objectAttributes.hasOwnProperty('changedInfo') === false) {
                    objectAttributes['changedInfo'] = [];
                }

                objectAttributes['changedInfo'][objectAttributes['changedInfo'].length] = changedInfo;
            }
        });

        return objectAttributes;
    }
};

var illuminatiGProcId = illuminatiJsAgent.generateGlobalTransactionId();

// document.addEventListener('keyup', function(e) {
//     var screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
//     var oldObject = illuminatiJsAgent.getEventData(e);
//     var newObject = illuminatiJsAgent.getNewEventData(oldObject);
//
//     console.log(oldObject);
//     console.log(newObject);
// });
//
// document.addEventListener('click', function(e) {
//     var screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
//     var oldObject = illuminatiJsAgent.getEventData(e);
//     var newObject = illuminatiJsAgent.getNewEventData(oldObject);
//
//     //console.log(newObject);
// });

var originElements = {};

var interval = setInterval(function() {
    if(document.readyState === 'complete') {
        clearInterval(interval);
        // document ready
        var elems = document.body.getElementsByTagName("*");

        var tempRadioStore = {};
        var elementStore = {};

        for (var i=0; i<elems.length; i++) {
            var elem = elems[i];

            if (illuminatiJsAgent.checkPassElement(elem) == true) {
                continue;
            }

            var elementObj = {
                obj: elem,
                type: elem.type,
                id: elem.getAttribute('id'),
                name: elem.getAttribute('name')
            };

            var elementUniqueId = illuminatiJsAgent.getElementUniqueId(elementObj);

            if (elem.localName === 'input' && elem.getAttribute('type') === 'radio') {
                if (tempRadioStore.hasOwnProperty(elementUniqueId) === false) {
                    tempRadioStore[elementUniqueId] = [];
                }

                var radio = {};
                for (var j = 0; j < elem.attributes.length; j++) {
                    var item = elem.attributes.item(j);
                    radio[item.name] = item.value;
                }

                radio['obj'] = elem;
                tempRadioStore[elementUniqueId][tempRadioStore[elementUniqueId].length] = radio;

                continue;
            }

            for (var j = 0; j < elem.attributes.length; j++) {
                var item = elem.attributes.item(j);

                if (elem.localName === 'select') {
                    elementObj['option'] = [];

                    for (var k=0; k<elem.childElementCount; k++) {
                        var option = {};
                        for (var m=0; m<elem[k].attributes.length; m++) {
                            var optionItem = elem[k].attributes.item(m);
                            option[optionItem.name] = optionItem.value;
                        }

                        elementObj['option'][elementObj['option'].length] = option;
                    }
                } else {
                    elementObj[item.name] = item.value;
                }
            }

            var key = elementObj.type + '-' + elementUniqueId;

            elementStore[key] = elementObj;
        }

        for (var key in tempRadioStore) {
            elementStore[key] = tempRadioStore[key];
        };

        console.log(elementStore);

        for (var key in elementStore) {
            //console.log(elementStore[key]);
            var eventElem = elementStore[key]

            // is radio element check
            if (Array.isArray(eventElem) !== true) {
                switch (eventElem.type) {
                    case 'text' :
                    case 'textarea' :
                        eventElem['obj'].addEventListener('keypress', function (e) {
                           console.log('keypress');
                        });
                        break;
                    case 'select-one' :
                        eventElem['obj'].addEventListener('change', function (e) {
                            console.log('change');
                        });
                        break;

                    default :
                        eventElem['obj'].addEventListener('click', function (e) {
                            console.log('click');
                        });
                        break;
                }
            } else {
                for (var n=0; n<eventElem.length; n++) {
                    var tmpRadioObj = eventElem[n];
                    tmpRadioObj['obj'].addEventListener('click', function (e) {
                        console.log('radio click');
                    });
                }
            }
            //console.log(eventElem.type, eventElem);

            // if (elem.type !== 'radio') {
            //     elem.addEventListener();
            // } else {
            //
            // }
        }

        sessionStorage.setItem('illuminati', JSON.stringify(elementStore));
        //console.log(JSON.parse(sessionStorage.getItem('illuminati')));
    }
}, 100);