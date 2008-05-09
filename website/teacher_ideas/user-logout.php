<?php

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/web-utils.php");

    session_start();
    cookie_var_clear("contributor_email");
    cookie_var_clear("contributor_password_hash");
    session_write_close();

    if (isset($_REQUEST['url'])) {
        $url = $_REQUEST['url'];
    }
    else {
        $url = SITE_ROOT.'teacher_ideas/user-edit-profile.php';
    }

    // Just in case redirection doesn't work...
    force_redirect($url, 0);

    // ...but ideally, we'll do a header redirect
    force_header_redirect($url);

?>