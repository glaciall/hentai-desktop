<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hentai Desktop</title>
    <link rel="stylesheet" type="text/css" href="${web_resource}/desktop.css" />
    <link rel="stylesheet" type="text/css" href="http://apps.bdimg.com/libs/animate.css/3.1.0/animate.min.css" />
    <script type="text/javascript" src="${web_resource}/timer.js"></script>
</head>
<body>
<div class="x-screen">
    <canvas id="screen" width="800" height="600"></canvas>
    <div class="opening"></div>
</div>
<div class="x-stat-panel">
    <div class="x-stat" id="x-session-id">
        <h1></h1>
        <h5>Id</h5>
    </div>
    <hr />
    <div class="x-stat" id="x-frames">
        <h1>0</h1>
        <h5>frames</h5>
    </div>
    <hr />
    <div class="x-stat" id="x-last-frame">
        <h1>0 kb</h1>
        <h5>last frame</h5>
    </div>
    <hr />
    <div class="x-stat" id="x-bytes">
        <h1>0 kb</h1>
        <h5>total transfer</h5>
    </div>
</div>
<div class="x-auth-dialog">
    <div class="x-title">输入密码开始连接</div>
    <hr />
    <div class="x-password"><input id="password" type="password" /></div>
    <div class="x-message"></div>
    <div class="x-button">
        <button id="btn-auth">开始连接</button>
    </div>
</div>
<script type="text/javascript" src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
<script type="text/javascript" src="${web_resource}/cursor.js"></script>
<script type="text/javascript">
    $.fn.extend({
        animateCss: function(animationName, callback) {
            var animationEnd = (function(el) {
                var animations = {
                    animation: 'animationend',
                    OAnimation: 'oAnimationEnd',
                    MozAnimation: 'mozAnimationEnd',
                    WebkitAnimation: 'webkitAnimationEnd',
                };

                for (var t in animations) {
                    if (el.style[t] !== undefined) {
                        return animations[t];
                    }
                }
            })(document.createElement('div'));

            this.addClass('animated ' + animationName).one(animationEnd, function() {
                $(this).removeClass('animated ' + animationName);

                if (typeof callback === 'function') callback();
            });

            return this;
        },
    });
</script>
<script type="text/javascript" src="${web_resource}/decompress.js"></script>
<script type="text/javascript" src="${web_resource}/desktop.js"></script>
<script type="text/javascript">
    var ROOT_PATH = '${context}';
    var RES_PATH = '${web_resource}';
    $(document).ready(function()
    {
        Desktop.init();
    });
</script>
</body>
</html>