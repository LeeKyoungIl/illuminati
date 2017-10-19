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
    /* Wrap onreadystaechange callback */
    var callback = typeof this.onreadystatechange == 'function' ? this.onreadystatechange : null;
    this.onreadystatechange = function() {
        if (callback) {
            callback.apply(this, arguments);
        }
        if (this.readyState == 4) {
            if (this.responseURL.indexOf(collectorUrl) == -1) {
                try {
                    illuminatiJsAgent.tempBufferToBuffer();
                    illuminatiJsAgent.sendToIlluminati(false);
                } catch (e) {}
            }
        }
    }
    _send.apply(this, arguments);
};

var illuminatiJsAgent = {
    passElementType : ['form', 'input', 'select', 'textarea'],
    illuminatiInputElementType : ['text', 'radio', 'checkbox'],

    init : function (illuminatiUniqueUserId) {
        // session transaction Id
        window.sessionStorage.setItem('illuminatiSProcId', illuminatiJsAgent.generateTransactionId('S'));

        var gTransactionId = window.sessionStorage.getItem('illuminatiGProcId');

        if (typeof gTransactionId === 'undefined' || gTransactionId === null) {
            window.sessionStorage.setItem('illuminatiGProcId', String(illuminatiJsAgent.generateTransactionId('G')));
        }

        if (typeof illuminatiUniqueUserId !== 'undefined' && illuminatiUniqueUserId !== null) {
            window.sessionStorage.setItem('illuminatiUniqueUserId', String(illuminatiUniqueUserId));
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
            gTransactionId[gTransactionId.length] = this.generateUDID();
        }

        return gTransactionId;
    },

    generateTransactionId : function (type) {
        return this.generateSumUDID().join('')+'-illuminati'+type+'ProcId';
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

        if (elem.localName === 'input' && Array.prototype.inArrayCheck(elem.type, this.illuminatiInputElementType) === false) {
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
            var firstElementData = JSON.parse(window.sessionStorage.getItem('illuminati'));
            var key = e.target.type + '-' + this.getElementUniqueId(eventObject);

            eventObject['obj'] = firstElementData[key];
        } else if (eventObject['attributes'].hasOwnProperty('type') === true) {
            if (eventObject['attributes']['type'] === 'checkbox') {
                eventObject['checked'] = e.target.checked;
            } else if (eventObject['attributes']['type'] === 'radio') {
                var firstElementData = JSON.parse(window.sessionStorage.getItem('illuminati'));
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
                                objectAttributes[objectKey] = eval('tempSelectOption.' + objectKey);
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
                        var radioElementUniqueId = this.getElementUniqueId(tempOldRadioObj);
                        changedInfo['changedValues'][changedInfo['changedValues'].length] = illuminatiJsAgent.getChangedAttributeValue('radio', radioElementUniqueId, 'checked', tempOldRadioObj.hasOwnProperty('checked'), targetObject.checked, p);

                        Object.keys(tempOldRadioObj).map(function(objectKey, index) {
                            if (tempOldRadioObj.hasOwnProperty(objectKey) === true) {
                                objectAttributes[objectKey] = eval('tempOldRadioObj.' + objectKey);
                            }
                        });
                    }
                }
            } else if (oldObject.target.type.indexOf('checkbox') > -1) {
                if ((oldObject.checked === true && targetObject.checked === false)
                    || (oldObject.checked === false && targetObject.checked === true)) {
                    changedInfo['changedValues'][0] = illuminatiJsAgent.getChangedAttributeValue('checkbox', oldObject.elementUniqueId, 'checked', oldObject.checked, targetObject.checked);

                    for (var i=0; i<targetObject.attributes.length; i++) {
                        var item = targetObject.attributes.item(i);
                        if (targetObject.hasOwnProperty(item.name) === true) {
                            objectAttributes[item.name] = eval('targetObject.' + item.name);
                        }
                    }
                }
            } else {
                for (var i=0; i<targetObject.attributes.length; i++) {
                    var item = targetObject.attributes.item(i);
                    if (targetObject.hasOwnProperty(item.name) === true) {
                        objectAttributes[item.name] = eval('targetObject.' + item.name);
                    }
                }

                if (oldObject.obj.type.indexOf('textarea') > -1) {
                    objectAttributes['value'] = targetObject.value;
                }

                Object.keys(objectAttributes).map(function(objectKey, index) {
                    var value = objectAttributes[objectKey];

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
        var elementTempBufferStore = window.sessionStorage.getItem('illuminati-buffer-temp');
        if (elementTempBufferStore !== 'undefined' && elementTempBufferStore !== null) {
            window.sessionStorage.setItem('illuminati-buffer', window.sessionStorage.getItem('illuminati-buffer-temp'));
            window.sessionStorage.removeItem('illuminati-buffer-temp');

            return true;
        }

        return false;
    },

    setElementToSessionStorage : function (newObject) {
        if (newObject.hasOwnProperty('changedInfo') === true) {
            var elementStore = JSON.parse(window.sessionStorage.getItem('illuminati'));
            var key = newObject.target.type + '-' + newObject.elementUniqueId;

            var sessionStorageName = 'illuminati-buffer';
            // if illuminatiSendStatus is 'ready', data save to buffer.
            if (illuminatiSendStatus === 'sending') {
                sessionStorageName += '-temp';
            }

            var targetElementStore = JSON.parse(window.sessionStorage.getItem(sessionStorageName));

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

            window.sessionStorage.setItem(sessionStorageName, JSON.stringify(elementStore));
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
                    obj: elem,
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
                var eventElem = elementStore[key];

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

                        case 'form' :
                            eventElem['obj'].addEventListener('submit', function (e) {
                                if (e.preventDefault) {
                                    e.preventDefault();
                                }

                                var sTransactionId = window.sessionStorage.getItem('illuminatiSProcId');
                                if (typeof sTransactionId !== 'undefined' && sTransactionId !== null) {
                                    var sInput = document.createElement('input');
                                    sInput.type = 'hidden';
                                    sInput.name = 'illuminatiSProcId';
                                    sInput.value = String(sTransactionId);
                                    this.appendChild(sInput);
                                }
                                var gTransactionId = window.sessionStorage.getItem('illuminatiGProcId');
                                if (typeof gTransactionId !== 'undefined' && gTransactionId !== null) {
                                    var gInput = document.createElement('input');
                                    gInput.type = 'hidden';
                                    gInput.name = 'illuminatiGProcId';
                                    gInput.value = String(gTransactionId);
                                    this.appendChild(gInput);
                                }
                                var uniqueUserId = window.sessionStorage.getItem('illuminatiUniqueUserId');
                                if (typeof uniqueUserId !== 'undefined' && uniqueUserId !== null) {
                                    var uInput = document.createElement('input');
                                    uInput.type = 'hidden';
                                    uInput.name = 'illuminatiUniqueUserId';
                                    uInput.value = String(uniqueUserId);
                                    this.appendChild(uInput);
                                }

                                try {
                                    illuminatiJsAgent.tempBufferToBuffer();
                                    illuminatiJsAgent.sendToIlluminati(false);
                                } catch (e) {}

                                this.submit();
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

            window.sessionStorage.setItem('illuminati', JSON.stringify(elementStore));

            if (isFirst === true) {
                isFirst = false;
                window.setTimeout(function () {
                    illuminatiJsAgent.domElementInit();
                }, 2000);
            }
        }
    },

    sendToIlluminati : function (isAsync) {
        if (illuminatiSendStatus === 'done') {
            var elementStore = JSON.parse(window.sessionStorage.getItem('illuminati-buffer'));
            if (typeof elementStore === 'undefined' || elementStore === null) {
                return;
            }

            illuminatiSendStatus = 'sending';

            var illuminatiJsModel = {
                illuminatiGProcId: window.sessionStorage.getItem('illuminatiGProcId'),
                illuminatiSProcId: window.sessionStorage.getItem('illuminatiSProcId'),
                illuminatiUniqueUserId: window.sessionStorage.getItem('illuminatiUniqueUserId')
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
                } catch (e) {}

                illuminatiSendStatus = 'done';
                if (illuminatiJsAgent.tempBufferToBuffer() === false) {
                    window.sessionStorage.removeItem('illuminati-buffer');
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

        var illuminatiGProcId = window.sessionStorage.getItem('illuminatiGProcId');
        if (typeof illuminatiGProcId !== 'undefined' && illuminatiGProcId !== null) {
            illuminatiXhr.setRequestHeader('illuminatiGProcId', illuminatiGProcId);
        }
        var illuminatiSProcId = window.sessionStorage.getItem('illuminatiSProcId');
        if (typeof illuminatiSProcId !== 'undefined' && illuminatiSProcId !== null) {
            illuminatiXhr.setRequestHeader('illuminatiSProcId', illuminatiSProcId);
        }
        var illuminatiUniqueUserId = window.sessionStorage.getItem('illuminatiUniqueUserId');
        if (typeof illuminatiUniqueUserId !== 'undefined' && illuminatiUniqueUserId !== null) {
            illuminatiXhr.setRequestHeader('illuminatiUniqueUserId', illuminatiUniqueUserId);
        }
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
var collectorUrl = '/illuminati/js/collector';


if (illuminatiJsAgent.checkIsIe() === true) {
    alert('IE is not yet supported.');
    console.info('IE is not yet supported.');
} else {
    illuminatiAjax.init();

    var interval = window.setInterval(function() {
        illuminatiJsAgent.domElementInit();
    }, 100);

    var sendToIlluminati = window.setInterval(function () {
        illuminatiJsAgent.sendToIlluminati(true);
    }, collectIntervalTimeMs);
}