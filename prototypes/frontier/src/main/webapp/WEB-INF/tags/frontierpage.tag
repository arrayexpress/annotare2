<%@tag description="Frontier page template" pageEncoding="UTF-8"%>
<%@attribute name="extracss" fragment="true"%>
<%@attribute name="extrajs" fragment="true"%>
<!-- for more info please see http://stackoverflow.com/questions/1296235/jsp-tricks-to-make-templating-easier/3257426#3257426 -->
<!doctype html>
<!-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ -->
<!--[if lt IE 7]> <html class="no-js ie6 oldie" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js ie8 oldie" lang="en"> <![endif]-->
<!-- Consider adding an manifest.appcache: h5bp.com/d/Offline -->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->
<head>
    <meta charset="utf-8">

    <!-- Use the .htaccess and remove these lines to avoid edge case issues.
         More info: h5bp.com/b/378 -->
    <!-- <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> --> <!-- Not yet implemented -->

    <title>Annotare &lt; EMBL-EBI</title>
    <meta name="description" content="EMBL-EBI"><!-- Describe what this page is about -->
    <meta name="keywords" content="bioinformatics, europe, institute"><!-- A few keywords that relate to the content of THIS PAGE (not the whol project) -->
    <meta name="author" content="EMBL-EBI"><!-- Your [project-name] here -->

    <!-- Mobile viewport optimized: j.mp/bplateviewport -->
    <meta name="viewport" content="width=device-width,initial-scale=1">

    <!-- Place favicon.ico and apple-touch-icon.png in the root directory: mathiasbynens.be/notes/touch-icons -->

    <!-- CSS: implied media=all -->
    <!-- CSS concatenated and minified via ant build script-->
    <link rel="stylesheet" href="//www.ebi.ac.uk/web_guidelines/css/compliance/mini/ebi-fluid-embl.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/annotare.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/annotare-colours.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/font-awesome.min.css">
    <jsp:invoke fragment="extracss"/>

    <!-- end CSS-->


    <!-- All JavaScript at the bottom, except for Modernizr / Respond.
         Modernizr enables HTML5 elements & feature detects; Respond is a polyfill for min/max-width CSS3 Media Queries
         For optimal performance, use a custom Modernizr build: www.modernizr.com/download/ -->

    <!-- Full build -->
    <!-- <script src="//www.ebi.ac.uk/web_guidelines/js/libs/modernizr.minified.2.1.6.js"></script> -->

    <!-- custom build (lacks most of the "advanced" HTML5 support -->
    <script src="//www.ebi.ac.uk/web_guidelines/js/libs/modernizr.custom.49274.js"></script>
    <jsp:invoke fragment="extrajs"/>
</head>

<body class="level2"><!-- add any of your classes or IDs -->
<div id="skip-to">
    <ul>
        <li><a href="#content">Skip to main content</a></li>
        <li><a href="#local-nav">Skip to local navigation</a></li>
        <li><a href="#global-nav">Skip to EBI global navigation menu</a></li>
        <li><a href="#global-nav-expanded">Skip to expanded EBI global navigation menu (includes all sub-sections)</a></li>
    </ul>
</div>

<div id="wrapper" class="container_24">
    <header>
        <div id="global-masthead" class="masthead grid_24">
            <!--This has to be one line and no newline characters-->
            <a href="//www.ebi.ac.uk/" title="Go to the EMBL-EBI homepage"><img src="//www.ebi.ac.uk/web_guidelines/images/logos/EMBL-EBI/EMBL_EBI_Logo_white.png" alt="EMBL European Bioinformatics Institute"></a>

            <nav>
                <ul id="global-nav">
                    <!-- set active class as appropriate -->
                    <li class="first active" id="services"><a href="//www.ebi.ac.uk/services">Services</a></li>
                    <li id="research"><a href="//www.ebi.ac.uk/research">Research</a></li>
                    <li id="training"><a href="//www.ebi.ac.uk/training">Training</a></li>
                    <li id="industry"><a href="//www.ebi.ac.uk/industry">Industry</a></li>
                    <li id="about" class="last"><a href="//www.ebi.ac.uk/about">About us</a></li>
                </ul>
            </nav>

        </div>

        <div id="local-masthead" class="masthead grid_24 nomenu">

            <!-- local-title -->
            <!-- NB: for additional title style patterns, see http://frontier.ebi.ac.uk/web/style/patterns -->

            <div id="local-title" class="logo-title"><img class="svg" src="${pageContext.request.contextPath}/assets/images/annotare-logo-64.svg" width="64" height="64" alt="Annotare"><span><h1><a href=./" title="Back to Annotare homepage">Annotare</a></h1></span></div>

            <!-- /local-title -->

            <!-- local-nav -->

            <nav>
                <ul class="grid_24" id="local-nav">
                    <li class="first active"><a href="./">Home</a></li>
                    <li><a href="./help/">Help</a></li>
                    <li class="last"><a href="./about/">About Annotare</a></li>
                    <!-- If you need to include functional (as opposed to purely navigational) links in your local menu,
                         add them here, and give them a class of "functional". Remember: you'll need a class of "last" for
                         whichever one will show up last...
                         For example: -->
                    <li class="functional last"><a href="./login/" class="icon icon-functional" data-icon="l">Login</a></li>
                    <li class="functional"><a href="./sign-up/" class="icon icon-functional" data-icon="7">Register</a></li>
                    <li class="functional"><a href="#" class="icon icon-generic" data-icon="\">Feedback</a></li>
                </ul>
            </nav>

            <!-- /local-nav -->

        </div>
    </header>

    <div id="content" role="main" class="grid_24 clearfix">
        <jsp:doBody/>
    </div>


    <footer>
        <div id="global-footer" class="grid_24">

            <nav id="global-nav-expanded">

                <div class="grid_4 alpha">
                    <h3 class="embl-ebi"><a href="//www.ebi.ac.uk/" title="EMBL-EBI">EMBL-EBI</a></h3>
                </div>

                <div class="grid_4">
                    <h3 class="services"><a href="//www.ebi.ac.uk/services">Services</a></h3>
                </div>

                <div class="grid_4">
                    <h3 class="research"><a href="//www.ebi.ac.uk/research">Research</a></h3>
                </div>

                <div class="grid_4">
                    <h3 class="training"><a href="//www.ebi.ac.uk/training">Training</a></h3>
                </div>

                <div class="grid_4">
                    <h3 class="industry"><a href="//www.ebi.ac.uk/industry">Industry</a></h3>
                </div>

                <div class="grid_4 omega">
                    <h3 class="about"><a href="//www.ebi.ac.uk/about">About us</a></h3>
                </div>

            </nav>

            <section id="ebi-footer-meta">
                <p class="address">EMBL-EBI, Wellcome Trust Genome Campus, Hinxton, Cambridgeshire, CB10 1SD, UK &nbsp; &nbsp; +44 (0)1223 49 44 44</p>
                <p class="legal">Copyright &copy; EMBL-EBI 2013 | EBI is an outstation of the <a href="http://www.embl.org">European Molecular Biology Laboratory</a> | <a href="/about/privacy">Privacy</a> | <a href="/about/cookies">Cookies</a> | <a href="/about/terms-of-use">Terms of use</a></p>
            </section>

        </div>

    </footer>
</div> <!--! end of #wrapper -->


<!-- JavaScript at the bottom for fast page loading -->

<!-- Grab Google CDN's jQuery, with a protocol relative URL; fall back to local if offline -->
<!--
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
<script>window.jQuery || document.write('<script src="../js/libs/jquery-1.6.2.min.js"><\/script>')</script>
-->


<!-- Your custom JavaScript file scan go here... change names accordingly -->
<!--
<script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/plugins.js"></script>
<script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/script.js"></script>
-->
<!-- TODO: reinstate this
<script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/cookiebanner.js"></script>
-->
<script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/foot.js"></script>

<!-- end scripts-->

<!-- Google Analytics details... -->
<!-- Change UA-XXXXX-X to be your site's ID -->
<!--
<script>
  window._gaq = [['_setAccount','UAXXXXXXXX1'],['_trackPageview'],['_trackPageLoadTime']];
  Modernizr.load({
    load: ('https:' == location.protocol ? '//ssl' : '//www') + '.google-analytics.com/ga.js'
  });
</script>
-->
</body>
</html>
