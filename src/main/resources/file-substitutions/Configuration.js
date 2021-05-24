// Substituted by lpg-learning-catalogue

// HMRC support

if (typeof XMLHttpRequest !== 'undefined') {
    window._xhr = XMLHttpRequest;
    window.XMLHttpRequest = function () {
        var x = new window._xhr();
        x.withCredentials = true;
        return x;
    }
}


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

// Polyfill to add URL support to IE11
(function(t){var e=function(){try{return!!Symbol.iterator}catch(e){return false}};var r=e();var n=function(t){var e={next:function(){var e=t.shift();return{done:e===void 0,value:e}}};if(r){e[Symbol.iterator]=function(){return e}}return e};var i=function(e){return encodeURIComponent(e).replace(/%20/g,"+")};var o=function(e){return decodeURIComponent(String(e).replace(/\+/g," "))};var a=function(){var a=function(e){Object.defineProperty(this,"_entries",{writable:true,value:{}});var t=typeof e;if(t==="undefined"){}else if(t==="string"){if(e!==""){this._fromString(e)}}else if(e instanceof a){var r=this;e.forEach(function(e,t){r.append(t,e)})}else if(e!==null&&t==="object"){if(Object.prototype.toString.call(e)==="[object Array]"){for(var n=0;n<e.length;n++){var i=e[n];if(Object.prototype.toString.call(i)==="[object Array]"||i.length!==2){this.append(i[0],i[1])}else{throw new TypeError("Expected [string, any] as entry at index "+n+" of URLSearchParams's input")}}}else{for(var o in e){if(e.hasOwnProperty(o)){this.append(o,e[o])}}}}else{throw new TypeError("Unsupported input's type for URLSearchParams")}};var e=a.prototype;e.append=function(e,t){if(e in this._entries){this._entries[e].push(String(t))}else{this._entries[e]=[String(t)]}};e.delete=function(e){delete this._entries[e]};e.get=function(e){return e in this._entries?this._entries[e][0]:null};e.getAll=function(e){return e in this._entries?this._entries[e].slice(0):[]};e.has=function(e){return e in this._entries};e.set=function(e,t){this._entries[e]=[String(t)]};e.forEach=function(e,t){var r;for(var n in this._entries){if(this._entries.hasOwnProperty(n)){r=this._entries[n];for(var i=0;i<r.length;i++){e.call(t,r[i],n,this)}}}};e.keys=function(){var r=[];this.forEach(function(e,t){r.push(t)});return n(r)};e.values=function(){var t=[];this.forEach(function(e){t.push(e)});return n(t)};e.entries=function(){var r=[];this.forEach(function(e,t){r.push([t,e])});return n(r)};if(r){e[Symbol.iterator]=e.entries}e.toString=function(){var r=[];this.forEach(function(e,t){r.push(i(t)+"="+i(e))});return r.join("&")};t.URLSearchParams=a};var s=function(){try{var e=t.URLSearchParams;return new e("?a=1").toString()==="a=1"&&typeof e.prototype.set==="function"&&typeof e.prototype.entries==="function"}catch(e){return false}};if(!s()){a()}var f=t.URLSearchParams.prototype;if(typeof f.sort!=="function"){f.sort=function(){var r=this;var n=[];this.forEach(function(e,t){n.push([t,e]);if(!r._entries){r.delete(t)}});n.sort(function(e,t){if(e[0]<t[0]){return-1}else if(e[0]>t[0]){return+1}else{return 0}});if(r._entries){r._entries={}}for(var e=0;e<n.length;e++){this.append(n[e][0],n[e][1])}}}if(typeof f._fromString!=="function"){Object.defineProperty(f,"_fromString",{enumerable:false,configurable:false,writable:false,value:function(e){if(this._entries){this._entries={}}else{var r=[];this.forEach(function(e,t){r.push(t)});for(var t=0;t<r.length;t++){this.delete(r[t])}}e=e.replace(/^\?/,"");var n=e.split("&");var i;for(var t=0;t<n.length;t++){i=n[t].split("=");this.append(o(i[0]),i.length>1?o(i[1]):"")}}})}})(typeof global!=="undefined"?global:typeof window!=="undefined"?window:typeof self!=="undefined"?self:this);(function(u){var e=function(){try{var e=new u.URL("b","http://a");e.pathname="c d";return e.href==="http://a/c%20d"&&e.searchParams}catch(e){return false}};var t=function(){var t=u.URL;var e=function(e,t){if(typeof e!=="string")e=String(e);if(t&&typeof t!=="string")t=String(t);var r=document,n;if(t&&(u.location===void 0||t!==u.location.href)){t=t.toLowerCase();r=document.implementation.createHTMLDocument("");n=r.createElement("base");n.href=t;r.head.appendChild(n);try{if(n.href.indexOf(t)!==0)throw new Error(n.href)}catch(e){throw new Error("URL unable to set base "+t+" due to "+e)}}var i=r.createElement("a");i.href=e;if(n){r.body.appendChild(i);i.href=i.href}var o=r.createElement("input");o.type="url";o.value=e;if(i.protocol===":"||!/:/.test(i.href)||!o.checkValidity()&&!t){throw new TypeError("Invalid URL")}Object.defineProperty(this,"_anchorElement",{value:i});var a=new u.URLSearchParams(this.search);var s=true;var f=true;var c=this;["append","delete","set"].forEach(function(e){var t=a[e];a[e]=function(){t.apply(a,arguments);if(s){f=false;c.search=a.toString();f=true}}});Object.defineProperty(this,"searchParams",{value:a,enumerable:true});var h=void 0;Object.defineProperty(this,"_updateSearchParams",{enumerable:false,configurable:false,writable:false,value:function(){if(this.search!==h){h=this.search;if(f){s=false;this.searchParams._fromString(this.search);s=true}}}})};var r=e.prototype;var n=function(t){Object.defineProperty(r,t,{get:function(){return this._anchorElement[t]},set:function(e){this._anchorElement[t]=e},enumerable:true})};["hash","host","hostname","port","protocol"].forEach(function(e){n(e)});Object.defineProperty(r,"search",{get:function(){return this._anchorElement["search"]},set:function(e){this._anchorElement["search"]=e;this._updateSearchParams()},enumerable:true});Object.defineProperties(r,{toString:{get:function(){var e=this;return function(){return e.href}}},href:{get:function(){return this._anchorElement.href.replace(/\?$/,"")},set:function(e){this._anchorElement.href=e;this._updateSearchParams()},enumerable:true},pathname:{get:function(){return this._anchorElement.pathname.replace(/(^\/?)/,"/")},set:function(e){this._anchorElement.pathname=e},enumerable:true},origin:{get:function(){var e={"http:":80,"https:":443,"ftp:":21}[this._anchorElement.protocol];var t=this._anchorElement.port!=e&&this._anchorElement.port!=="";return this._anchorElement.protocol+"//"+this._anchorElement.hostname+(t?":"+this._anchorElement.port:"")},enumerable:true},password:{get:function(){return""},set:function(e){},enumerable:true},username:{get:function(){return""},set:function(e){},enumerable:true}});e.createObjectURL=function(e){return t.createObjectURL.apply(t,arguments)};e.revokeObjectURL=function(e){return t.revokeObjectURL.apply(t,arguments)};u.URL=e};if(!e()){t()}if(u.location!==void 0&&!("origin"in u.location)){var r=function(){return u.location.protocol+"//"+u.location.hostname+(u.location.port?":"+u.location.port:"")};try{Object.defineProperty(u.location,"origin",{get:r,enumerable:true})}catch(e){setInterval(function(){u.location.origin=r()},100)}}})(typeof global!=="undefined"?global:typeof window!=="undefined"?window:typeof self!=="undefined"?self:this);
// Use the polyfill above to create the URL object

