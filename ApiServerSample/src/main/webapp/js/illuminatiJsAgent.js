var _send = XMLHttpRequest.prototype.send;

XMLHttpRequest.prototype.send = function() {

    /* Wrap onreadystaechange callback */
    var callback = this.onreadystatechange;
    this.onreadystatechange = function() {
        if (this.readyState == 4) {

            /* We are in response; do something, like logging or anything you want */

        }

        callback.apply(this, arguments);
    }

    _send.apply(this, arguments);
}