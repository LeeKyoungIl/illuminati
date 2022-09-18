/*
 * Copyright 2017 Phoboslabs.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
XMLHttpRequest.prototype.send = function () {
  illuminatiAjax.setRequestHeaderOnAjaxEvent(this);

  if (isIlluminatiRequest === false) {
    illuminatiJsAgent.tempBufferToBuffer();
    illuminatiJsAgent.sendToIlluminati(false);
  }

  _send.apply(this, arguments);
};

var illuminatiJsAgent = {
  addedListeners: {},
  passElementType: ['form', 'input', 'select', 'textarea'],
  illuminatiInputElementType: ['text', 'radio', 'checkbox'],
  collectorUrl: '/illuminati/js/collector',
  collectIntervalTimeMs: 15000, // per 15 sec
  isAutoCollect: true,
  isFirstInitDomElement: true,
  illuminatiSendStatus: 'done',
  lastCheckObject: null,
  autoSendToIlluminati: null,
  screenInfo: null,

  init: function () {
    if (isEventListener === false && isAttachEvent === false) {
      return;
    }
    // session transaction Id
    illuminatiJsAgent.setSessionStorage('illuminatiSProcId',
        illuminatiJsAgent.generateTransactionId('S'));

    var gTransactionId = illuminatiJsAgent.getSessionStorage(
        'illuminatiGProcId');

    if (typeof gTransactionId === 'undefined' || gTransactionId === null) {
      illuminatiJsAgent.setSessionStorage('illuminatiGProcId',
          String(illuminatiJsAgent.generateTransactionId('G')));
    }

    if (illuminatiJsAgent.isAutoCollect === true) {
      illuminatiJsAgent.autoSendToIlluminati = window.setInterval(function () {
        illuminatiJsAgent.sendToIlluminati(true);
      }, illuminatiJsAgent.collectIntervalTimeMs);
    }
  },

  ObjectIsEmpty: function (obj) {
    if (obj === undefined || obj === null) {
      return true;
    }
    for (var key in obj) {
      if (obj.hasOwnProperty(key) === true) {
        return false;
      }
    }
    return true;
  },

  setIsAutoCollect: function (isAutoCollect) {
    illuminatiJsAgent.isAutoCollect = isAutoCollect;
  },

  setCollectorUrl: function (collectorUrl) {
    illuminatiJsAgent.collectorUrl = collectorUrl;
  },

  setcollectIntervalTimeMs: function (collectIntervalTimeMs) {
    illuminatiJsAgent.collectIntervalTimeMs = collectIntervalTimeMs;
  },

  setUniqueUserId: function (illuminatiUniqueUserId) {
    if (typeof illuminatiUniqueUserId !== 'undefined' && illuminatiUniqueUserId
        !== null) {
      illuminatiJsAgent.setSessionStorage('illuminatiUniqueUserId',
          String(illuminatiUniqueUserId));
    }
  },

  objectSize: function (obj) {
    if (illuminatiJsAgent.ObjectIsEmpty(obj) === true) {
      return 0;
    }
    var size = 0, key;
    for (key in obj) {
      if (obj.hasOwnProperty(key)) {
        size++;
      }
    }
    return size;
  },

  setSessionStorage: function (key, value) {
    try {
      window.sessionStorage.setItem(key, value);
    } catch (e) {
      console.debug("This browser is not support session storage.");
    }
  },

  getSessionStorage: function (key) {
    try {
      return window.sessionStorage.getItem(key);
    } catch (e) {
      console.debug("This browser is not support session storage.");
      return null;
    }
  },

  removeSessionStorage: function (key) {
    try {
      window.sessionStorage.removeItem(key);
    } catch (e) {
      console.debug("This browser is not support session storage.");
    }
  },

  checkIsIe: function () {
    if (navigator.appName == 'Microsoft Internet Explorer'
        || !!(navigator.userAgent.match(/Trident/) || navigator.userAgent.match(
            /rv:11/))) {
      return true;
    }

    return false;
  },

  generateUDID: function () {
    return Math.floor((1 + Math.random()) * 0x10000)
    .toString(16)
    .substring(1);
  },

  generateSumUDID: function () {
    var gTransactionId = [];
    for (var i = 0; i < 8; i++) {
      gTransactionId[gTransactionId.length] = illuminatiJsAgent.generateUDID();
    }

    return gTransactionId;
  },

  generateTransactionId: function (type) {
    return illuminatiJsAgent.generateSumUDID().join('') + '-illuminati' + type
        + 'ProcId';
  },

  generateHiddenInputElement: function (form, name, value, id) {
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

  getElementUniqueId: function (elementObj) {
    var uniqueId = null;
    try {
      uniqueId = (typeof elementObj.id !== 'undefined' && elementObj.id !== null
          && elementObj.id.trim() !== '') ? elementObj.id : elementObj.name;
    } catch (e) {
    }

    if (uniqueId !== null && uniqueId.trim() === '') {
      uniqueId = '';
    }

    return uniqueId;
  },

  checkPassElement: function (elem) {
    if (Array.prototype.inArrayCheck(elem.localName,
        illuminatiJsAgent.passElementType) === false) {
      return true;
    }

    if (typeof elem.getAttribute('id') === 'undefined' && elem.getAttribute(
        'name') === 'undefined') {
      return true;
    }

    var isUniqueIdEmptyCheck = false;
    if (typeof elem.getAttribute('id') !== 'undefined' && elem.getAttribute(
        'id') != null && elem.getAttribute('id').trim() === '') {
      isUniqueIdEmptyCheck = true;
    }
    if (typeof elem.getAttribute('name') !== 'undefined' && elem.getAttribute(
        'name') != null && elem.getAttribute('name').trim() === '') {
      isUniqueIdEmptyCheck = true;
    }
    if (isUniqueIdEmptyCheck === true) {
      return true;
    }

    if (elem.localName === 'input' && Array.prototype.inArrayCheck(elem.type,
        illuminatiJsAgent.illuminatiInputElementType) === false) {
      return true;
    }

    return false;
  },

  getScreenInfoAtEvent: function (e) {
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

  getElementObj: function (object) {
    if (typeof object['id'] !== 'undefined' && object['id'] !== null
        && object['id'].trim() !== '') {
      return document.getElementById(object['id'].trim());
    } else if (typeof object['name'] !== 'undefined' && object['name'] !== null
        && object['name'].trim() !== '') {
      return document.getElementsByName(object['name'].trim());
    }

    return null;
  },

  getChangedAttributeValue: function (type, elementUniqueId, attributeName,
      oldData, newData, index) {
    var changedValue = {};
    changedValue['elementType'] = type;
    changedValue['elementUniqueId'] = elementUniqueId;
    changedValue['attributeName'] = attributeName;
    changedValue['oldData'] = String(oldData);
    changedValue['newData'] = String(newData);
    changedValue['index'] = (typeof index !== 'undefined' && index !== null)
        ? index : 0;

    return changedValue;
  },

  getEventData: function (e) {
    var eventObject = {};
    var objectAttributes = {};

    for (var i = 0; i < e.target.attributes.length; i++) {
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
      var firstElementData = JSON.parse(
          illuminatiJsAgent.getSessionStorage('illuminati'));
      var key = e.target.type + '-' + illuminatiJsAgent.getElementUniqueId(
          eventObject);

      eventObject['obj'] = firstElementData[key];
    } else if (illuminatiJsAgent.ObjectIsEmpty(eventObject['attributes'])
        === false && eventObject['attributes'].hasOwnProperty('type')
        === true) {
      if (eventObject['attributes']['type'] === 'checkbox') {
        eventObject['checked'] = e.target.checked;
      } else if (eventObject['attributes']['type'] === 'radio') {
        var firstElementData = JSON.parse(
            illuminatiJsAgent.getSessionStorage('illuminati'));
        var key = e.target.type + '-' + illuminatiJsAgent.getElementUniqueId(
            eventObject);

        eventObject['obj'] = firstElementData[key];
      }
    }

    eventObject['elementUniqueId'] = illuminatiJsAgent.getElementUniqueId(
        eventObject);
    eventObject['target'] = e.target;

    return eventObject;
  },

  getNewEventData: function (oldObject) {
    try {
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
          for (var q = 0; q < oldObject.obj.option.length; q++) {
            var tempSelectOption = oldObject.obj.option[q];

            if (illuminatiJsAgent.ObjectIsEmpty(tempSelectOption) === true) {
              continue;
            }

            if ((tempSelectOption.hasOwnProperty('selected') === true
                    && targetObject[q].selected === false)
                || (tempSelectOption.hasOwnProperty('selected') === false
                    && targetObject[q].selected === true)) {
              changedInfo['changedValues'][changedInfo['changedValues'].length] = illuminatiJsAgent.getChangedAttributeValue(
                  'select', oldObject.elementUniqueId, 'selected',
                  tempSelectOption.hasOwnProperty('selected'),
                  targetObject[q].selected, q);

              Object.keys(tempSelectOption).map(function (objectKey, index) {
                if (tempSelectOption.hasOwnProperty(objectKey) === true) {
                  try {
                    objectAttributes[objectKey] = eval(
                        'tempSelectOption.' + objectKey);
                  } catch (e) {
                  }
                }
              });
            }
          }

          objectAttributes['obj'] = oldObject.obj;
          objectAttributes['id'] = oldObject.id;
          objectAttributes['name'] = oldObject.name;
        } else if (oldObject.target.type.indexOf('radio') > -1) {
          objectAttributes['type'] = 'radio';

          for (var p = 0; p < oldObject.obj.length; p++) {
            var tempOldRadioObj = oldObject.obj[p];
            var tempTargetObject = targetObject[p];

            if ((tempOldRadioObj.hasOwnProperty('checked') === true
                    && tempOldRadioObj.checked === false)
                || (tempTargetObject.hasOwnProperty('checked') === false)
                || (tempOldRadioObj.hasOwnProperty('checked') === true
                    && tempTargetObject.checked === true)) {
              var radioElementUniqueId = illuminatiJsAgent.getElementUniqueId(
                  tempOldRadioObj);
              var oldChecked = (typeof tempOldRadioObj === 'undefined'
                  || tempOldRadioObj === null) ? '' : tempOldRadioObj.checked;
              var newChecked = (typeof tempTargetObject === 'undefined'
                  || tempTargetObject === null) ? '' : tempTargetObject.checked;
              changedInfo['changedValues'][changedInfo['changedValues'].length] = illuminatiJsAgent.getChangedAttributeValue(
                  'radio', radioElementUniqueId, 'checked', oldChecked,
                  newChecked, p);

              Object.keys(tempOldRadioObj).map(function (objectKey, index) {
                if (tempTargetObject.hasOwnProperty('checked') === true) {
                  try {
                    objectAttributes[objectKey] = eval(
                        'tempTargetObject.' + objectKey);
                  } catch (e) {
                  }
                }
              });
            }
          }
        } else if (oldObject.target.type.indexOf('checkbox') > -1) {
          if ((oldObject.checked === true && targetObject.checked === false)
              || (oldObject.checked === false && targetObject.checked
                  === true)) {
            changedInfo['changedValues'][0] = illuminatiJsAgent.getChangedAttributeValue(
                'checkbox', oldObject.elementUniqueId, 'checked',
                oldObject.checked, targetObject.checked);

            for (var i = 0; i < targetObject.attributes.length; i++) {
              try {
                var item = targetObject.attributes.item(i);
                objectAttributes[item.name] = eval('targetObject.' + item.name);
              } catch (e) {
              }
            }
          }
        } else {
          for (var i = 0; i < targetObject.attributes.length; i++) {
            try {
              var item = targetObject.attributes.item(i);
              objectAttributes[item.name] = eval('targetObject.' + item.name);
            } catch (e) {
            }
          }

          if (oldObject.obj.type.indexOf('textarea') > -1) {
            objectAttributes['value'] = targetObject.value;
          }

          Object.keys(objectAttributes).map(function (objectKey, index) {
            if (illuminatiJsAgent.ObjectIsEmpty(oldObject.attributes)
                === false) {
              if (oldObject.attributes.hasOwnProperty(objectKey) === true
                  && (oldObject.attributes[objectKey]
                      !== objectAttributes[objectKey])) {
                changedInfo['changedValues'][changedInfo['changedValues'].length] = illuminatiJsAgent.getChangedAttributeValue(
                    oldObject.target.localName, oldObject.elementUniqueId,
                    objectKey, oldObject.attributes[objectKey],
                    objectAttributes[objectKey]);
              } else if ((oldObject.attributes.hasOwnProperty(objectKey)
                      === false)
                  && (Array.prototype.inArrayCheck(objectKey, ignoreRemoveKeys)
                      === false)) {
                changedInfo['removedKey'] = objectKey;
              }
            }
          });
        }
      }

      if (changedInfo['changedValues'].length > 0) {
        objectAttributes['changedInfo'] = changedInfo;
      }

      return objectAttributes;
    } catch (e) {
      console.debug(e);
      return oldObject;
    }
  },

  tempBufferToBuffer: function () {
    var elementTempBufferStore = illuminatiJsAgent.getSessionStorage(
        'illuminati-buffer-temp');
    if (elementTempBufferStore !== 'undefined' && elementTempBufferStore
        !== null) {
      illuminatiJsAgent.setSessionStorage('illuminati-buffer',
          illuminatiJsAgent.getSessionStorage('illuminati-buffer-temp'));
      illuminatiJsAgent.removeSessionStorage('illuminati-buffer-temp');

      return true;
    }

    return false;
  },

  setElementToSessionStorage: function (newObject) {
    if (illuminatiJsAgent.ObjectIsEmpty(newObject) === true) {
      return;
    }
    if (newObject.hasOwnProperty('changedInfo') === true) {
      var elementStore = JSON.parse(
          illuminatiJsAgent.getSessionStorage('illuminati'));
      var key = newObject.target.type + '-' + newObject.elementUniqueId

      var sessionStorageName = 'illuminati-buffer';
      // if illuminatiSendStatus is 'ready', data save to buffer.
      if (illuminatiJsAgent.illuminatiSendStatus === 'sending') {
        sessionStorageName += '-temp';
      }

      var targetElementStore = JSON.parse(
          illuminatiJsAgent.getSessionStorage(sessionStorageName));

      if (typeof targetElementStore !== 'undefined' && targetElementStore
          != null) {
        elementStore = targetElementStore;
      }

      if (illuminatiJsAgent.ObjectIsEmpty(elementStore) === true) {
        return;
      }

      if (illuminatiJsAgent.ObjectIsEmpty(newObject) === false
          && newObject.hasOwnProperty('changedInfo') === true
          && elementStore.hasOwnProperty(key) === true) {
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
      }

      illuminatiJsAgent.setSessionStorage(sessionStorageName,
          JSON.stringify(elementStore));
    }
  },

  isValidateAddEventListener: function (element, eventName) {
    if (typeof element === 'undefined' || element === null || Array.isArray(
        eventName) === false || eventName.length === 0) {
      return '';
    }
    return illuminatiJsAgent.getElementUniqueId(element);
  },

  addEventInputText: function (element, eventName, objIndex) {
    var elementUniqueValue = illuminatiJsAgent.isValidateAddEventListener(
        element, eventName);
    if (elementUniqueValue === '') {
      return;
    }
    elementUniqueValue += '_' + String(objIndex);

    try {
      if (isEventListener === true) {
        if (typeof eventName[0] !== 'undefined' && eventName[0] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_input_text_' + elementUniqueValue + '_'
                + eventName[0]) === false) {
          element.addEventListener(eventName[0], function (e) {
            illuminatiJsAgent.addEventInputTextFunction(e);
          });
          element.addEventListener(
              'illuminati_input_text_' + elementUniqueValue + '_'
              + eventName[0], function () {
                return true;
              });
        }
      } else if (isAttachEvent === true) {
        if (typeof eventName[0] !== 'undefined' && eventName[0] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_input_text_' + elementUniqueValue + '_'
                + eventName[0]) === false) {
          element.attachEvent('on' + eventName[0], function (e) {
            illuminatiJsAgent.addEventInputTextFunction(e);
          });
          element.attachEvent(
              'illuminati_input_text_' + elementUniqueValue + '_'
              + eventName[0], true);
        }
      }
    } catch (e) {
      console.debug(e);
    }
  },

  addEventInputTextFunction: function (event) {
    illuminatiJsAgent.screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(
        event);
    var oldObject = illuminatiJsAgent.getEventData(event);
    var newObject = illuminatiJsAgent.getNewEventData(oldObject);
    newObject['screenInfo'] = illuminatiJsAgent.screenInfo;
    illuminatiJsAgent.setElementToSessionStorage(newObject);
  },

  addEventTextarea: function (element, eventName, objIndex) {
    var elementUniqueValue = illuminatiJsAgent.isValidateAddEventListener(
        element, eventName);
    if (elementUniqueValue === '') {
      return;
    }
    elementUniqueValue += '_' + String(objIndex);

    try {
      if (isEventListener === true) {
        if (typeof eventName[0] !== 'undefined' && eventName[0] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_textarea_lastcheck_' + elementUniqueValue + '_'
                + eventName[0]) === false) {
          element.addEventListener(eventName[0], function (e) {
            illuminatiJsAgent.addEventTextareaLastCheckFunction(e);
          });
          element.addEventListener(
              'illuminati_textarea_lastcheck_' + elementUniqueValue + '_'
              + eventName[0], function () {
                return true;
              });
        }
        if (typeof eventName[1] !== 'undefined' && eventName[1] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_textarea_' + elementUniqueValue + '_'
                + eventName[1]) === false) {
          element.addEventListener(eventName[1], function (e) {
            illuminatiJsAgent.addEventTextareaFunction(e);
          });
          element.addEventListener(
              'illuminati_textarea_' + elementUniqueValue + '_' + eventName[1],
              function () {
                return true;
              });
        }
      } else if (isAttachEvent === true) {
        if (typeof eventName[0] !== 'undefined' && eventName[0] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_textarea_lastcheck_' + elementUniqueValue + '_'
                + eventName[0]) === false) {
          element.attachEvent('on' + eventName[0], function (e) {
            illuminatiJsAgent.addEventTextareaLastCheckFunction(e);
          });
          element.attachEvent(
              'illuminati_textarea_lastcheck_' + elementUniqueValue + '_'
              + eventName[0], function () {
                return true;
              });
        }
        if (typeof eventName[1] !== 'undefined' && eventName[1] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_textarea_' + elementUniqueValue + '_'
                + eventName[1]) === false) {
          element.attachEvent('on' + eventName[1], function (e) {
            illuminatiJsAgent.addEventTextareaFunction(e);
          });
          element.attachEvent(
              'illuminati_textarea_' + elementUniqueValue + '_' + eventName[1],
              function () {
                return true;
              });
        }
      }
    } catch (e) {
      console.debug(e);
    }
  },

  addEventTextareaLastCheckFunction: function (event) {
    illuminatiJsAgent.screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(
        event);
    illuminatiJsAgent.lastCheckObject = illuminatiJsAgent.getEventData(event);
  },
  addEventTextareaFunction: function (e) {
    if (typeof illuminatiJsAgent.lastCheckObject === 'undefined') {
      illuminatiJsAgent.addEventTextareaLastCheckFunction(e);
    }

    var newObject = illuminatiJsAgent.getNewEventData(
        illuminatiJsAgent.lastCheckObject);
    delete (illuminatiJsAgent.lastCheckObject);
    newObject['screenInfo'] = illuminatiJsAgent.screenInfo;
    illuminatiJsAgent.setElementToSessionStorage(newObject);
  },

  addEventSelectBox: function (element, eventName, objIndex) {
    var elementUniqueValue = illuminatiJsAgent.isValidateAddEventListener(
        element, eventName);
    if (elementUniqueValue === '') {
      return;
    }
    elementUniqueValue += '_' + String(objIndex);

    try {
      if (isEventListener === true) {
        if (typeof eventName[0] !== 'undefined' && eventName[0] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_selectbox_' + elementUniqueValue + '_'
                + eventName[0]) === false) {
          element.addEventListener(eventName[0], function (e) {
            illuminatiJsAgent.addEventSelectBoxFunction(e);
          });
          element.addEventListener(
              'illuminati_selectbox_' + elementUniqueValue + '_' + eventName[0],
              function () {
                return true;
              });
        }
      } else if (isAttachEvent === true) {
        if (typeof eventName[0] !== 'undefined' && eventName[0] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_selectbox_' + elementUniqueValue + '_'
                + eventName[0]) === false) {
          element.attachEvent('on' + eventName[0], function (e) {
            illuminatiJsAgent.addEventSelectBoxFunction(e);
          });
          element.attachEvent(
              'illuminati_selectbox_' + elementUniqueValue + '_' + eventName[0],
              function () {
                return true;
              });
        }
      }
    } catch (e) {
      console.debug(e);
    }
  },

  addEventSelectBoxFunction: function (event) {
    illuminatiJsAgent.screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(
        event);
    var oldObject = illuminatiJsAgent.getEventData(event);
    var newObject = illuminatiJsAgent.getNewEventData(oldObject);
    newObject['screenInfo'] = illuminatiJsAgent.screenInfo;
    illuminatiJsAgent.setElementToSessionStorage(newObject);
  },

  addEventForm: function (element, eventName, objIndex) {
    var elementUniqueValue = illuminatiJsAgent.isValidateAddEventListener(
        element, eventName) + String(objIndex);

    try {
      if (isEventListener === true) {
        if (typeof eventName[0] !== 'undefined' && eventName[0] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_form_' + elementUniqueValue + '_' + eventName[0])
            === false) {
          element.addEventListener(eventName[0], function (e) {
            illuminatiJsAgent.addEventFormFunction(this, e);
          });
          element.addEventListener(
              'illuminati_form_' + elementUniqueValue + '_' + eventName[0],
              function () {
                return true;
              });
        }
      } else if (isAttachEvent === true) {
        if (typeof eventName[0] !== 'undefined' && eventName[0] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_form_' + elementUniqueValue + '_' + eventName[0])
            === false) {
          element.attachEvent('on' + eventName[0], function (e) {
            illuminatiJsAgent.addEventFormFunction(this, e);
          });
          element.attachEvent(
              'illuminati_form_' + elementUniqueValue + '_' + eventName[0],
              function () {
                return true;
              });
        }
      }
    } catch (e) {
      console.debug(e);
    }
  },

  addEventFormFunction: function (obj, event) {
    if (event.preventDefault) {
      event.preventDefault();
    }

    var sTransactionId = illuminatiJsAgent.getSessionStorage(
        'illuminatiSProcId');
    if (typeof sTransactionId !== 'undefined' && sTransactionId !== null) {
      illuminatiJsAgent.generateHiddenInputElement(this, 'illuminatiSProcId',
          sTransactionId);
    }
    var gTransactionId = illuminatiJsAgent.getSessionStorage(
        'illuminatiGProcId');
    if (typeof gTransactionId !== 'undefined' && gTransactionId !== null) {
      illuminatiJsAgent.generateHiddenInputElement(this, 'illuminatiGProcId',
          gTransactionId);
    }
    var uniqueUserId = illuminatiJsAgent.getSessionStorage(
        'illuminatiUniqueUserId');
    if (typeof uniqueUserId !== 'undefined' && uniqueUserId !== null) {
      illuminatiJsAgent.generateHiddenInputElement(this,
          'illuminatiUniqueUserId', uniqueUserId)
    }

    try {
      illuminatiJsAgent.tempBufferToBuffer();
      illuminatiJsAgent.sendToIlluminati(false);

      obj.submit();
    } catch (e) {
    }
  },

  addEventClick: function (element, eventName, objIndex) {
    var elementUniqueValue = illuminatiJsAgent.isValidateAddEventListener(
        element, eventName);
    if (elementUniqueValue === '') {
      return;
    }
    elementUniqueValue += '_' + String(objIndex);

    try {
      if (isEventListener === true) {
        if (typeof eventName[0] !== 'undefined' && eventName[0] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_' + elementUniqueValue + '_' + eventName[0])
            === false) {
          element.addEventListener(eventName[0], function (e) {
            illuminatiJsAgent.addEventClickLastCheckFunction(e);
          });
          element.addEventListener(
              'illuminati_' + elementUniqueValue + '_' + eventName[0],
              function () {
                return true;
              });
        }
        if (typeof eventName[1] !== 'undefined' && eventName[1] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_' + elementUniqueValue + '_' + eventName[1])
            === false) {
          element.addEventListener(eventName[1], function (e) {
            illuminatiJsAgent.addEventClickFunction(e);
          });
          element.addEventListener(
              'illuminati_' + elementUniqueValue + '_' + eventName[1],
              function () {
                return true;
              });
        }
      } else if (isAttachEvent === true) {
        if (typeof eventName[0] !== 'undefined' && eventName[0] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_' + elementUniqueValue + '_' + eventName[0])
            === false) {
          element.attachEvent('on' + eventName[0], function (e) {
            illuminatiJsAgent.addEventClickLastCheckFunction(e);
          });
          element.attachEvent(
              'illuminati_' + elementUniqueValue + '_' + eventName[0],
              function () {
                return true;
              });
        }
        if (typeof eventName[1] !== 'undefined' && eventName[1] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_' + elementUniqueValue + '_' + eventName[1])
            === false) {
          element.attachEvent('on' + eventName[1], function (e) {
            illuminatiJsAgent.addEventClickFunction(e);
          });
          element.attachEvent(
              'illuminati_' + elementUniqueValue + '_' + eventName[1],
              function () {
                return true;
              });
        }
      }
    } catch (e) {
      console.debug(e);
    }
  },

  addEventClickLastCheckFunction: function (event) {
    illuminatiJsAgent.screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(
        event);
    illuminatiJsAgent.lastCheckObject = illuminatiJsAgent.getEventData(event);
  },
  addEventClickFunction: function (e) {
    if (typeof illuminatiJsAgent.lastCheckObject === 'undefined') {
      illuminatiJsAgent.addEventClickLastCheckFunction(e);
    }

    var newObject = illuminatiJsAgent.getNewEventData(
        illuminatiJsAgent.lastCheckObject);
    delete (illuminatiJsAgent.lastCheckObject);
    newObject['screenInfo'] = illuminatiJsAgent.screenInfo;
    illuminatiJsAgent.setElementToSessionStorage(newObject);
  },

  addEventBaseClick: function (element, eventName, objIndex, index) {
    var elementUniqueValue = illuminatiJsAgent.isValidateAddEventListener(
        element, eventName);
    if (elementUniqueValue === '') {
      return;
    }
    elementUniqueValue += '_' + String(objIndex) + String(index);

    try {
      if (isEventListener === true) {
        if (typeof eventName[0] !== 'undefined' && eventName[0] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_' + elementUniqueValue + '_' + eventName[0])
            === false) {
          element.addEventListener(eventName[0], function (e) {
            illuminatiJsAgent.addEventBaseClickFunction(e);
          });
          element.addEventListener(
              'illuminati_' + elementUniqueValue + '_' + eventName[0],
              function () {
                return true;
              });
        }
      } else if (isAttachEvent === true) {
        if (typeof eventName[0] !== 'undefined' && eventName[0] !== null
            && illuminatiJsAgent.addedListeners.hasOwnProperty(
                'illuminati_' + elementUniqueValue + '_' + eventName[0])
            === false) {
          element.attachEvent('on' + eventName[0], function (e) {
            illuminatiJsAgent.addEventBaseClickFunction(e);
          });
          element.attachEvent(
              'illuminati_' + elementUniqueValue + '_' + eventName[0],
              function () {
                return true;
              });
        }
      }
    } catch (e) {
      console.debug(e);
    }
  },

  addEventBaseClickFunction: function (event) {
    illuminatiJsAgent.screenInfo = illuminatiJsAgent.getScreenInfoAtEvent(
        event);
    var oldObject = illuminatiJsAgent.getEventData(event);
    var newObject = illuminatiJsAgent.getNewEventData(oldObject);
    newObject['screenInfo'] = illuminatiJsAgent.screenInfo;
    illuminatiJsAgent.setElementToSessionStorage(newObject);
  },

  domElementInit: function () {
    if (document.readyState === 'complete') {
      window.clearInterval(interval);

      // document ready
      var elems = document.body.getElementsByTagName("*");

      var tempRadioStore = {};
      var elementStore = {};

      for (var i = 0; i < elems.length; i++) {
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

        if (elem.localName === 'input' && elem.getAttribute('type')
            === 'radio') {
          if (tempRadioStore.hasOwnProperty(
              elementObj.type + '-' + elementUniqueId) === false) {
            tempRadioStore[elementObj.type + '-' + elementUniqueId] = [];
          }

          var radio = {};
          for (var j = 0; j < elem.attributes.length; j++) {
            var item = elem.attributes.item(j);
            radio[item.name] = item.value;
          }

          radio['originElement'] = elem;
          radio['checked'] = elem.checked;
          tempRadioStore[elementObj.type + '-'
          + elementUniqueId][tempRadioStore[elementObj.type + '-'
          + elementUniqueId].length] = radio;
          continue;
        }

        for (var j = 0; j < elem.attributes.length; j++) {
          var item = elem.attributes.item(j);

          if (elem.localName === 'select') {
            elementObj['option'] = [];

            for (var k = 0; k < elem.childElementCount; k++) {
              var option = {};
              for (var m = 0; m < elem[k].attributes.length; m++) {
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
        var objIndex = 0;
        for (var key in elementStore) {
          var eventElem = elementStore[key];
          if (Array.isArray(eventElem) !== true) {
            switch (eventElem.type) {
              case 'text' :
                illuminatiJsAgent.addEventInputText(eventElem['originElement'],
                    ['keyup'], objIndex);
                break;
              case 'textarea' :
                illuminatiJsAgent.addEventTextarea(eventElem['originElement'],
                    ['focusin', 'keyup'], objIndex);
                break;
              case 'select-one' :
                illuminatiJsAgent.addEventSelectBox(eventElem['originElement'],
                    ['change'], objIndex);
                break;

              case 'form' :
                illuminatiJsAgent.addEventForm(eventElem['originElement'],
                    ['submit'], objIndex);
                break;

              default :
                if (eventElem.type !== undefined) {
                  illuminatiJsAgent.addEventClick(eventElem['originElement'],
                      ['mouseup', 'click'], objIndex);
                }
                break;
            }
          } else {
            for (var n = 0; n < eventElem.length; n++) {
              var tmpRadioObj = eventElem[n];
              illuminatiJsAgent.addEventBaseClick(tmpRadioObj['originElement'],
                  ['click'], objIndex, n);
            }
          }

          objIndex++;
        }
      }

      for (var key in elementStore) {
        delete elementStore[key]['originElement'];
      }

      illuminatiJsAgent.setSessionStorage('illuminati',
          JSON.stringify(elementStore));

      if (illuminatiJsAgent.isFirstInitDomElement === true) {
        illuminatiJsAgent.isFirstInitDomElement = false;
        window.setTimeout(function () {
          illuminatiJsAgent.domElementInit();
        }, 3000);
      }
    }
  },

  sendToIlluminati: function (isAsync) {
    if (illuminatiJsAgent.illuminatiSendStatus === 'done') {
      var elementStore = JSON.parse(
          illuminatiJsAgent.getSessionStorage('illuminati-buffer'));
      if (typeof elementStore === 'undefined' || elementStore === null) {
        return;
      }

      illuminatiJsAgent.illuminatiSendStatus = 'sending';

      var illuminatiJsModel = {
        illuminatiGProcId: illuminatiJsAgent.getSessionStorage(
            'illuminatiGProcId'),
        illuminatiSProcId: illuminatiJsAgent.getSessionStorage(
            'illuminatiSProcId'),
        illuminatiUniqueUserId: illuminatiJsAgent.getSessionStorage(
            'illuminatiUniqueUserId')
      };

      var ignoreCheckEventStoreKeys = ['alreadySent'];
      Object.keys(elementStore).map(function (objectKey, index) {
        if (Array.prototype.inArrayCheck(objectKey, ignoreCheckEventStoreKeys)
            === false) {
          if (illuminatiJsAgent.ObjectIsEmpty(elementStore[objectKey]) === false
              && elementStore[objectKey].hasOwnProperty('changedInfo')
              === true) {
            var changedObj = elementStore[objectKey];

            if (illuminatiJsModel.hasOwnProperty('changedValues') === true) {
              for (var l = 0;
                  l < changedObj['changedInfo']['changedValues'].length; l++) {
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
          illuminatiAjax.sendByPost(illuminatiJsAgent.collectorUrl, isAsync,
              illuminatiJsModel);
        } catch (e) {
          console.debug(e);
        }

        illuminatiJsAgent.illuminatiSendStatus = 'done';
        if (illuminatiJsAgent.tempBufferToBuffer() === false) {
          illuminatiJsAgent.removeSessionStorage('illuminati-buffer');
        }
      }
    }
  }
};

var illuminatiXhr;
var illuminatiAjax = {
  xmlHttpObjType: null,
  timeoutMs: 5000,

  init: function () {
    if (typeof XMLHttpRequest !== 'undefined') {
      illuminatiXhr = new XMLHttpRequest();
    } else {
      var versions = ['MSXML2.XmlHttp.5.0',
        'MSXML2.XmlHttp.4.0',
        'MSXML2.XmlHttp.3.0',
        'MSXML2.XmlHttp.2.0',
        'Microsoft.XmlHttp']

      for (var i = 0, len = versions.length; i < len; i++) {
        try {
          illuminatiXhr = new ActiveXObject(versions[i]);
          break;
        } catch (e) {
        }
      } // end for
    }
  },

  setRequestHeaderOnAjaxEvent: function (illuminatiXhr) {
    var illuminatiGProcId = illuminatiJsAgent.getSessionStorage(
        'illuminatiGProcId');
    if (typeof illuminatiGProcId !== 'undefined' && illuminatiGProcId
        !== null) {
      illuminatiXhr.setRequestHeader('illuminatiGProcId', illuminatiGProcId);
    }
    var illuminatiSProcId = illuminatiJsAgent.getSessionStorage(
        'illuminatiSProcId');
    if (typeof illuminatiSProcId !== 'undefined' && illuminatiSProcId
        !== null) {
      illuminatiXhr.setRequestHeader('illuminatiSProcId', illuminatiSProcId);
    }
    var illuminatiUniqueUserId = illuminatiJsAgent.getSessionStorage(
        'illuminatiUniqueUserId');
    if (typeof illuminatiUniqueUserId !== 'undefined' && illuminatiUniqueUserId
        !== null) {
      illuminatiXhr.setRequestHeader('illuminatiUniqueUserId',
          illuminatiUniqueUserId);
    }
  },

  sendByPost: function (requestUrl, isAsync, data) {
    // todo : IE 6, 7 does not support cross-domain request. because of send POST method using hidden iframe.
    //var msieVersion = ['msie 6.0', 'msie 7.0'];

    // if (this.checkIEBrowser(msieVersion) === true) {
    //     // not yet
    // } else {
    illuminatiXhr.open('POST', requestUrl, isAsync);

    // if (isAsync === false) {
    //     illuminatiXhr.onreadystatechange = this.handleStateChange();
    // }

    illuminatiXhr.setRequestHeader('Content-Type',
        'application/json; charset=UTF-8');
    // set header
    //illuminatiAjax.setRequestHeaderOnAjaxEvent(illuminatiXhr);
    //xmlHttp.withCredentials = true;
    isIlluminatiRequest = true;
    illuminatiXhr.send(JSON.stringify(data));

    setTimeout(function () {
      isIlluminatiRequest = false;
      illuminatiXhr.abort();
    }, this.timeoutMs);

    isIlluminatiRequest = false;
    return true;
  }
};

var isEventListener = false;
var isAttachEvent = false;
var isIlluminatiRequest = false;

if (document.addEventListener) {
  isEventListener = true;
} else if (document.attachEvent) {
  isAttachEvent = true;
}

if (isEventListener === true || isAttachEvent === true) {
  var interval = window.setInterval(function () {
    illuminatiJsAgent.domElementInit();
  }, 100);

  illuminatiAjax.init();
}