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

    passElementType : ['form', 'input', 'select', 'textarea'],
    illuminatiInputElementType : ['text', 'radio', 'checkbox'],

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

    getChangedAttributeValue : function (type, elementUniqueId, attributeName, oldData, newData) {
        var changedValue = {};
        changedValue['elementType'] = type;
        changedValue['elementUniqueId'] = elementUniqueId;
        changedValue['attributeName'] = attributeName;
        changedValue['oldData'] = String(oldData);
        changedValue['newData'] = String(newData);

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
                            objectAttributes[objectKey] = eval('tempSelectOption.' + objectKey);
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

                    if ((tempOldRadioObj.hasOwnProperty('checked') === true && targetObject[p].checked === false)
                        || (tempOldRadioObj.hasOwnProperty('checked') === false && targetObject[p].checked === true)) {
                        var radioElementUniqueId = this.getElementUniqueId(tempOldRadioObj);
                        changedInfo['changedValues'][changedInfo['changedValues'].length] = illuminatiJsAgent.getChangedAttributeValue('radio', radioElementUniqueId, 'checked', tempOldRadioObj.hasOwnProperty('checked'), targetObject[p].checked);

                        Object.keys(tempOldRadioObj).map(function(objectKey, index) {
                            objectAttributes[objectKey] = eval('tempOldRadioObj.' + objectKey);
                        });
                    }
                }
            } else if (oldObject.target.type.indexOf('checkbox') > -1) {
                if ((oldObject.checked === true && targetObject.checked === false)
                    || (oldObject.checked === false && targetObject.checked === true)) {
                    changedInfo['changedValues'][0] = illuminatiJsAgent.getChangedAttributeValue('checkbox', oldObject.elementUniqueId, 'checked', oldObject.checked, targetObject.checked);

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

                if (oldObject.obj.type.indexOf('textarea') > -1) {
                    objectAttributes['value'] = targetObject.value;
                }

                Object.keys(objectAttributes).map(function(objectKey, index) {
                    var value = objectAttributes[objectKey];

                    if (oldObject.attributes.hasOwnProperty(objectKey) === true
                        && (oldObject.attributes[objectKey] !== objectAttributes[objectKey])) {
                        changedInfo['changedValues'][0] = illuminatiJsAgent.getChangedAttributeValue(oldObject.target.localName, oldObject.elementUniqueId, objectKey, oldObject.attributes[objectKey], objectAttributes[objectKey]);
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
        var elementTempBufferStore = sessionStorage.getItem('illuminati-buffer-temp');
        if (elementTempBufferStore !== 'undefined' && elementTempBufferStore !== null) {
            sessionStorage.setItem('illuminati-buffer', sessionStorage.getItem('illuminati-buffer-temp'));
            sessionStorage.removeItem('illuminati-buffer-temp');

            return true;
        }

        return false;
    },

    setElementToSessionStorage : function (newObject) {
        if (newObject.hasOwnProperty('changedInfo') === true) {
            var elementStore = JSON.parse(sessionStorage.getItem('illuminati'));
            var key = newObject.target.type + '-' + newObject.elementUniqueId;

            var sessionStorageName = 'illuminati-buffer';
            // if illuminatiSendStatus is 'ready', data save to buffer.
            if (illuminatiSendStatus === 'sending') {
                sessionStorageName += '-temp';
            }

            var targetElementStore = JSON.parse(sessionStorage.getItem(sessionStorageName));

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

            sessionStorage.setItem(sessionStorageName, JSON.stringify(elementStore));
        }
    },

    sendToIlluminati : function (isAsync) {
        console.log(illuminatiSendStatus);
        if (illuminatiSendStatus === 'done') {
            var elementStore = JSON.parse(sessionStorage.getItem('illuminati-buffer'));
            console.log(elementStore);
            if (typeof elementStore === 'undefined' || elementStore === null) {
                return;
            }

            illuminatiSendStatus = 'sending';

            var illuminatiJsModel = {
                illuminatiGProcId: sessionStorage.getItem('illuminatiGProcId')
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
                illuminatiAjax.sendByPost('/illuminati/js/collector', isAsync, illuminatiJsModel);
                illuminatiSendStatus = 'done';
                if (illuminatiJsAgent.tempBufferToBuffer() === false) {
                    sessionStorage.removeItem('illuminati-buffer');
                }
            }
        }
    }
};

sessionStorage.setItem('illuminatiGProcId', illuminatiJsAgent.generateGlobalTransactionId());

var illuminatiSendStatus = 'done';
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
                            console.log('submit');
                            illuminatiJsAgent.tempBufferToBuffer();
                            illuminatiJsAgent.sendToIlluminati(false);

                            return true;
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

var illuminatiXhr;
var illuminatiAjax = {
    xmlHttpObjType : null,
    timeoutMs : 5000,

    // IE browser check
    checkIEBrowser : function (vCheck) {
        if(navigator.appName.toLowerCase() === "microsoft internet explorer") {
            var tmpAppVersion = navigator.appVersion.toLowerCase();

            var pos = tmpAppVersion.indexOf("msie");
            var ver = tmpAppVersion.substr(pos,8);

            if (vCheck == null || vCheck == 'undefined') {
                return true;
            } else {
                if (sLogger.utils.includeArrayData(vCheck, ver)) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    },

    init : function () {
        if(typeof XMLHttpRequest !== 'undefined'){
            illuminatiXhr = new XMLHttpRequest();
        } else {
            var versions = ["MSXML2.XmlHttp.5.0",
                "MSXML2.XmlHttp.4.0",
                "MSXML2.XmlHttp.3.0",
                "MSXML2.XmlHttp.2.0",
                "Microsoft.XmlHttp"]

            for(var i = 0, len = versions.length; i < len; i++) {
                try {
                    illuminatiXhr = new ActiveXObject(versions[i]);
                    break;
                }
                catch(e){}
            } // end for
        }
    },

    sendByPost : function (requestUrl, isAsync, data) {
        // IE 6, 7 does not support cross-domain request. because of send POST method using hidden iframe.
        var msieVersion = ['msie 6.0', 'msie 7.0'];

        if (this.checkIEBrowser(msieVersion) === true) {
            // not yet
        } else {
            console.log(isAsync);
            illuminatiXhr.open('POST', requestUrl, isAsync);

            if (isAsync === false) {
                illuminatiXhr.onreadystatechange = this.handleStateChange();
            }

            illuminatiXhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
            illuminatiXhr.setRequestHeader('illuminatiGProcId', sessionStorage.getItem('illuminatiGProcId'));
            //xmlHttp.withCredentials = true;
            illuminatiXhr.send(JSON.stringify(data));

            setTimeout(function () {
                illuminatiXhr.abort();
            }, this.timeoutMs);
        }
    },

    // ajax result handling
    handleStateChange : function () {
        /*
         * 0 : open()
         * 1 : loading..
         * 2 : loading completed
         * 3 : server processing
         * 4 : Server processing is completed
         */
        switch (illuminatiXhr.readyState) {
            case 0 :
                break;
            case 1 :
                break;
            case 2 :
                break;
            case 3 :
                break;
            case 4 :
                /*
                 * 200 : processing is completed
                 * 403 : forbidden
                 * 404 : not found error
                 * 500 : internal server error
                 */
                switch (illuminatiXhr.status) {
                    case 200 :
                        console.log("ajax completed. ", "processing is completed");
                        break;
                    case 403 :
                        console.log("ajax forbidden error. ", "forbidden");
                        break;
                    case 404 :
                        console.log("ajax not found error.", "not found error");
                        break;
                    case 500 :
                        console.log("ajax internal server error.", "internal server error");
                        break;
                }
                break;
        }

        return false;
    }
};

illuminatiAjax.init();

var sendToIlluminati = setInterval(function () {
    illuminatiJsAgent.sendToIlluminati(true);
}, 15000);