var url = new URL(window.location.toString());
// can we find the required host in the url? If not, error
if (url.hostname.indexOf("learn.civilservice.gov.uk") === -1) {
    console.log('Content being accessed on invalid domain');
    throw new Error('Content being accessed on invalid domain');
}

// Get the course ID from the path
var urlPathParts = url.pathname.split("/");
var courseId = urlPathParts[2];
// Get the moduleId from the query params
var moduleId = url.searchParams.get("module");
// split the host and take off the cdn part
var splitHost = url.host.split(".");
splitHost.shift();
var host = splitHost.join(".");
var scheme = window.location.protocol;
var path = '/learning-record/' + courseId + '/' + moduleId;

//Configuration Parameters
var blnDebug = false;						//set this to false if you don't want the overhead of recording debug information

var strLMSStandard = "TCAPI";				//used in versions that support multiple standards, set to "NONE" to default
//to StandAlone mode. Possible values = "NONE", "SCORM", "AICC", ""SCORM2004", "AUTO"
//AUTO mode will automatically determine the best standard to use (it first tries SCORM 2004, then SCORM 1.2/1.1 then AICC, then NONE)

var DEFAULT_EXIT_TYPE = EXIT_TYPE_SUSPEND;	//When the content is unloaded without an API function indicating the type of exit,
//what default behavior do you want to assume.  Use EXIT_TYPE_SUSPEND if you plan to
//call Finish when the content is complete.  Use EXIT_TYPE_FINISH if you do not plan
//to call Finish.


