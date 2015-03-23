var _base_path = "http://127.0.0.1:8080"

/**
 * Object for render action
 * */
var datatable_render_action = function(target_column){
    return {
        "mRender": function (data, type, row) {
        return "<button data-id=\""+data+"\" class=\"btn btn-default modal_edit\">Edit</button>"+
            "<button data-toggle=\"modal\" data-target=\"#del_user_modal\" data-id=\""+data+"\" class=\"btn btn-danger modal_delete\">Delete</button>"
    },
        "aTargets":[target_column]
    }
}

var datatable_remove_row = function(DTobj,event){
    DTobj.row($(event.target).parents("tr")).remove().draw();
}

/**
 * Init a generic select2
 */
var select2_init = function(select_dom , dt_obj ,select_ajax_path){
    return $(select_dom).select2({
        ajax:{
            url:select_ajax_path,
            type:"get",
            dataType: 'json',
            results:function(data){
                var ret = [];
                for(var x in data.data){
                    ret.push({
                        "id":data.data[x]._id.$oid,
                        "text":data.data[x].name
                    })
                }
                return {results:ret}
            },
            cache:true
        }
    }).on("select2-selecting",function(event){
        //function to handle select group
        //add the select to table
        //clear the selection
        //TODO : check table group exist ?
        dt_obj.row.add({
            _id:{$oid:event.val},
            name:event.choice.text
        })
        dt_obj.draw();
        $(event.target).select2("val",'');
    })
}

var dt_init = function(table_dom){
    return $(table_dom).DataTable({
        paging:false,
        searching:false,
        columns:[
            {data:"name"},
            {data:null}
        ],
        "aoColumnDefs":[
            {
                "mRender": function (data, type, row) {
                    return "<button class=\"btn btn-danger button-group-delete\">Delete</button>"
                },
                "aTargets":[1]
            }
        ],
        "drawCallback":function(){
            var self=this.api();
            //add handle for delete
            $(".button-group-delete").on("click",function(event){
                datatable_remove_row(self,event);
            })
        }
    })
}
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
    self.username = ko.observable("");
    self.password = ko.observable("");
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
var CreateUserViewModel = function(parentModel, groupTableDom, aclTableDom , select_addGroup_dom, select_add_feature_dom){

    var _path = _base_path +"/user/create"
    var self = this;

    /**
     * `create` and `update` mode only
     * @type {string}
     */
    this.mode = "create";
    this.update_id =null;

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

    //Tab group
    this.group = ko.observableArray();
    this.DTgroup = dt_init(groupTableDom);
    /**
     * Select for add group to the table
     * @type {*|jQuery}
     */
    this.select_add_group = select2_init(select_addGroup_dom,self.DTgroup,_base_path+"/group");

    //Tab access right
    this.DTfeature = dt_init(aclTableDom);
    this.select_add_acl = select2_init(select_add_feature_dom,self.DTfeature,_base_path+"/feature")

    /**
     * Check current mode and call
     */
    this.click_create_handle = function(){
        if(self.mode == "create"){
            this.createUser()
        }
        if(self.mode == "update"){
            var data =
            this.updateUser()
        }
    }

    this.clearFields = function(){
        self.display_name ("");
        self.username ("");
        self.position ("");
        self.division ("");
        self.subunit ("");
        self.team ("");

        self.DTgroup.clear().draw();
        self.DTfeature.clear().draw();
    }

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
                "display_name":self.display_name(),
                "username":self.username(),
                      "pw":self.pw(),
                "position":self.position(),
                "division":self.division(),
                "subunit":self.subunit(),
                "team":self.team()
            }),
            success:function(){
                alert_model.success("Successfully create user \""+self.username()+"\"");
                parentModel.requestUserList();
            },
            error : function(xhr,status,error) {
                var respone_json = JSON.parse(xhr.responseText);
                alert_model.error(respone_json["error"])
            }
        })
    }

    this.editUser = function(id,object){
        self.update_id = id;
        self.display_name(object.display_name);
        self.username (object.username);
        self.position(object.position);
        self.division (object.division);
        self.subunit(object.subunit);
        self.team (object.team);

        //init current group
        self.DTgroup.clear();
        $.ajax({
            url:_base_path+"/group/user/"+id,
            type:"GET",
            success:function(json){
                self.DTgroup.rows.add(json.data);
                self.DTgroup.draw();
            }
        })

        //init current feature
        self.DTfeature.clear();
        $.ajax({
            url:_base_path+"/feature/user/"+id,
            type:"GET",
            success:function(json){
                self.DTfeature.rows.add(json.data);
                self.DTfeature.draw();
            }
        })
    }

    this.updateUser = function(id){
        var update_info = {
            "display_name":self.display_name(),
            "username":self.username(),
            "position":self.position(),
            "division":self.division(),
            "subunit":self.subunit(),
            "team":self.team()
        }

        //update basic informatin
        if(self.pw()!=""){
            if(self.pw() != self.confirm()){
                alert_model.error("Password and Confirm Password are not same")
                return;
            }else{
                update_info["pw"] = self.pw()
            }
        }

        $.ajax(_base_path+"/user/"+self.update_id , {
            type :"PUT",
            dataType:"json",
            contentType :"application/json",
            data:ko.toJSON(update_info),
            success:function(){
                alert_model.success("Successfully update user \""+self.username()+"\"");
                parentModel.requestUserList();
            },
            error : function(xhr,status,error) {
                var respone_json = JSON.parse(xhr.responseText);
                alert_model.error(respone_json["error"])
            }
        })

        //update group
        var update_group_json = [];
        for(var x = 0; x <self.DTgroup.data().length;x++){
            update_group_json.push({
                "userid":self.update_id,
                "groupid":self.DTgroup.row(x).data()._id.$oid
            })
        }
        if(update_group_json.length > 0)
            $.ajax(_base_path+"/group/addMember",{
                type :"PUT",
                dataType:"json",
                contentType :"application/json",
                data:ko.toJSON(update_group_json)
            });

        //update feature acl
        var update_feature_json = [];
        for(var x = 0; x <self.DTfeature.data().length;x++){
            update_feature_json.push({
                "userid":self.update_id,
                "featureid":self.DTfeature.row(x).data()._id.$oid
            })
        }
        if(update_feature_json.length > 0)
            $.ajax(_base_path+"/feature/addMember",{
                type :"PUT",
                dataType:"json",
                contentType :"application/json",
                data:ko.toJSON(update_feature_json)
            });
    }
}

