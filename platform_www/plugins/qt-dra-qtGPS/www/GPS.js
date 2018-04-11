cordova.define("qt-dra-qtGPS.qtGPS", function(require, exports, module) {
/**
 * Created by Administrator on 2016/6/3.
 */

cordova.define("qt_GPS", function(require, exports, module) {
    var exec = require('cordova/exec');
    var GPSExports={};
    var util={
        draExec:function(df,fn){
            exec(df.success, df.error, "qtGPS", fn, util.toArray(df));
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


//    GPS是否开启
    GPSExports.GPSIsOpen = function(options){
        var df= {success:function(){},error:function(){}};
        df=util.extend(df,options);
        util.draExec(df,"GPSIsOpen");
    }

    //开启GPS
    GPSExports.openGPS = function(options){
        var df= {success:function(){},error:function(){}};
        df=util.extend(df,options);
        util.draExec(df,"openGPS");
    }

    //获取当前地理位置坐标
    GPSExports.getCurrentPosition = function(options){
        var df= {success:function(){},error:function(){}};
        df=util.extend(df,options);
        util.draExec(df,"getCurrentPosition");
    }
    module.exports=GPSExports;
});
});
