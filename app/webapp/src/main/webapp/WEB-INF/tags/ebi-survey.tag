
<%@tag description="Frontier page template" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<script defer="defer" src="//ebi.emblstatic.net/web_guidelines/EBI-Framework/v1.3/js/script.js"></script>

<!-- The Foundation theme JavaScript -->
<script src="//ebi.emblstatic.net/web_guidelines/EBI-Framework/v1.3/libraries/foundation-6/js/foundation.js"></script>
<script src="//ebi.emblstatic.net/web_guidelines/EBI-Framework/v1.3/js/foundationExtendEBI.js"></script>
<script type="text/JavaScript">$(document).foundation();</script>
<script type="text/JavaScript">$(document).foundationExtendEBI();</script>

<script type="text/javascript">
    document.addEventListener("DOMContentLoaded", function(event){
        ebiInjectAnnouncements({ headline: 'We need your help!', message: 'Has Annotare saved you time or effort? ' +
                'Please take 15 minutes to fill in a survey and help EMBL-EBI make the case for why open data resources are critical to life science research.  ' +
                '<b><a href="https://www.surveymonkey.com/r/QGFMBH8?channel=[webpage]" target="_blank">https://www.surveymonkey.com/r/QGFMBH8?channel=[webpage]</a></b>', priority: 'warning' });
        
        var notification_dive = document.getElementsByClassName("notifications-js")
        if (notification_dive.length > 0) {
            notification_dive[0].style.marginTop = "-1.5rem";
            notification_dive[0].style.marginBottom = "-1.5rem";
            notification_dive[0].style.maxWidth = "100rem"
        }
    })
    // var container = document.getElementById('main-content-area') || document.getElementById('main-content') || document.getElementById('main') || document.getElementById('content') || document.getElementById('contentsarea');
    // if (container == null) {
    //     var main = document.createElement('div');
    //     main.setAttribute("id", "main-content-area");
    //     document.body.insertBefore(main, document.body.firstChild);
    // }

</script>