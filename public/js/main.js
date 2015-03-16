var _base_path = "http://127.0.0.1:9000"

/**
 * Json Object for function list
 * @type {{user_manage: string, item_manage: string, borrow: string, report: string}}
 */
var acl_func = {
    user_manage : "User Management",
    item_manage : "Item Management",
    borrow :"Borrow Item",
    report : "report"
}
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

    //Attributes
    //Tab info
    this.display_name = ko.observable("");
    this.username = ko.observable("");
    this.position = ko.observable("");
    this.division = ko.observable("");
    this.subunit = ko.observable("");
    this.team = ko.observable("");

    //Tab password
    this.pw = ko.observable("");
    this.confirm = ko.observable("");

    //Tab
    this.group = ko.observableArray();

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
                "display_name":self.display_name,
                "username":self.username,
                      "pw":self.pw,
                "position":self.position,
                "division":self.division,
                "subunit":self.subunit,
                "divison":self.divison,
                "team":self.team
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

        //reload

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

    /**
     * 0: success
     * 1: error
     */
    this.alert_status = ko.observable();
    this.alert_visible = ko.observable(false);

    this.success = function(message){
        self.alert_message(message);
        self.alert_status(0);
        self.alert_visible(true);
    }

    this.error = function(message){
        self.alert_message(message);
        self.alert_status(1);
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

/**]
 * View model for request borrow item
 * @constructor
 */
var RequestItemModel = function(){
    var self = this;

    this.request_location = ko.observable();
    this.request_items = ko.observableArray();

    this.request_new_item_id = ko.observable();
    this.request_new_item_qty = ko.observable();

    this.getNewItemName = function(){
        for(var x in items)
            if(items[x].id == self.request_new_item_id())
                return items[x].name;
    }

    this.click_add = function(){

        self.request_items.push({
            id:self.request_new_item_id(),
            name :self.getNewItemName(),
            qty:self.request_new_item_qty()
        });
    }

    this.click_add_request = function(){
        //ajax reqeust to server
    }
}

var ManageItemList = function(tableDom){
    var self = this;

    self.tableDom = tableDom;
}