var ListUserModelView = function(datatable_DOM){

    var _path = _base_path+"/user"
    var self =this;
    this.createModel = null;
    this.userList = ko.observableArray();
    this.pageNumber = ko.observable(1);
    this.userPerPage = ko.observable(20);
    this.delete_user_display_name = ko.observable();
    this.delete_user_id = 0;
    this.datatable_obj = $(datatable_DOM).DataTable({
        columns:[
            {"data":"display_name"},
            {"data":"position"},
            {"data":"division"},
            {"data":"subunit"},
            {"data":"team"},
            {"data":"_id.$oid"}
        ],
        "aoColumnDefs": [
            datatable_render_action(5)
        ],
        "drawCallback":function(){
            $(".modal_edit").on("click",self.buttonEditHandle);
        }

    });

    this.buttonCreateHandle = function(){
        self.createModel.mode="create";
        self.createModel.clearFields();
        return true;
    }

    this.buttonEditHandle = function(){
        self.createModel.mode="update";
        var data = self.datatable_obj.row($(this).parents("tr")).data();
        self.createModel.editUser(data._id.$oid , data);
        $("#createUserModal").modal("show");
        return true;
    }

    this.updateTable =function(){
        self.datatable_obj.clear();
        var list = self.userList();
        for(var x in list){
            self.datatable_obj.row.add(list[x]);
        }
        self.datatable_obj.draw();

        //update handle
        $(".modal_delete").on("click",self.deleteClickHandle)
    }
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
                self.userList.removeAll();
                self.totalNum = json["total_num"];
                for(x in json.data)
                    self.userList.push(json.data[x]);
                self.updateTable();
            },
            error:function(){
                alert_model.error("Fail to retrieve user list")
            }
        })
    }

    this.getUserById = function(id){
        var list = self.datatable_obj.data();
        for(var x in list){
            if(list[x]._id.$oid == id){
                return list[x]
            }
        }
        throw "no record for id: "+id;
    }

    /**
     * Function to
     * @returns {boolean}
     */
    this.deleteClickHandle = function(){
        self.delete_user_id = $(this).data("id");
        self.delete_user_display_name(self.getUserById(self.delete_user_id).display_name);
        return true
    }

    this.requestDelUser = function(){
        var _del_path = _path + "/"+self.delete_user_id;
        $.ajax(_del_path,{
            type :"DELETE",
            dataType:"json",
            contentType :"application/json",
            data:ko.toJSON({}),
            success:function(json){
                alert_model.success("Successfull delete user \""+self.delete_user_display_name()+"\"");

                //update
                self.requestUserList();
            },
            error:function(){
                alert_model.error("Fail to remove user");
            }
        })
        return true;
    }

    self.requestUserList();
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
        //TODO : request borrow item
    }
}

