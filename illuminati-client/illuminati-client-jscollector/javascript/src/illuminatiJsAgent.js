Array.prototype.inArrayCheck = function (needle, haystack) {
    var length = haystack.length;
    for (var i = 0; i < length; i++) {
        if (haystack[i] === needle) {
            return true;
        }
    }
    return false;
};

var _send = XMLHttpRequest.prototype.send;
XMLHttpRequest.prototype.send = function() {
    if (this.responseURL.indexOf(collectorUrl) === -1) {
        illuminatiAjax.setRequestHeaderOnAjaxEvent(this);

        illuminatiJsAgent.tempBufferToBuffer();
        illuminatiJsAgent.sendToIlluminati(false);
    }

    _send.apply(this, arguments);
};

var illuminatiJsAgent = {
    passElementType : ['form', 'input', 'select', 'textarea'],
    illuminatiInputElementType : ['text', 'radio', 'checkbox'],

    init : function () {
        if (isEventListener === false && isAttachEvent === false) {
            return;
        }
        // session transaction Id
        illuminatiJsAgent.setSessionStorage('illuminatiSProcId', illuminatiJsAgent.generateTransactionId('S'));

        var gTransactionId = illuminatiJsAgent.getSessionStorage('illuminatiGProcId');

        if (typeof gTransactionId === 'undefined' || gTransactionId === null) {
            illuminatiJsAgent.setSessionStorage('illuminatiGProcId', String(illuminatiJsAgent.generateTransactionId('G')));
        }

        if (typeof isAutoCollect !== 'undefined' && isAutoCollect === true) {
            autoSendToIlluminati = window.setInterval(function () {
                illuminatiJsAgent.sendToIlluminati(true);
            }, collectIntervalTimeMs);
        }
    },

    setUniqueUserId : function (illuminatiUniqueUserId) {
        if (typeof illuminatiUniqueUserId !== 'undefined' && illuminatiUniqueUserId !== null) {
            illuminatiJsAgent.setSessionStorage('illuminatiUniqueUserId', String(illuminatiUniqueUserId));
        }
    },

    setSessionStorage : function (key, value) {
        try {
            window.sessionStorage.setItem(key, value);
        } catch (e) {
            console.debug("This browser is not support session storage.");
        }
    },

    getSessionStorage : function (key) {
        try {
            return window.sessionStorage.getItem(key);
        } catch (e) {
            console.debug("This browser is not support session storage.");
            return null;
        }
    },

    removeSessionStorage : function (key) {
        try {
            window.sessionStorage.removeItem(key);
        } catch (e) {
            console.debug("This browser is not support session storage.");
        }
    },

    checkIsIe : function () {
        if (navigator.appName == 'Microsoft Internet Explorer' ||  !!(navigator.userAgent.match(/Trident/) || navigator.userAgent.match(/rv:11/))) {
            return true;
        }

        return false;
    },

    generateUDID : function () {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    },

    generateSumUDID : function () {
        var gTransactionId = [];
        for (var i=0; i<8; i++) {
            gTransactionId[gTransactionId.length] = illuminatiJsAgent.generateUDID();
        }

        return gTransactionId;
    },

    generateTransactionId : function (type) {
        return illuminatiJsAgent.generateSumUDID().join('')+'-illuminati'+type+'ProcId';
    },

    generateHiddenInputElement : function (form, name, value, id) {
        try {
            var sInput = document.createElement('input');
            sInput.type = 'hidden';
            sInput.name = name;
            sInput.value = String(value);

            if (typeof id !== 'undefined' && id !== null) {
                sInput.id = id;
            }

            form.appendChild(sInput);
        } catch (e) {
            console.debug('create hidden input element exception : ', e);
        }
    },

    getElementUniqueId : function (elementObj) {
        return (typeof elementObj.id !== 'undefined' && elementObj.id !== null && elementObj.id.trim() !== '') ? elementObj.id : elementObj.name;
    },

    checkPassElement : function (elem) {
        if (Array.prototype.inArrayCheck(elem.localName, illuminatiJsAgent.passElementType) === false) {
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

        if (elem.localName === 'input' && Array.prototype.inArrayCheck(elem.type, illuminatiJsAgent.illuminatiInputElementType) === false) {
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

    getChangedAttributeValue : function (type, elementUniqueId, attributeName, oldData, newData, index) {
        var changedValue = {};
        changedValue['elementType'] = type;
        changedValue['elementUniqueId'] = elementUniqueId;
        changedValue['attributeName'] = attributeName;
        changedValue['oldData'] = String(oldData);
        changedValue['newData'] = String(newData);
        changedValue['index'] = (typeof index !== 'undefined' && index !== null) ? index : 0;

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
            var firstElementData = JSON.parse(illuminatiJsAgent.getSessionStorage('illuminati'));
            var key = e.target.type + '-' + illuminatiJsAgent.getElementUniqueId(eventObject);

            eventObject['obj'] = firstElementData[key];
        } else if (eventObject['attributes'].hasOwnProperty('type') === true) {
            if (eventObject['attributes']['type'] === 'checkbox') {
                eventObject['checked'] = e.target.checked;
            } else if (eventObject['attributes']['type'] === 'radio') {
                var firstElementData = JSON.parse(illuminatiJsAgent.getSessionStorage('illuminati'));
                var key = e.target.type + '-' + illuminatiJsAgent.getElementUniqueId(eventObject);

                eventObject['obj'] = firstElementData[key];
            }
        }

        eventObject['elementUniqueId'] = illuminatiJsAgent.getElementUniqueId(eventObject);
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
        objectAttributes['type'] = oldObject.target.type;
        objectAttributes['target'] = oldObject.target;

        changedInfo['changedValues'] = [];
        var ignoreRemoveKeys = ['target', 'elementUniqueId'];

        if (typeof oldObject.target.type !== 'undefined') {
            if (oldObject.target.type.indexOf('select') > -1) {
                for (var q=0; q<oldObject.obj.option.length; q++) {
                    var tempSelectOption = oldObject.obj.option[q];

                    if ((tempSelectOption.hasOwnProperty('selected') === true && targetObject[q].selected === false)
                        || (tempSelectOption.hasOwnProperty('selected') === false && targetObject[q].selected === true)) {
                        changedInfo['changedValues'][changedInfo['changedValues'].length] = illuminatiJsAgent.getChangedAttributeValue('select', oldObject.elementUniqueId, 'selected', tempSelectOption.hasOwnProperty('selected'), targetObject[q].selected);

                        Object.keys(tempSelectOption).map(function(objectKey, index) {
                            if (tempSelectOption.hasOwnProperty(objectKey) === true) {
                                try {
                                    objectAttributes[objectKey] = eval('tempSelectOption.' + objectKey);
                                } catch (e) {}
                            }
                        });
                    }
                }

                objectAttributes['obj'] = oldObject.obj;
                objectAttributes['id'] = oldObject.id;
                objectAttributes['name'] = oldObject.name;
            } else if (oldObject.target.type.indexOf('radio') > -1) {
                objectAttributes['type'] = 'radio';

                for (var p=0; p<oldObject.obj.length; p++) {
                    var tempOldRadioObj = oldObject.obj[p];

                    if ((tempOldRadioObj.hasOwnProperty('checked') === true && targetObject.checked === false)
                        || (tempOldRadioObj.hasOwnProperty('checked') === false && targetObject.checked === true)) {
                        var radioElementUniqueId = illuminatiJsAgent.getElementUniqueId(tempOldRadioObj);
                        changedInfo['changedValues'][changedInfo['changedValues'].length] = illuminatiJsAgent.getChangedAttributeValue('radio', radioElementUniqueId, 'checked', tempOldRadioObj.hasOwnProperty('checked'), targetObject.checked, p);

                        Object.keys(tempOldRadioObj).map(function(objectKey, index) {
                            if (tempOldRadioObj.hasOwnProperty(objectKey) === true) {
                                try {
                                    objectAttributes[objectKey] = eval('tempOldRadioObj.' + objectKey);
                                } catch (e) {}
                            }
                        });
                    }
                }
            } else if (oldObject.target.type.indexOf('checkbox') > -1) {
                if ((oldObject.checked === true && targetObject.checked === false)
                    || (oldObject.checked === false && targetObject.checked === true)) {
                    changedInfo['changedValues'][0] = illuminatiJsAgent.getChangedAttributeValue('checkbox', oldObject.elementUniqueId, 'checked', oldObject.checked, targetObject.checked);

                    for (var i=0; i<targetObject.attributes.length; i++) {
                        try {
                            var item = targetObject.attributes.item(i);
                            objectAttributes[item.name] = eval('targetObject.' + item.name);
                        } catch (e) {}
                    }
                }
            } else {
                for (var i=0; i<targetObject.attributes.length; i++) {
                    try {
                        var item = targetObject.attributes.item(i);
                        objectAttributes[item.name] = eval('targetObject.' + item.name);
                    } catch (e) {}
                }

                if (oldObject.obj.type.indexOf('textarea') > -1) {
                    objectAttributes['value'] = targetObject.value;
                }

                Object.keys(objectAttributes).map(function(objectKey, index) {
                    if (oldObject.attributes.hasOwnProperty(objectKey) === true
                        && (oldObject.attributes[objectKey] !== objectAttributes[objectKey])) {
                        changedInfo['changedValues'][changedInfo['changedValues'].length] = illuminatiJsAgent.getChangedAttributeValue(oldObject.target.localName, oldObject.elementUniqueId, objectKey, oldObject.attributes[objectKey], objectAttributes[objectKey]);
                    } else if ((oldObject.attributes.hasOwnProperty(objectKey) === false)
                        && (Array.prototype.inArrayCheck(objectKey, ignoreRemoveKeys) === false)) {
                        changedInfo['removedKey'] = objectKey;
                    }
                });
            }
        }

        if (changedInfo['changedValues'].length > 0) {
            objectAttributes['changedInfo'] = changedInfo;
        }

        return objectAttributes;
    },

    tempBufferToBuffer : function () {
        var elementTempBufferStore = illuminatiJsAgent.getSessionStorage('illuminati-buffer-temp');
        if (elementTempBufferStore !== 'undefined' && elementTempBufferStore !== null) {
            illuminatiJsAgent.setSessionStorage('illuminati-buffer', illuminatiJsAgent.getSessionStorage('illuminati-buffer-temp'));
            illuminatiJsAgent.removeSessionStorage('illuminati-buffer-temp');

            return true;
        }

        return false;
    },

    setElementToSessionStorage : function (newObject) {
        if (newObject.hasOwnProperty('changedInfo') === true) {
            var elementStore = JSON.parse(illuminatiJsAgent.getSessionStorage('illuminati'));
            var key = newObject.target.type + '-' + newObject.elementUniqueId

            var sessionStorageName = 'illuminati-buffer';
            // if illuminatiSendStatus is 'ready', data save to buffer.
            if (illuminatiSendStatus === 'sending') {
                sessionStorageName += '-temp';
            }

            var targetElementStore = JSON.parse(illuminatiJsAgent.getSessionStorage(sessionStorageName));

            if (typeof targetElementStore !== 'undefined' && targetElementStore != null) {
                elementStore = targetElementStore;
            }

            if (Array.isArray(elementStore[key]) === false) {
                elementStore[key]['changedInfo'] = newObject.changedInfo;
            } else {
                var tempArray = elementStore[key];
                elementStore[key] = {
                    obj: tempArray,
                    type: newObject.type,
                    changedInfo: newObject.changedInfo
                };
            }

            illuminatiJsAgent.setSessionStorage(sessionStorageName, JSON.stringify(elementStore));
        }
    },

    addEventInputText : function (element, eventName) {
        if (isEventListener === true) {
            if (typeof eventName[0] !== 'undefined' && eventName[0] !== null) {
                element.addEventListener(eventName[0], function (e) {
                    var screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                    var oldObject = illuminatiJsAgent.getEventData(e);
                    var newObject = illuminatiJsAgent.getNewEventData(oldObject);
                    newObject['screenInfo'] = screenInfo;
                    illuminatiJsAgent.setElementToSessionStorage(newObject);
                });
            }
        } else if (isAttachEvent === true) {
            if (typeof eventName[0] !== 'undefined' && eventName[0] !== null) {
                element.attachEvent('on'+eventName[0], function (e) {
                    var screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                    var oldObject = illuminatiJsAgent.getEventData(e);
                    var newObject = illuminatiJsAgent.getNewEventData(oldObject);
                    newObject['screenInfo'] = screenInfo;
                    illuminatiJsAgent.setElementToSessionStorage(newObject);
                });
            }
        }
    },

    addEventTextarea : function (element, eventName) {
        var screenInfo;
        if (isEventListener === true) {
            if (typeof eventName[0] !== 'undefined' && eventName[0] !== null) {
                element.addEventListener(eventName[0], function (e) {
                    screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                    lastCheckObject = illuminatiJsAgent.getEventData(e);
                });
            }
            if (typeof eventName[1] !== 'undefined' && eventName[1] !== null) {
                element.addEventListener(eventName[1], function (e) {
                    var newObject = illuminatiJsAgent.getNewEventData(lastCheckObject);
                    newObject['screenInfo'] = screenInfo;
                    illuminatiJsAgent.setElementToSessionStorage(newObject);
                });
            }
        } else if (isAttachEvent === true) {
            if (typeof eventName[0] !== 'undefined' && eventName[0] !== null) {
                element.attachEvent('on'+eventName[0], function (e) {
                    screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                    lastCheckObject = illuminatiJsAgent.getEventData(e);
                });
            }
            if (typeof eventName[1] !== 'undefined' && eventName[1] !== null) {
                element.attachEvent('on'+eventName[1], function (e) {
                    var newObject = illuminatiJsAgent.getNewEventData(lastCheckObject);
                    newObject['screenInfo'] = screenInfo;
                    illuminatiJsAgent.setElementToSessionStorage(newObject);
                });
            }
        }
    },

    addEventSelectBox : function (element, eventName) {
        if (isEventListener === true) {
            if (typeof eventName[0] !== 'undefined' && eventName[0] !== null) {
                element.addEventListener(eventName[0], function (e) {
                    var screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                    var oldObject = illuminatiJsAgent.getEventData(e);
                    var newObject = illuminatiJsAgent.getNewEventData(oldObject);
                    newObject['screenInfo'] = screenInfo;
                    illuminatiJsAgent.setElementToSessionStorage(newObject);
                });
            }
        } else if (isAttachEvent === true) {
            if (typeof eventName[0] !== 'undefined' && eventName[0] !== null) {
                element.attachEvent('on'+eventName[0], function (e) {
                    var screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                    var oldObject = illuminatiJsAgent.getEventData(e);
                    var newObject = illuminatiJsAgent.getNewEventData(oldObject);
                    newObject['screenInfo'] = screenInfo;
                    illuminatiJsAgent.setElementToSessionStorage(newObject);
                });
            }
        }
    },

    addEventForm : function (element, eventName) {
        if (isEventListener === true) {
            if (typeof eventName[0] !== 'undefined' && eventName[0] !== null) {
                element.addEventListener(eventName[0], function (e) {
                    if (e.preventDefault) {
                        e.preventDefault();
                    }

                    var sTransactionId = illuminatiJsAgent.getSessionStorage('illuminatiSProcId');
                    if (typeof sTransactionId !== 'undefined' && sTransactionId !== null) {
                        illuminatiJsAgent.generateHiddenInputElement(this, 'illuminatiSProcId', sTransactionId);
                    }
                    var gTransactionId = illuminatiJsAgent.getSessionStorage('illuminatiGProcId');
                    if (typeof gTransactionId !== 'undefined' && gTransactionId !== null) {
                        illuminatiJsAgent.generateHiddenInputElement(this, 'illuminatiGProcId', gTransactionId);
                    }
                    var uniqueUserId = illuminatiJsAgent.getSessionStorage('illuminatiUniqueUserId');
                    if (typeof uniqueUserId !== 'undefined' && uniqueUserId !== null) {
                        illuminatiJsAgent.generateHiddenInputElement(this, 'illuminatiUniqueUserId', uniqueUserId)
                    }

                    try {
                        illuminatiJsAgent.tempBufferToBuffer();
                        illuminatiJsAgent.sendToIlluminati(false);

                        this.submit();
                    } catch (e) {}
                });
            }
        } else if (isAttachEvent === true) {
            if (typeof eventName[0] !== 'undefined' && eventName[0] !== null) {
                element.attachEvent('on'+eventName[0], function (e) {
                    if (e.preventDefault) {
                        e.preventDefault();
                    }

                    var sTransactionId = illuminatiJsAgent.getSessionStorage('illuminatiSProcId');
                    if (typeof sTransactionId !== 'undefined' && sTransactionId !== null) {
                        illuminatiJsAgent.generateHiddenInputElement(this, 'illuminatiSProcId', sTransactionId);
                    }
                    var gTransactionId = illuminatiJsAgent.getSessionStorage('illuminatiGProcId');
                    if (typeof gTransactionId !== 'undefined' && gTransactionId !== null) {
                        illuminatiJsAgent.generateHiddenInputElement(this, 'illuminatiGProcId', gTransactionId);
                    }
                    var uniqueUserId = illuminatiJsAgent.getSessionStorage('illuminatiUniqueUserId');
                    if (typeof uniqueUserId !== 'undefined' && uniqueUserId !== null) {
                        illuminatiJsAgent.generateHiddenInputElement(this, 'illuminatiUniqueUserId', uniqueUserId)
                    }

                    try {
                        illuminatiJsAgent.tempBufferToBuffer();
                        illuminatiJsAgent.sendToIlluminati(false);

                        this.submit();
                    } catch (e) {}
                });
            }
        }
    },

    addEventClick : function (element, eventName) {
        var screenInfo;
        if (isEventListener === true) {
            if (typeof eventName[0] !== 'undefined' && eventName[0] !== null) {
                element.addEventListener(eventName[0], function (e) {
                    screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                    lastCheckObject = illuminatiJsAgent.getEventData(e);
                });
            }
            if (typeof eventName[1] !== 'undefined' && eventName[0] !== null) {
                element.addEventListener(eventName[1], function (e) {
                    var newObject = illuminatiJsAgent.getNewEventData(lastCheckObject);
                    newObject['screenInfo'] = screenInfo;
                    delete(lastCheckObject);
                    illuminatiJsAgent.setElementToSessionStorage(newObject);
                });
            }
        } else if (isAttachEvent === true) {
            if (typeof eventName[0] !== 'undefined' && eventName[0] !== null) {
                element.attachEvent('on'+eventName[0], function (e) {
                    screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                    lastCheckObject = illuminatiJsAgent.getEventData(e);
                });
            }
            if (typeof eventName[1] !== 'undefined' && eventName[0] !== null) {
                element.attachEvent('on'+eventName[1], function (e) {
                    var newObject = illuminatiJsAgent.getNewEventData(lastCheckObject);
                    newObject['screenInfo'] = screenInfo;
                    delete(lastCheckObject);
                    illuminatiJsAgent.setElementToSessionStorage(newObject);
                });
            }
        }
    },

    addEventBaseClick : function (element, eventName) {
        if (isEventListener === true) {
            if (typeof eventName[0] !== 'undefined' && eventName[0] !== null) {
                element.addEventListener(eventName[0], function (e) {
                    var screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                    var oldObject = illuminatiJsAgent.getEventData(e);
                    var newObject = illuminatiJsAgent.getNewEventData(oldObject);
                    newObject['screenInfo'] = screenInfo;
                    illuminatiJsAgent.setElementToSessionStorage(newObject);
                });
            }
        } else if (isAttachEvent === true) {
            if (typeof eventName[0] !== 'undefined' && eventName[0] !== null) {
                element.attachEvent('on'+eventName[0], function (e) {
                    var screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(e);
                    var oldObject = illuminatiJsAgent.getEventData(e);
                    var newObject = illuminatiJsAgent.getNewEventData(oldObject);
                    newObject['screenInfo'] = screenInfo;
                    illuminatiJsAgent.setElementToSessionStorage(newObject);
                });
            }
        }
    },

    domElementInit : function () {
        if(document.readyState === 'complete') {
            window.clearInterval(interval);

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
                    originElement: elem,
                    type: elem.type,
                    id: elem.getAttribute('id'),
                    name: elem.getAttribute('name')
                };

                if (elem.localName === 'form') {
                    elementObj['type'] = 'form';
                }

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

                    radio['originElement'] = elem;
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

            if (isEventListener === true || isAttachEvent === true) {
                for (var key in elementStore) {
                    var eventElem = elementStore[key];

                    if (Array.isArray(eventElem) !== true) {
                        switch (eventElem.type) {
                            case 'text' :
                                illuminatiJsAgent.addEventInputText(eventElem['originElement'], ['keyup']);
                                break;
                            case 'textarea' :
                                illuminatiJsAgent.addEventTextarea(eventElem['originElement'], ['focusin', 'keyup']);
                                break;
                            case 'select-one' :
                                illuminatiJsAgent.addEventSelectBox(eventElem['originElement'], ['change']);
                                break;

                            case 'form' :
                                illuminatiJsAgent.addEventForm(eventElem['originElement'], ['submit']);
                                break;

                            default :
                                illuminatiJsAgent.addEventClick(eventElem['originElement'], ['mouseup', 'click']);
                                break;
                        }
                    } else {
                        for (var n=0; n<eventElem.length; n++) {
                            var tmpRadioObj = eventElem[n];
                            illuminatiJsAgent.addEventBaseClick(tmpRadioObj['originElement'], ['click']);
                        }
                    }
                }
            }

            for (var key in elementStore) {
                delete elementStore[key]['originElement'];
            }

            illuminatiJsAgent.setSessionStorage('illuminati', JSON.stringify(elementStore));

            if (isFirst === true) {
                isFirst = false;
                window.setTimeout(function () {
                    illuminatiJsAgent.domElementInit();
                }, 3000);
            }
        }
    },

    sendToIlluminati : function (isAsync) {
        if (illuminatiSendStatus === 'done') {
            var elementStore = JSON.parse(illuminatiJsAgent.getSessionStorage('illuminati-buffer'));
            if (typeof elementStore === 'undefined' || elementStore === null) {
                return;
            }

            illuminatiSendStatus = 'sending';

            var illuminatiJsModel = {
                illuminatiGProcId: illuminatiJsAgent.getSessionStorage('illuminatiGProcId'),
                illuminatiSProcId: illuminatiJsAgent.getSessionStorage('illuminatiSProcId'),
                illuminatiUniqueUserId: illuminatiJsAgent.getSessionStorage('illuminatiUniqueUserId')
            };

            var ignoreCheckEventStoreKeys = ['alreadySent'];
            Object.keys(elementStore).map(function(objectKey, index) {
                if (Array.prototype.inArrayCheck(objectKey, ignoreCheckEventStoreKeys) === false) {
                    if (elementStore[objectKey].hasOwnProperty('changedInfo') === true) {
                        var changedObj = elementStore[objectKey];

                        if (illuminatiJsModel.hasOwnProperty('changedValues') === true) {
                            for (var l=0; l<changedObj['changedInfo']['changedValues'].length; l++) {
                                illuminatiJsModel['changedValues'][illuminatiJsModel['changedValues'].length] = changedObj['changedInfo']['changedValues'][l];
                            }
                        } else {
                            illuminatiJsModel['changedValues'] = changedObj['changedInfo']['changedValues'];
                        }
                    }
                }
            });

            if (illuminatiJsModel.hasOwnProperty('changedValues') === true) {
                try {
                    illuminatiAjax.sendByPost(collectorUrl, isAsync, illuminatiJsModel);
                } catch (e) {
                    console.log(e);
                }

                illuminatiSendStatus = 'done';
                if (illuminatiJsAgent.tempBufferToBuffer() === false) {
                    illuminatiJsAgent.removeSessionStorage('illuminati-buffer');
                }
            }
        }
    }
};

var illuminatiXhr;
var illuminatiAjax = {
    xmlHttpObjType : null,
    timeoutMs : 5000,

    init : function () {
        if(typeof XMLHttpRequest !== 'undefined'){
            illuminatiXhr = new XMLHttpRequest();
        } else {
            var versions = ['MSXML2.XmlHttp.5.0',
                'MSXML2.XmlHttp.4.0',
                'MSXML2.XmlHttp.3.0',
                'MSXML2.XmlHttp.2.0',
                'Microsoft.XmlHttp']

            for(var i=0, len=versions.length; i<len; i++) {
                try {
                    illuminatiXhr = new ActiveXObject(versions[i]);
                    break;
                }
                catch(e){}
            } // end for
        }
    },

    setRequestHeaderOnAjaxEvent : function (illuminatiXhr) {
        var illuminatiGProcId = illuminatiJsAgent.getSessionStorage('illuminatiGProcId');
        if (typeof illuminatiGProcId !== 'undefined' && illuminatiGProcId !== null) {
            illuminatiXhr.setRequestHeader('illuminatiGProcId', illuminatiGProcId);
        }
        var illuminatiSProcId = illuminatiJsAgent.getSessionStorage('illuminatiSProcId');
        if (typeof illuminatiSProcId !== 'undefined' && illuminatiSProcId !== null) {
            illuminatiXhr.setRequestHeader('illuminatiSProcId', illuminatiSProcId);
        }
        var illuminatiUniqueUserId = illuminatiJsAgent.getSessionStorage('illuminatiUniqueUserId');
        if (typeof illuminatiUniqueUserId !== 'undefined' && illuminatiUniqueUserId !== null) {
            illuminatiXhr.setRequestHeader('illuminatiUniqueUserId', illuminatiUniqueUserId);
        }
    },

    sendByPost : function (requestUrl, isAsync, data) {
        // todo : IE 6, 7 does not support cross-domain request. because of send POST method using hidden iframe.
        //var msieVersion = ['msie 6.0', 'msie 7.0'];

        // if (this.checkIEBrowser(msieVersion) === true) {
        //     // not yet
        // } else {
        illuminatiXhr.open('POST', requestUrl, isAsync);

        // if (isAsync === false) {
        //     illuminatiXhr.onreadystatechange = this.handleStateChange();
        // }

        illuminatiXhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
        // set header
        //illuminatiAjax.setRequestHeaderOnAjaxEvent(illuminatiXhr);
        //xmlHttp.withCredentials = true;
        illuminatiXhr.send(JSON.stringify(data));

        setTimeout(function () {
            illuminatiXhr.abort();
        }, this.timeoutMs);

        if (isAsync === false && illuminatiXhr.readyState === 4 && illuminatiXhr.status === 200) {
            return true;
        }
    }
};

var illuminatiSendStatus = 'done';
var lastCheckObject;
var isFirst = true;
var collectIntervalTimeMs = 15000;
var autoSendToIlluminati;
var collectorUrl = '/illuminati/js/collector';
var isAutoCollect = false;
var isEventListener = false;
var isAttachEvent = false;

if (document.addEventListener) {
    isEventListener = true;
} else if (document.attachEvent) {
    isAttachEvent = true;
}

if (isEventListener === true || isAttachEvent === true) {
    var interval = window.setInterval(function() {
        illuminatiJsAgent.domElementInit();
    }, 100);

    illuminatiAjax.init();
}