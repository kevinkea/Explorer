cordova.define("qt-dra-qtContact.qtContact", function(require, exports, module) {
/**
 * Created by Administrator on 2016/6/3.
 */

cordova.define("qt_contact", function(require, exports, module) {
    var exec = require('cordova/exec');
    var contactExports={};
    var util={
        draExec:function(df,fn){
            exec(df.success, df.error, "qtContact", fn, util.toArray(df));
        },
        toArray:function(obj){
            var ar=new Array();
            var _ob={};
            for(var key in obj){
                if(typeof(obj[key]!="function")){
                    _ob[key]=obj[key];
                }
            }
            ar[0]=JSON.stringify(_ob);
            return ar;
        },
        extend:function(df,data){
            try{
                if(typeof data=="object"){
                    for(var key in data){
                        if(toString.apply(data[key])=== '[object Array]'){
                            for(var i=0;i<data[key].length;i++){
                                if(!df[key][i]){
                                    df[key][i] = [];
                                }
                                df[key][i]=this.extend(df[key][i],data[key][i]);
                            }
                        }else{
                            df[key]=data[key];
                        }
                    }
                }else{
                    df=data;
                }
            }catch (e){
                console.log(e);
            }
            return df;
        }

    };


  //获取个人信息
      contactExports.getPersonalInfo=function(options){
          var df= {selectType:1,success:function(){},error:function(){}};
          df=util.extend(df,options);
          util.draExec(df,"getPersonalInfo");
      };
    module.exports=contactExports;
});
});
