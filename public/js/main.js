var _base_path = "http://127.0.0.1:8080"

/**
 * Model view for login
 * @constructor
 */
var LoginViewModel = function() {
    var self = this;
    var _path_login = _base_path+"/user/login"
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
            success : function(json){
                resultJsOp(json , self);
            },
            error : function(xhr,status,error){
                var respone_json = JSON.parse(xhr.responseText);
                resultJsOp(respone_json , self);
            }
        })
    }
}
/**
 * Menu View Model
 * @constructor
 */
var MenuViewModel = function(){
    var self = this;
    var _path_menu = _base_path + "/system/menu";
    self.menu_items = ko.observableArray();

    self.get = function(){
        $.ajax(_path_menu,{
            type:"GET",
            dataType:"json",
            success:function(json){
                for(x in json)
                    self.menu_items.push(json[x]);
            }
            //TODO: error handle
        })
    }
}

/**
 * Create User model view
 * @constructor
 */
var CreateUserViewModel = function(){
    var _path = _base_path +"/user/create"
    var self = this;

    this.username = ko.observable();
    this.pw = ko.observable();
    this.confirm = ko.observable();

    this.createUser = function(){
        if(this.pw() != this.confirm()){
            alert_model.error("Password and Confirm Password are not same")
            return;
        }
        $.ajax(_path , {
            type :"post",
            dataType:"json",
            contentType :"application/json",
            data:ko.toJSON({
                "username":self.username,
                      "pw":self.pw
            }),
            success:function(){
                alert_model.success("Successfully create user \""+self.username()+"\"");

                //clear
                self.username("");
                self.pw("");
                self.confirm("");
            },
            error : function(xhr,status,error) {
                var respone_json = JSON.parse(xhr.responseText);
                alert_model.error(respone_json["error"])
            }
        })
    }
}

/**
 *TODO: paginate
 */
var ListUserModelView = function(){

    var _path = _base_path+"/user"
    var self =this;
    this.userList = ko.observableArray();
    this.pageNumber = ko.observable(1);
    this.userPerPage = ko.observable(20);
    this.totalNum = ko.observable(0);

    this.requestUserList = function(){
        $.ajax(_path,{
            type :"get",
            dataType:"json",
            contentType :"application/json",
            data:{
                "page":this.pageNumber(),
                "itemNum":this.userPerPage()
            },
            success:function(json){
                self.totalNum = json["total_num"];
                for(x in json.data)
                    self.userList.push(json.data[x])
            },
            error:function(){
                alert_model.error("Fail to retrieve user list")
            }
        })
    }

    this.requestDelUser = function(){
        var user = this
        var id = user["_id"]["$oid"];
        var _del_path = _path + "/"+id
        $.ajax(_del_path,{
            type :"DELETE",
            dataType:"json",
            contentType :"application/json",
            data:ko.toJSON({}),
            success:function(json){
                alert_model.success("Successfull delete user \""+user.username+"\"");
                self.userList.remove(user);
            },
            error:function(){
                alert_model.error("Fail to remove user");
            }
        })
    }
}
/**
 * For alert module
 * @constructor
 */
var AlertViewModel = function(){
    var self = this;
    this.alert_message = ko.observable();
    this.alert_status = ko.observable();
    this.alert_visible = ko.observable(false);

    this.success = function(message){
        self.alert_message(message);
        self.alert_status("success");
        self.alert_visible(true);
    }

    this.error = function(message){
        self.alert_message(message);
        self.alert_status("error");
        self.alert_visible(true);
    }
}

/**
 * Standad result handler for server respond
 * - redirect
 * - error
 */
var resultJsOp = function(ret_obj , view_model){
    if(typeof ret_obj.error != 'undefined'){
        //error handling
        view_model.error_display(true);
        view_model.error_msg(ret_obj.error);
    }else if(typeof ret_obj.redirect != 'undefined'){
        //redirect
        window.location.href = ret_obj.redirect
    }
}