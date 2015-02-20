var _base_path = "http://127.0.0.1:8080"

var _path_login = _base_path+"/user/login"

var LoginViewModel = function() {
    var self = this;
    self.username = ko.observable();
    self.password = ko.observable();
    self.error_display = ko.observable(false);
    self.error_msg = ko.observable();

    self.login = function(){
        $.ajax(_path_login,{
            data:ko.toJSON({username:self.username,password:self.password}),
            type :"post",
            contentType :"application/json",
            dataType:"json",
            success : function(){
                //TODO redirect

            },
            error : function(xhr,status,error){
                var respone_json = JSON.parse(xhr.responseText)
                self.error_display (true);
                //self.error_msg (respone_json["error"]);
                self.error_msg ("Wrong user name and password");
            }
        })
    }
}

/**
 * Standad result handler for server respond
 * - redirect
 * - error
 */
var ResultJsOp