var AICC_LESSON_ID = "1";					//if recording question answers in AICC in an LMS that supports interactions,
//this field need to match the system_id on line in the .DES file that describes
//this course, the default is 1. Be sure that this value does not contain double quote characters (")

var EXIT_BEHAVIOR = "REDIR_CONTENT_FRAME";		//used to control window closing behavior on call of ConcedeControl
//Possible Values: SCORM_RECOMMENDED, ALWAYS_CLOSE, ALWAYS_CLOSE_TOP, NOTHING, REDIR_CONTENT_FRAME, LMS_SPECIFIED_REDIRECT

var EXIT_TARGET = scheme + '//' + host + path;			//Used in conjunction with EXIT_BEHAVIOR, only with REDIR_CONTENT_FRAME. This should be a neutral page that is displayed
//after the course has exited, but before it has been taked away by the LMS

var LMS_SPECIFIED_REDIRECT_EVAL_STATEMENT = "";	//JS to be eval'ed during exit ONLY with EXIT_BEHAVIOR of LMS_SPECIFIED_REDIRECT

var AICC_COMM_DISABLE_XMLHTTP = false;		//false is the preferred value, true can be required in certain cross domain situations
var AICC_COMM_DISABLE_IFRAME = false;		//false is the preferred value, true can be required in certain cross domain situations

var AICC_COMM_PREPEND_HTTP_IF_MISSING = true;		//Some AICC LMS's will omit the "http://" from the AICC_URL value. If this is the case,
//set this setting to true to have the API prepend the "http://" value

var AICC_REPORT_MIN_MAX_SCORE = false;		//Some AICC LMS's have trouble processing a score which contains a min and max value. Setting this
//value to false allows you to turn off that reporting to accommodate those LMS's.


var SHOW_DEBUG_ON_LAUNCH = false;		//set this to true when debugging to force the debug window to launch immediately

var DO_NOT_REPORT_INTERACTIONS = false;		//set this to true to disable reporting of question results to the LMS as some LMS's
//particularly those supporting only AICC can have problems with interactions results


var SCORE_CAN_ONLY_IMPROVE = false;			//set this to true to ensure that on subsequent attempts, a learner's score can only go up

var REVIEW_MODE_IS_READ_ONLY = false;		//set this to true if no new data should be saved when a course is launched in review mode (normally this is the LMS's responsibility)


/*
These variables control how long the API should wait on an AICC form submission before timing out
AICC_RE_CHECK_LOADED_INTERVAL = Number of milliseconds the API waits between checks to see if the form is loaded
AICC_RE_CHECK_ATTEMPTS_BEFORE_TIMEOUT = Number of times the API checks to see if the form is loaded
AICC_RE_CHECK_LOADED_INTERVAL * AICC_RE_CHECK_ATTEMPTS_BEFORE_TIMEOUT = Desired time out in milliseconds
*/
var AICC_RE_CHECK_LOADED_INTERVAL = 250;
var AICC_RE_CHECK_ATTEMPTS_BEFORE_TIMEOUT = 240;

var USE_AICC_KILL_TIME = true;				//set this to false to disable the explicit extra waiting between AICC requests

//This controls the entry default when it is not set by the LMS
//Possible options are
//ENTRY_REVIEW (normal default) - ENTRY_FIRST_TIME - ENTRY_RESUME
var AICC_ENTRY_FLAG_DEFAULT = ENTRY_REVIEW;


var FORCED_COMMIT_TIME = "5000"; //Used to force CommitData back to the LMS at the desired interval (in milliseconds). Set to 0 (zero) to not force a commit time.
