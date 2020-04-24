// Substituted by lpg-learning-catalogue

// Storyline support

if (typeof XMLHttpRequest !== 'undefined') {
  window._xhr = XMLHttpRequest;
  window.XMLHttpRequest = function () {
    var x = new window._xhr();
    x.withCredentials = true;
    return x;
  }
}


var CLOSE_METHODS = {

  csl: function() {
      console.log('user.js: csl close');

      var url = window.location.toString();
      var prodMatch = url.match(/(https?):\/\/([^-]*)-?cdn\.learn\.civilservice\.gov\.uk\/[^/]+\/([^/]+)\/([^/]+)\/.*$/);
      var nonProdMatch = url.match(/(https?):\/\/([^-]*)-?cdn\.cshr\.digital\/[^/]+\/([^/]+)\/([^/]+)\/.*$/);

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
          console.log('nonProdMatch[3]: ' + nonProdMatch[3]);
          courseId = nonProdMatch[3];
          console.log('nonProdMatch[2]: ' + nonProdMatch[2]);
          if (nonProdMatch[2] === 'local') {
              host = 'lpg.local.cshr.digital:3001/';
          } else {
              host = nonProdMatch[2] + '-lpg.cshr.digital/';
          }
      }

      var scheme = window.location.protocol;
      var moduleId = getParameterByName('module');
      var path = 'learning-record/' + courseId + '/' + moduleId;

      window.location = scheme + '//' + host + path;

      console.log('courseId: ' + courseId);
      console.log('host: ' + host);
      console.log('scheme: ' + scheme);
      console.log('moduleId: ' + courseId);
      console.log('path: ' + path);
      console.log('window.location = scheme + // + host + path: ' + scheme + '//' + host + path);
      console.log('window.location: ' + window.location);
      alert('user.js');

      return true;
  }
};

top.window.close = CLOSE_METHODS['csl'];

function s() {
  var pubSub;
  try {
    if (window.require && !!(pubSub = require('helpers/pubSub'))) {
      pubSub.on('player:closing', CLOSE_METHODS['csl']);
      return;
    }
  } catch(e) {
  }
  setTimeout(s, 1);
}
s();


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
function r(f){/in/.test(document.readyState)?setTimeout('r('+f+')',9):f()}
r(function(){
  var title = getParameterByName('title');
  document.title = title;
});


if (false) {
  // Code that enables debug output from TinCan lib.
  function d() {
    if (window.TinCan && window.TinCan.enableDebug) {
      window.TinCan.enableDebug();
    } else {
      setTimeout(d, 1);
    }
  }
  d();
}

