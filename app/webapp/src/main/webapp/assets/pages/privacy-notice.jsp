<%--
  ~ Copyright 2009-2018 European Molecular Biology Laboratory
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<style>
    #button {
        align-items: flex-start;
        background-color: rgb(32, 122, 122);
        background-image: -webkit-linear-gradient(top, rgb(18, 64, 171), rgb(42, 68, 128));
        border-bottom-color: rgb(6, 38, 111);
        border-bottom-left-radius: 5px;
        border-bottom-right-radius: 5px;
        border-bottom-style: solid;
        border-bottom-width: 1px;
        border-image-outset: 0px;
        border-image-repeat: stretch;
        border-image-slice: 100%;
        border-image-source: none;
        border-image-width: 1;
        border-left-color: rgb(108, 140, 213);
        border-left-style: solid;
        border-left-width: 1px;
        border-right-color: rgb(6, 38, 111);
        border-right-style: solid;
        border-right-width: 1px;
        border-top-color: rgb(108, 140, 213);
        border-top-left-radius: 5px;
        border-top-right-radius: 5px;
        border-top-style: solid;
        border-top-width: 1px;
        box-shadow: rgb(173, 173, 173) 0px 2px 2px 0px;
        box-sizing: border-box;
        color: rgb(255, 255, 255);
        cursor: pointer;
        display: inline-block;
        filter: none;
        font-family: "Helvetica Neue", Helvetica, sans-serif;
        font-size: 14.04px;
        font-stretch: 100%;
        font-style: normal;
        font-variant-caps: normal;
        font-variant-east-asian: normal;
        font-variant-ligatures: normal;
        font-variant-numeric: normal;
        font-weight: 400;
        height: 24px;
        letter-spacing: normal;
        line-height: normal;
        margin-bottom: 7.02px;
        margin-left: 0px;
        margin-right: 0px;
        margin-top: 7.02px;
        padding-bottom: 3px;
        padding-left: 9px;
        padding-right: 9px;
        padding-top: 3px;
        text-align: left;
        text-decoration-color: rgb(255, 255, 255);
        text-decoration-line: none;
        text-decoration-style: solid;
        text-indent: 0px;
        text-rendering: auto;
        text-shadow: rgb(6, 38, 111) 1px 1px 0px;
        text-size-adjust: 100%;
        text-transform: none;
        user-select: none;
        vertical-align: middle;
        white-space: pre;
        width: 64.4688px;
        word-spacing: 0px;
        writing-mode: horizontal-tb;
        -webkit-appearance: none;
        -webkit-rtl-ordering: logical;
        -webkit-border-image: none;
    }
    #button a {
        display: block;
        width: 100%;
        line-height: 2em;
        text-align: center;
        text-decoration: none;
        border-radius: 5px;
    }
    #button:hover {
        color: #eff;
        background: #207a7a;
    }
</style>
<head>
    <title>PRIVACY NOTICE</title>
</head>
<form name="privacyNotice" method="POST">
    <div>
        <p>This service requires limited processing of your personal data. By using the service you are agreeing to this as outlined in our <a href="https://www.ebi.ac.uk/data-protection/privacy-notice/annotare" target="_blank">Privacy Notice</a> and <a href="https://www.ebi.ac.uk/about/terms-of-use" target="_blank">
            Terms of Use</a>.</p>
    </div>
    <div>
        <div style="float: right;">
            <a id="button" href="${pageContext.request.contextPath}/login/">Decline</a>
            <input type="submit" name="accept" value="Accept" class="submit"/>
        </div>
    </div>
</form>
</html>
