<html>
<head>
    <script type="text/javascript" language="JavaScript" src="js/jquery-1.8.0.min.js"></script>
</head>
<body>
    <h2>Welcome to DBCheck</h2>

    <form action="dbchecker">
        <b>Connection String :</b><input id="url" type="text" size="100"/><br/>
        <b>Username :</b><input id="user" type="text" size="100"/><br/>
        <b>Password :</b><input id="pass" type="text" size="100"/><br/>
        <b>Driver :</b>
        <select id="driver">
          <option value="1" selected="selected">MySql</option>
          <option value="2">Oracle</option>
          <option value="3">SQL Server (jtds)</option>
          <option value="4">PostgreSQL</option>
        </select><br/>
        <b>SQL</b><textarea rows="10" cols="100" id="sql">select 1 from dual</textarea><br/>
        <input id="repeat" type="checkbox">Repeat?</input>
        <button id="startStopButton" type="button">Start</button>
    </form>
    <div id="connectionResults"></div>
</body>
<script type="text/javascript">
    $(document).ready(function() {
        $('#startStopButton').bind('click', function(event) {
            if ($('#startStopButton').text() == 'Start') {
                $('#connectionResults').text('');
                $('#startStopButton').text('Stop');
                makeDbCall();
            }
            else {
                $('#repeat').attr('checked', false);
                $('#startStopButton').text('Start');
            }                
        });
    });
    
    function makeDbCall() {
        $.ajax({
            url: "dbchecker",
            cache: false,
            async: true, // block
            type: "GET",
            data: { url: $('#url').val(),
                    driver: $('#driver').val(),
                    user: $('#user').val(),
                    pass: $('#pass').val(),
                    sql: $('#sql').val()
                  },
            success: function(data) {
                $('#connectionResults').append(data);
                if ($('#repeat').is(':checked')){
                    setTimeout(makeDbCall, 10000);
                } 
                else {
                    $('#repeat').attr('checked', false);
                    $('#startStopButton').text('Start');
                }
            },
            error: function(data) {
                $('#repeat').attr('checked', false);
                $('#startStopButton').text('Start');
                $('#connectionResults').append(data.responseText);
            }
        });
    }
</script>
</html>
