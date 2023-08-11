<div class="form-cell" ${elementMetaData!}>
    <label class="label">
        ${element.properties.label} <span class="form-cell-validator">${decoration}</span>
        <#if error??>
            <span class="form-error-message">${error}</span>
        </#if>
    </label>
    <#if (element.properties.enableWebsocket! == 'true')>
        <input id="isEnabled" name="isEnabled" type="hidden" />
    </#if>

    <#if (element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
        <div class="form-cell-value"><span>${value!?html}</span></div>
        <input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" />
    <#else>    
        <input type="text" id="messageInput" placeholder="Enter message">
        <button id="sendButton">Send</button>
        <button id="closeButton">Close</button><br>
        <div id='output'></div>
    </#if>

    <script>
    $(document).ready(function() {
        $("#sendButton").off("click");
        $("#closeButton").off("click");

        if ($("#isEnabled").length > 0) {

            const ws = new WebSocket(((window.location.protocol === "https:") ? "wss://" : "ws://") + window.location.host + "${request.contextPath}/web/socket/plugin/org.joget.WebSocketPlugin");

            ws.onopen = function(event) {
                console.log(event);
                $("#output").append('Connection opened with timeStamp: ' + event.timeStamp + '<br/>');
   
                $("#sendButton").on("click", function(){
                    const message = $("#messageInput").val();

                    //send message to endpoint
                    ws.send(message);  
                    $("#output").append("${username} send " + message + '<br/>'); 
                    $("#messageInput").val("");
                    return false;
                });

                $("#closeButton").on("click", function(){
                    if (ws.readyState === WebSocket.OPEN) {
                        //close the endpoint connection
                        ws.close();
                    }
                    $("#sendButton").hide();
                    $("#closeButton").hide();
                    return false;       
                });
            }; 

            ws.onmessage = function(event) {
                $("#output").append(event.data + '<br/>');
            }; 

            ws.onclose = function(event) {
                $("#output").append('Connection closed with timeStamp: ' + event.timeStamp + '<br/>');
                $("#output").append('WebSocket closed<br/>');
            }; 

            ws.onError = function(event) {
                $("#output").append("Error: " + event.data + '<br/>');
            };  
        
        } else {
            $("#output").html('WebSocket is not enable.<br>');
        }
    });
    </script>
    <div style="clear:both;"></div>
</div>
