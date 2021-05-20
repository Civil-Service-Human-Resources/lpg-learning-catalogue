// Substituted by lpg-learning-catalogue

var CLOSE_METHODS = {

    csl: function () {
            console.log('close_methods.js: csl close');

            var url = window.location.toString();
            var prodMatch = url.match(/(https?):\/\/([^-]*)-?cdn\.learn\.civilservice\.gov\.uk\/[^/]+\/([^/]+)\/([^/]+)\/.*$/);
            //var nonProdMatch = url.match(/(https?):\/\/([^-]*)-?cdn\.cshr\.digital\/[^/]+\/([^/]+)\/([^/]+)\/.*$/);
            var nonProdMatch = url.match(/(https?):\/\/([^-]*)-?cdn\.*\.learn\.civilservice\.gov\.uk\/[^/]+\/([^/]+)\/([^/]+)\/.*$/);

            console.log('url: ' + url);
            console.log('prodMatch: ' + prodMatch);
            console.log('nonProdMatch: ' + nonProdMatch);

            if (!prodMatch && !nonProdMatch) {
                console.log('Content being accessed on invalid domain');
                throw new Error('Content being accessed on invalid domain');
            }

            var courseId;
            var host;
            if (prodMatch) {
                console.log('prodMatch[3]: ' + prodMatch[3]);
                courseId = prodMatch[3];
                host = 'learn.civilservice.gov.uk/';
            } else {
                console.log('nonProdMatch[1]: ' + nonProdMatch[1]);
                console.log('nonProdMatch[2]: ' + nonProdMatch[2]);
                console.log('nonProdMatch[3]: ' + nonProdMatch[3]);
                console.log('nonProdMatch[4]: ' + nonProdMatch[4]);
                courseId = nonProdMatch[3];
                console.log('courseId = nonProdMatch[3]: ' + courseId);
                if (nonProdMatch[2] === 'local') {
                    host = 'lpg.local.cshr.digital:3001/';
                } else {
                    host = nonProdMatch[2] + '.learn.civilservice.gov.uk/';
                    console.log('host = nonProdMatch[2] + .learn.civilservice.gov.uk: ' + host);
                }
            }
            console.log('host: ' + host);

            var scheme = window.location.protocol;
            console.log('scheme: ' + scheme);

            var moduleId = getParameterByName('module');
            console.log('moduleId: ' + courseId);

            var path = 'learning-record/' + courseId + '/' + moduleId;
            console.log('path = learning-record/ + courseId + / + moduleId: ' + path);

            window.location = scheme + '//' + host + path;
            console.log('window.location = scheme + // + host + path: ' + scheme + '//' + host + path);
            console.log('window.location: ' + window.location);

            alert('close_methods.js');

            return true;
    	}
};


function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}


// Native js method to run on document ready thats crossbrowser and also will not override any existing on ready code
function r(f) {
    /in/.test(document.readyState) ? setTimeout('r(' + f + ')', 9) : f()
}

r(function () {
    var title = getParameterByName('title');
    document.title = title;
});
