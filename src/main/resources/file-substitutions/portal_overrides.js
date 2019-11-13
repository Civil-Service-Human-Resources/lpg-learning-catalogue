// Substituted by lpg-learning-catalogue

if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-accessible"] && OBJ_NAV_BUTTONS["extra-accessible"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-accessible"].booDefaultDisplayButton = false;
if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-credits"] && OBJ_NAV_BUTTONS["extra-credits"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-credits"].booDefaultDisplayButton = false;
if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-resources"] && OBJ_NAV_BUTTONS["extra-resources"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-resources"].booDefaultDisplayButton = false;
if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-language-selector"] && OBJ_NAV_BUTTONS["extra-language-selector"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-language-selector"].booDefaultDisplayButton = false;
if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-settings"] && OBJ_NAV_BUTTONS["extra-settings"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-settings"].booDefaultDisplayButton = false;
if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-search"] && OBJ_NAV_BUTTONS["extra-search"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-search"].booDefaultDisplayButton = false;
if (OBJ_NAV_BUTTONS && OBJ_NAV_BUTTONS["extra-jlr-menu"] && OBJ_NAV_BUTTONS["extra-jlr-menu"].booDefaultDisplayButton) OBJ_NAV_BUTTONS["extra-jlr-menu"].booDefaultDisplayButton = false;

var url = window.location.toString();
var env = !!url[2] ? url[2] + '-' : '';

var match;
var host;
if (env === '') {
    match = url.match(/(https?):\/\/([^-]*)-?cdn\.learn\.civilservice\.gov\.uk\/[^/]+\/([^/]+)\/([^/]+)\/.*$/);
    host = env + 'cdn.' + 'cshr.digital/';
} else {
    match = url.match(/(https?):\/\/([^-]*)-?cdn\.cshr\.digital\/[^/]+\/([^/]+)\/([^/]+)\/.*$/);
    host = env + 'learn.' + 'civilservice.gov.uk/';
}

if (!match) {
    throw new Error('Content being accessed on invalid domain');
}
var moduleId = getParameterByName('module');

var scheme = match[1];
var path = 'learning-record/' + match[3] + '/' + moduleId + '/xapi';

if (match[2] === 'local') {
    scheme = 'http';
    host = 'lpg.local.cshr.digital:3001/';
}
console.log('Matt portal: url: ' + url + "env: " + env + "path: " + path + "host: " + host);
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
