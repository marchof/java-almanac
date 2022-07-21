define("ace/theme/almanac",["require","exports","module","ace/lib/dom"], function(require, exports, module) {

exports.isDark = false;
exports.cssClass = "ace-almanac";
exports.cssText = "\
.ace-almanac .ace_gutter {\
background: #fff;\
color: #AAA;\
}\
.ace-almanac  {\
background: #fff;\
color: #333;\
}\
.ace-almanac .ace_keyword {\
color: #589;\
}\
.ace-almanac .ace_string {\
color: #666;\
}\
.ace-almanac .ace_variable.ace_class {\
color: teal;\
}\
.ace-almanac .ace_constant.ace_numeric {\
color: #666;\
}\
.ace-almanac .ace_constant.ace_buildin {\
color: #0086B3;\
}\
.ace-almanac .ace_comment {\
color: #999;\
font-style: italic;\
}\
.ace-almanac .ace_variable.ace_language  {\
color: #0086B3;\
}\
.ace-almanac .ace_paren {\
font-weight: bold;\
}\
.ace-almanac .ace_boolean {\
font-weight: bold;\
}\
.ace-almanac .ace_string.ace_regexp {\
color: #009926;\
font-weight: normal;\
}\
.ace-almanac .ace_variable.ace_instance {\
color: teal;\
}\
.ace-almanac .ace_constant.ace_language {\
font-weight: bold;\
}\
.ace-almanac .ace_cursor {\
color: black;\
}\
.ace-almanac.ace_focus .ace_marker-layer .ace_active-line {\
background: rgb(255, 255, 204);\
}\
.ace-almanac .ace_marker-layer .ace_active-line {\
background: rgb(245, 245, 245);\
}\
.ace-almanac .ace_marker-layer .ace_selection {\
background: rgb(181, 213, 255);\
}\
.ace-almanac.ace_multiselect .ace_selection.ace_start {\
box-shadow: 0 0 3px 0px white;\
}\
.ace-almanac.ace_nobold .ace_line > span {\
font-weight: normal !important;\
}\
.ace-almanac .ace_marker-layer .ace_step {\
background: rgb(252, 255, 0);\
}\
.ace-almanac .ace_marker-layer .ace_stack {\
background: rgb(164, 229, 101);\
}\
.ace-almanac .ace_marker-layer .ace_bracket {\
margin: -1px 0 0 -1px;\
border: 1px solid rgb(192, 192, 192);\
}\
.ace-almanac .ace_gutter-active-line {\
background-color : rgba(0, 0, 0, 0.07);\
}\
.ace-almanac .ace_gutter-cell {\
padding-left: 9px;\
}\
.ace-almanac .ace_marker-layer .ace_selected-word {\
background: rgb(250, 250, 255);\
border: 1px solid rgb(200, 200, 250);\
}\
.ace-almanac .ace_invisible {\
color: #BFBFBF\
}\
.ace-almanac .ace_print-margin {\
width: 1px;\
background: #e8e8e8;\
}\
.ace-almanac .ace_indent-guide {\
background: url(\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAACCAYAAACZgbYnAAAAE0lEQVQImWP4////f4bLly//BwAmVgd1/w11/gAAAABJRU5ErkJggg==\") right repeat-y;\
}";

    var dom = require("../lib/dom");
    dom.importCssString(exports.cssText, exports.cssClass);
});                (function() {
                    window.require(["ace/theme/almanac"], function(m) {
                        if (typeof module == "object" && typeof exports == "object" && module) {
                            module.exports = m;
                        }
                    });
                })();
            