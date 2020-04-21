// Substituted by lpg-learning-catalogue

if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-accessible"] && OBJ_NAV_BUTTONS["extra-accessible"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-accessible"].booDefaultDisplayButton = false;
if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-credits"] && OBJ_NAV_BUTTONS["extra-credits"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-credits"].booDefaultDisplayButton = false;
if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-resources"] && OBJ_NAV_BUTTONS["extra-resources"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-resources"].booDefaultDisplayButton = false;
if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-language-selector"] && OBJ_NAV_BUTTONS["extra-language-selector"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-language-selector"].booDefaultDisplayButton = false;
if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-settings"] && OBJ_NAV_BUTTONS["extra-settings"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-settings"].booDefaultDisplayButton = false;
if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-search"] && OBJ_NAV_BUTTONS["extra-search"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-search"].booDefaultDisplayButton = false;
if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-jlr-menu"] && OBJ_NAV_BUTTONS["extra-jlr-menu"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-jlr-menu"].booDefaultDisplayButton = false;

console.log('portal_overrides.js');
var url = window.location.toString();
console.log('url: ' + url);
console.log('url[2]: ' + url[2]);
var env = !!url[2] ? url[2] + '-' : '';
console.log('env: ' + env);

var match;
var host;
if (env === '') {
    match = url.match(/(https?):\/\/([^-]*)-?cdn\.learn\.civilservice\.gov\.uk\/[^/]+\/([^/]+)\/([^/]+)\/.*$/);
    console.log('match:1: ' + match);
    host = 'learn.civilservice.gov.uk/';
    console.log('host:1: ' + host);
} else {
    match = url.match(/(https?):\/\/([^-]*)-?cdn\.cshr\.digital\/[^/]+\/([^/]+)\/([^/]+)\/.*$/);
    console.log('match:2: ' + match);
    host = 'staging-lpg.cshr.digital/';
    console.log('host:2: ' + host);
}

var moduleId = getParameterByName('module');
console.log('moduleId: ' + moduleId);
var scheme = match[1];
console.log('scheme:1: ' + scheme);
var path = 'learning-record/' + match[3] + '/' + moduleId + '/xapi';
console.log('path: ' + path);
console.log('match[2]: ' + match[2]);

if (match[2] === 'local') {
    scheme = 'http';
    console.log('scheme:2: ' + scheme);
    host = 'lpg.local.cshr.digital:3001/';
    console.log('host:3: ' + host);
}

var scheme_1 = window.location.protocol;
console.log('scheme_1: ' + scheme_1);
var host_1 = window.location.host;
console.log('host_1: ' + host_1);
var path_1 = window.location.pathname;
console.log('path_1: ' + path_1);

var matchLength = url.length - 1;
console.log('matchLength: ' + matchLength);
for (var i = 0; i <= 10; i++){
  console.log( "The value of element match[" + i + "] is: " + match[i]);
}
var urlLength = url.length - 1;
console.log('urlLength: ' + urlLength);
for (var i = 0; i <= 50; i++){
  console.log( "The value of element url[" + i + "] is: " + url[i]);
}

BOO_INCLUDE_EXIT_ON_NAV = false;
BOO_INCLUDE_ACCESSIBLE_ON_NAV = false;
CLOSE_METHOD = 'csl';

var CONTENT_TRACKING_CONFIG = {
    enabled: true,
    pollingInterval: 60000,
    stores: [
        {
            adapter: 'tincan',
            version: '1.0',
            endpoint: scheme + '://' + host + path
        }
    ]
}