/**
 * View model for item registration
 * @constructor
 */
var ItemRegistrationModel = function(){
    var self = this;

    this.registration_items = ko.observableArray();

    this.register_item_id = ko.observable();
    this.register_serial_num = ko.observable();

    this.getNewItemName = function(){
        for(var x in items)
            if(items[x].id == self.register_item_id())
                return items[x].name;
    }

    this.click_add = function(){
        self.registration_items.push({
            id:self.register_item_id(),
            name :self.getNewItemName(),
            serial_num:self.register_serial_num()
        });
    }

    this.click_add_request = function(){
        //ajax reqeust to server
        //TODO : request item registration
    }
}

var MangeGroupModel = function(tableDOM){
    var self = this;

    this.modal_name = ko.observable("");
    this.modal_description = ko.observable("");

    this.modal_members = ko.observableArray();

    this.modal_acl = ko.observableArray();

    self.dataTableObject = $(tableDOM).DataTable({
        columns:[
            {"data":"name"},
            {"data":"description"}
        ],
        "aoColumnDefs": [
            //Action
            {
                "mRender": function (data, type, row) {
                    return null;
                },
                "aTargets":[ 2 ]
            }
        ]
    });

    this.requesList = function(){
        $.ajax(_base_path+"/group",{
            type :"get",
            dataType:"json",
            contentType :"application/json",
            success:function(json){
                self.dataTableObject.clear();
                self.dataTableObject.rows.add(json.data);
                self.dataTableObject.draw();
            },
            error:function(){
                alert_model.error("Fail to retrieve group list")
            }
        })
    }

    this.requestCreate = function(){
        $.ajax(_base_path+"/group",{
            type:"POST",
            dataType:"json",
            contentType :"application/json",
            data:ko.toJSON({
                "name":self.modal_name(),
                "description":self.modal_description()
            }),
            contentType:"application/json",
            success:function(json){
                alert_model.success("Successfully added group : "+self.modal_name);
            },
            error:function(){
                alert_model.error("Fail to create group ");
            }
        })
    }

    this.createClickHandle = function(){
        self.requestCreate();
    }

    self.requesList();

}

var ManageItemListModel = function(tableDOM){
    var self = this;

    self.dataTableObject = $(tableDOM).DataTable({
        data:items,
        columns:[
            {"data":"image"},
            {"data":"name"},
            {"data":"description"}
        ],
        "aoColumnDefs": [
            datatable_render_action(0)
        ]
    });

    this.requestUserList = function(){
        $.ajax(_base_path+"/group",{
            type :"get",
            dataType:"json",
            contentType :"application/json",
            data:{
                "page":this.pageNumber(),
                "itemNum":this.userPerPage()
            },
            success:function(json){
                self.userList.removeAll();
                self.totalNum = json["total_num"];
                for(x in json.data)
                    self.userList.push(json.data[x]);
                self.updateTable();
            },
            error:function(){
                alert_model.error("Fail to retrieve user list")
            }
        })
    }

}

var ManageLocationModel = function(tableDOM , create_location_type_DOM){
    var self = this;

    self.dataTableObject = $(tableDOM).DataTable({
        data:locations,
        columns:[
            {"data":"name"},
            {"data":"type"},
            {"data":"description"}
        ]
    });

    self.select_location = $(create_location_type_DOM).select2({
        data:location_type,
        width:"100%"
    })
}

var ReceiveItemModel = function(data , item_table_DOM){
    var self = this;

    self.itemDataTable = $(item_table_DOM).DataTable({
        "paging":   false,
        "info":     false,
        "searching": false,
        data:data.items,
        columns:[
            {"data":"name"},
            {"data":"serial_num"}
        ]
    })

    self.from = ko.observable(data.from);
    self.to = ko.observable(data.to);
    self.items = ko.observableArray(data.items);

}

var AssignItemModel = function(select_from_location_DOM , select_to_location_DOM , select_assgin_item_DOM , select_assign_item_serial_DOM){
    var self = this;

    self.select_from_location = $(select_from_location_DOM).select2({
        data:getLocationGroupByType(),
        width:"100%"
    })

    self.select_to_location= $(select_to_location_DOM).select2({
        data:getLocationGroupByType(),
        width:"100%"
    })

    self.select_assgin_item = $(select_assgin_item_DOM).select2({
        data:getItemsGroupByCategory(),
        placeholder:"Item",
        width:"100%"
    })

    self.select_assign_item_serial = $(select_assign_item_serial_DOM).select2({
        data:getItemsGroupByCategory(),
        placeholder:"Serial Number",
        width:"100%"
    })
}