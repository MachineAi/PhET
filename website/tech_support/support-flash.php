<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class FlashSupportPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $portal_root = PORTAL_ROOT;

        print <<<EOT
            <p>This page will help you solve some of the problems people commonly have running our programs. If you can't solve your problem here, please notify us by email at the following address: <a href="mailto:phethelp@colorado.edu"><span class="red">phethelp@colorado.edu</span></a>.</p>

            <p>To run the Flash-based simulations you must have Flash 7 (available free) or newer installed on your computer.</p><a href="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash"><img src="{$this->prefix}images/get-flash.gif" alt="Get Flash" /></a>

            <p>If you get a blank window when you try to launch a Flash simulation, you probably need a new version of the Flash player.</p>

            <p>Older versions of the Flash Player can cause problems. Updating your flash player is recommended if you receive an error similar to:<br />
            <br />
            <a href="/"><img src="{$this->prefix}images/flash-error.gif" alt="Image of a Flash error message" /></a></p>

            <p>If you are unsure if you currently have a version of Flash, we can check for you if you <a href="{$portal_root}web-pages/misc-pages/flash_detect_v7.html">click here.</a></p>

            <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Back to the Top" /></a></p>

EOT;
    }

}

$page = new FlashSupportPage("Troubleshooting Flash", NAV_TECH_SUPPORT, null);
$page->update();
$page->render();

?>
