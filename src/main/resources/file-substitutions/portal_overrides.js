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

var path = 'learning-record/' + courseId + '/' + moduleId + '/xapi';
console.log('path = learning-record/ + courseId + / + moduleId + /xapi: ' + path);

console.log('endpoint = scheme + // + host + path: ' + scheme + '//' + host + path);

alert('portal_overrides.js');

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
            endpoint: scheme + '//' + host + path
        }
    ]
}
