<?php

    // Utils to support sims

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    
    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");
    require_once("include/db-utils.php");

    if (!defined('INSTALLER_TABLE_NAME')) {
        define('INSTALLER_TABLE_NAME', 'installer_info');
    }

    function installer_check_timestamp($new_timestamp) {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $table = INSTALLER_TABLE_NAME;
        $safe_timestamp = mysql_real_escape_string($new_timestamp, $connection);
        $query = "SELECT * FROM `{$table}` WHERE ".
            "`installer_info_timestamp` >= '{$safe_timestamp}'";
        $result = db_exec_query($query);
        $count = mysql_num_rows($result);
        if ($count > 0) {
            return false;
        }

        return true;
    }

    function installer_add_new_timestamp($new_timestamp) {
        $result = db_insert_row(INSTALLER_TABLE_NAME,
                                array('installer_info_timestamp' => $new_timestamp));
        if ($result > 0) {
            return true;
        }

        return false;
    }


    function installer_get_latest_timestamp() {
        $extra = "ORDER BY `installer_info_id` DESC LIMIT 1";
        $row = db_get_row_by_condition(INSTALLER_TABLE_NAME, array(), false, false, $extra);
        if (!$row || !isset($row['installer_info_timestamp'])) {
            return false;
        }
        
        return $row['installer_info_timestamp'];
    }

?>