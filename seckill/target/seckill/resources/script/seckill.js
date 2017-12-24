<!--存放主要交互逻辑代码-->
// JavaScript 模块化
// seckill.detail.init(param)
var seckill = {
    //封装秒杀相关ajax的url
    URL: {
        now: function () {
            return "/seckill/time/now";
        },

        exposer: function (seckillId) {
            return "/seckill/" + seckillId + "/exposer";
        },

        executor: function (seckillId, md5) {
            return "/seckill/" + seckillId + "/" + md5 + "/execute";
        }

    },

    //处理秒杀逻辑
    handleSeckillKill: function (seckillId, node) {
        //获取秒杀地址，控制显示逻辑，执行秒杀
        node.hide() //节点操作前要隐藏
            .html('<button class = "btn btn-primary btn-lg" id = "killBtn">执行秒杀!</button>');//按钮
        $.get(seckill.URL.exposer(seckillId), {}, function (result) {
            //在回调函数中，执行交互程序
            if (result && result["success"]) {
                var exposer = result["data"];
                if (exposer["exposed"]) {
                    //秒杀开启
                    //获取秒杀地址
                    var md5 = exposer["md5"];
                    var killUrl = seckill.URL.executor(seckillId, md5);
                    console.log("killUrl = " + killUrl);

                    //绑定一次点击事件
                    $("#killBtn").one('click', function () {
                        //执行秒杀请求
                        //1:先禁用按钮
                        $(this).addClass("disabled"); //$(this)===$('#killBtn')
                        //2:发送秒杀请求
                        $.post(killUrl, {}, function (result) {
                            if (result && result["success"]) {
                                var killResult = result["data"];
                                var state = killResult["state"];
                                var stateInfo = killResult["stateInfo"];
                                //3:显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                    node.show();
                } else {
                    //秒杀未开启，这是由于客户端与服务端时间跳转偏移引起的
                    var now = exposer["now"];
                    var startTime = exposer["startTime"];
                    var endTime = exposer["endTime"];
                    //重新计算计时逻辑
                    seckill.countDown(seckillId, now, startTime, endTime);
                }
            } else {
                console.log("result = " + result);
            }
        });
    },

    //验证手机号码
    validatePhone: function (phone) {
        // isNaN 非数字返回true
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },

    countDown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $("#seckill-box");
        // 时间判断
        if (nowTime > endTime) {
            // 秒杀结束
            seckillBox.html("秒杀结束!");

        } else if (nowTime < startTime) {
            // 秒杀未开始，计时时间绑定
            var killTime = new Date(startTime + 1000); //多加1s，防止客户端计时的偏移
            seckillBox.countdown(killTime, function (event) {
                //时间格式
                var format = event.strftime("秒杀倒计时: %D天 %H时 %M分 %S秒");
                seckillBox.html(format);
            }).on("finish.countdown", function () { //倒计时完成后回调事件
                //秒杀开始
                seckill.handleSeckillKill(seckillId, seckillBox);


            })

        } else {
            // 秒杀开始
            seckill.handleSeckillKill(seckillId, seckillBox);

        }

    },

    //详情页秒杀逻辑
    detail: {
        // 详情页初始化
        init: function (params) {
            //验证手机号码和登录, 计时交互
            //规划交互流程

            //在cookie中查找手机号码
            var killPhone = $.cookie('killPhone');

            //验证手机号

            // 如果未绑定
            if (!seckill.validatePhone(killPhone)) {
                //绑定手机号
                //控制输出
                var killPhoneModal = $('#killPhoneModal');
                //显示弹出层
                killPhoneModal.modal({
                    show: true, //显示弹出层
                    backdrop: "static", //禁止位置关闭
                    keyboard: false //关闭键盘事件
                });

                $("#killPhoneBtn").click(function () {
                    var inputPhone = $("#killPhoneKey").val();
                    console.log("inputPhone = " + inputPhone); //TODO
                    if (seckill.validatePhone(inputPhone)) {
                        //电话写入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //刷新页面
                        window.location.reload();
                    } else {
                        // 先隐藏节点.hide() -> 写入内容 -> 显示节点
                        // 好处是：写入内容的过程可能需要时间，不希望用户看到中间的任何过程
                        $("#killPhoneMessage").hide().html('<label class="label' +
                            ' label-danger">手机号码错误！</label>').show(300);
                    }
                });
            }

            //已经登录
            //计时交互
            var seckillId = params["seckillId"]; //This is the way that js accesses json data
            var startTime = params["startTime"];
            var endTime = params["endTime"];

            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result["success"]) {
                    var nowTime = result["data"];
                    //时间判断 计时交互
                    seckill.countDown(seckillId, nowTime, startTime, endTime);

                } else {
                    console.log("result = " + result)
                }
            })
        }

    }
}