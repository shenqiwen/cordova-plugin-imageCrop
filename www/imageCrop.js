var exec = require('cordova/exec');

exports.coolMethod = function(arg0, success, error) {
    exec(success, error, "imageCrop", "coolMethod", [arg0]);
};

//裁剪  参1 : 图片路径 参2: 相机 camera  相册 album 参3,4: 宽高  所有参数均为字符串 
exports.crop = function( source_filepath,source_type,crop_width ,crop_high , success, error) {
    exec(success, error, "imageCrop", "crop", [source_filepath,source_type,crop_width,crop_high]);
};

//压缩
exports.compress = function( source_filepath ,compress_quality , success, error) {
    exec(success, error, "imageCrop", "compress", [source_filepath,compress_quality]);
};

//android专属 注册 图片路径回调 监听 只需调用一次
exports.addCallBackListener = function(arg0, success, error) {
    exec(success, error, "imageCrop", "addCallBackListener", [arg0]);
};