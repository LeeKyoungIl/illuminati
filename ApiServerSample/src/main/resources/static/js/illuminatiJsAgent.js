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
        return (typeof elementObj.id !== 'undefined' && elementObj.id !== null && elementObj.id.trim() !== '') ? elementObj.id : elementObj.name;
    },

    checkPassElement : function (elem) {
        if (Array.prototype.inArrayCheck(elem.localName, this.passElementType) === false) {
            return true;
        }

        if (typeof elem.getAttribute('id') === 'undefined' && elem.getAttribute('name') === 'undefined') {
            return true;
        }

        var isUniqueIdEmptyCheck = false;
        if (typeof elem.getAttribute('id') !== 'undefined' && elem.getAttribute('id') != null && elem.getAttribute('id').trim() === '') {
            isUniqueIdEmptyCheck = true;
        }
        if (typeof elem.getAttribute('name') !== 'undefined' && elem.getAttribute('name') != null  && elem.getAttribute('name').trim() === '') {
            isUniqueIdEmptyCheck = true;
        }
        if (isUniqueIdEmptyCheck === true) {
            return true;
        }

        return false;
    },

    getScreenInfoAtEvent : function (e) {
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

    getElementObj : function (object) {
        if (typeof object['id'] !== 'undefined' && object['id'] !== null && object['id'].trim() !== '') {
            return document.getElementById(object['id'].trim());
        } else if (typeof object['name'] !== 'undefined' && object['name'] !== null && object['name'].trim() !== '') {
            return document.getElementsByName(object['name'].trim());
        }

        return null;
    },

    getChangedAttributeValue : function (attributeName, oldData, newData) {
        var changedValue = {};
        changedValue['attributeName'] = attributeName;
        changedValue['old'] = oldData;
        changedValue['new'] = newData;

        return changedValue;
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

        if (e.target.type.indexOf('textarea') > -1) {
            var tempTextareaObj = illuminatiJsAgent.getElementObj(eventObject);
            objectAttributes['value'] = tempTextareaObj.value;
        }

        eventObject['attributes'] = objectAttributes;
        eventObject['obj'] = illuminatiJsAgent.getElementObj(eventObject);

        if (e.target.type.indexOf('select') > -1) {
            var firstElementData = JSON.parse(sessionStorage.getItem('illuminati'));
            var key = e.target.type + '-' + this.getElementUniqueId(eventObject);

            eventObject['obj'] = firstElementData[key];
        } else if (eventObject['attributes'].hasOwnProperty('type') === true) {
            if (eventObject['attributes']['type'] === 'checkbox') {
                eventObject['checked'] = e.target.checked;
            } else if (eventObject['attributes']['type'] === 'radio') {
                var firstElementData = JSON.parse(sessionStorage.getItem('illuminati'));
                var key = e.target.type + '-' + this.getElementUniqueId(eventObject);

                eventObject['obj'] = firstElementData[key];
            }
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

        var tmpTargetObject = illuminatiJsAgent.getElementObj(newObject);
        if (tmpTargetObject !== null) {
            targetObject = tmpTargetObject;
        }

        var objectAttributes = {};
        var changedInfo = {};

        objectAttributes['elementUniqueId'] = oldObject.elementUniqueId;
        objectAttributes['type'] = oldObject.obj.type;

        if (typeof oldObject.obj.type !== 'undefined' && oldObject.obj.type.indexOf('select') > -1) {
            changedInfo['changedValue'] = [];
            for (var q=0; q<oldObject.obj.option.length; q++) {
                var tempSelectOption = oldObject.obj.option[q];

                if ((tempSelectOption.hasOwnProperty('selected') === true && targetObject[q].selected === false)
                    || (tempSelectOption.hasOwnProperty('selected') === false && targetObject[q].selected === true)) {
                    changedInfo['changedValue'][changedInfo['changedValue'].length] = illuminatiJsAgent.getChangedAttributeValue('selected', tempSelectOption.hasOwnProperty('selected'), targetObject[q].selected);

                    Object.keys(tempSelectOption).map(function(objectKey, index) {
                        objectAttributes[objectKey] = eval('tempSelectOption.' + objectKey);
                    });
                }
            }

            objectAttributes['obj'] = oldObject.obj;
            objectAttributes['id'] = oldObject.id;
            objectAttributes['name'] = oldObject.name;
        } else if (targetObject.length > 1 && oldObject.name.indexOf('radio') > -1) {
            changedInfo['changedValue'] = [];
            for (var p=0; p<oldObject.obj.length; p++) {
                var tempOldRadioObj = oldObject.obj[p];

                if ((tempOldRadioObj.hasOwnProperty('checked') === true && targetObject[p].checked === false)
                    || (tempOldRadioObj.hasOwnProperty('checked') === false && targetObject[p].checked === true)) {
                    changedInfo['changedValue'][changedInfo['changedValue'].length] = illuminatiJsAgent.getChangedAttributeValue('checked', tempOldRadioObj.hasOwnProperty('checked'), targetObject[p].checked);

                    Object.keys(tempOldRadioObj).map(function(objectKey, index) {
                        objectAttributes[objectKey] = eval('tempOldRadioObj.' + objectKey);
                    });
                }
            }
        } else if (oldObject.name.indexOf('checkbox') > -1) {
            if ((oldObject.checked === true && targetObject.checked === false)
                || (oldObject.checked === false && targetObject.checked === true)) {
                changedInfo['changedValue'] = illuminatiJsAgent.getChangedAttributeValue('checked', oldObject.checked, targetObject.checked);

                for (var i=0; i<targetObject.attributes.length; i++) {
                    var item = targetObject.attributes.item(i);
                    objectAttributes[item.name] = eval('targetObject.' + item.name);
                }
            }
        } else {
            for (var i=0; i<targetObject.attributes.length; i++) {
                var item = targetObject.attributes.item(i);
                objectAttributes[item.name] = eval('targetObject.' + item.name);
            }

            if (oldObject.name.indexOf('textarea') > -1) {
                objectAttributes['value'] = targetObject.value;
            }

            Object.keys(objectAttributes).map(function(objectKey, index) {
                var value = objectAttributes[objectKey];

                if (oldObject.attributes.hasOwnProperty(objectKey) === true
                    && (oldObject.attributes[objectKey] !== objectAttributes[objectKey])) {
                        changedInfo['changedValue'] = illuminatiJsAgent.getChangedAttributeValue(objectKey, oldObject.attributes[objectKey], objectAttributes[objectKey]);
                } else {
                    changedInfo['removedKey'] = objectKey;
                }
            });
        }

        if (Object.keys(changedInfo).length > 0) {
            objectAttributes['changedInfo'] = changedInfo;
        }

        return objectAttributes;
    },

    setElementToSessionStorage : function (newObject) {
        if (newObject.hasOwnProperty('changedInfo') === true) {
            var elementStore = JSON.parse(sessionStorage.getItem('illuminati'));
            var key = newObject.type + '-' + newObject.elementUniqueId;
            elementStore[key]['changedInfo'] = newObject.changedInfo;
            sessionStorage.setItem('illuminati', JSON.stringify(elementStore));
        }
    }
};

var illuminatiGProcId = illuminatiJsAgent.generateGlobalTransactionId();

var lastCheckObject;

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
                if (tempRadioStore.hasOwnProperty(elementObj.type + '-' + elementUniqueId) === false) {
                    tempRadioStore[elementObj.type + '-' + elementUniqueId] = [];
                }

                var radio = {};
                for (var j = 0; j < elem.attributes.length; j++) {
                    var item = elem.attributes.item(j);
                    radio[item.name] = item.value;
                }

                radio['obj'] = elem;
                tempRadioStore[elementObj.type + '-' + elementUniqueId][tempRadioStore[elementObj.type + '-' + elementUniqueId].length] = radio;

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

            if (elem.localName === 'textarea') {
                elementObj['value'] = elem.value;
            }

            var key = elementObj.type + '-' + elementUniqueId;

            elementStore[key] = elementObj;
        }

        for (var key in tempRadioStore) {
            elementStore[key] = tempRadioStore[key];
        }

        for (var key in elementStore) {
            var eventElem = elementStore[key]

            if (Array.isArray(eventElem) !== true) {
                switch (eventElem.type) {
                    case 'text' :
                        eventElem['obj'].addEventListener('keyup', function (e) {
                            var screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                            var oldObject = illuminatiJsAgent.getEventData(e);
                            var newObject = illuminatiJsAgent.getNewEventData(oldObject);
                            newObject['screenInfo'] = screenInfo;
                            illuminatiJsAgent.setElementToSessionStorage(newObject);
                        });
                        break;
                    case 'textarea' :
                        var screenInfo;
                        eventElem['obj'].addEventListener('focusin', function (e) {
                            screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                            lastCheckObject = illuminatiJsAgent.getEventData(e);
                        });
                        eventElem['obj'].addEventListener('keyup', function (e) {
                            var newObject = illuminatiJsAgent.getNewEventData(lastCheckObject);
                            newObject['screenInfo'] = screenInfo;
                            illuminatiJsAgent.setElementToSessionStorage(newObject);
                        });
                        break;
                    case 'select-one' :
                        eventElem['obj'].addEventListener('change', function (e) {
                            var screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                            var oldObject = illuminatiJsAgent.getEventData(e);
                            var newObject = illuminatiJsAgent.getNewEventData(oldObject);
                            newObject['screenInfo'] = screenInfo;
                            illuminatiJsAgent.setElementToSessionStorage(newObject);
                        });
                        break;

                    default :
                        var screenInfo;
                        eventElem['obj'].addEventListener('mouseup', function (e) {
                            screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                            lastCheckObject = illuminatiJsAgent.getEventData(e);
                        });
                        eventElem['obj'].addEventListener('click', function (e) {
                            var newObject = illuminatiJsAgent.getNewEventData(lastCheckObject);
                            newObject['screenInfo'] = screenInfo;
                            delete(lastClickObject);
                            illuminatiJsAgent.setElementToSessionStorage(newObject);
                        });
                        break;
                }
            } else {
                for (var n=0; n<eventElem.length; n++) {
                    var tmpRadioObj = eventElem[n];
                    tmpRadioObj['obj'].addEventListener('click', function (e) {
                        var screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                        var oldObject = illuminatiJsAgent.getEventData(e);
                        var newObject = illuminatiJsAgent.getNewEventData(oldObject);
                        newObject['screenInfo'] = screenInfo;
                        illuminatiJsAgent.setElementToSessionStorage(newObject);
                    });
                }
            }
        }

        sessionStorage.setItem('illuminati', JSON.stringify(elementStore));
    }
}, 100);