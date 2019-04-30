window.Desktop = {
    imageData : null,
    canvas : null,
    element : null,
    totalTransfered : 0,
    connection : null,
    // 状态：standby,connected,controlling,exchanging,disconnected
    state : 'standby',
    frames : [],
    pointers : [],
    screenOffset : null,

    // 初始化
    init : function()
    {
        if (this.connection != null) return;
        this._bindEvents();
        this.connect();
        this.showLoginDialog();
        this._startTimer();

        this.canvas = (this.element = document.getElementById('screen')).getContext('2d');
        this.imageData = this.canvas.createImageData(10, 10);

        // 初始化鼠标指针
        for (var i = 0; i < Cursor.length; i++)
        {
            var img = new Image();
            img.src = ROOT_PATH + '/static/icon/cursor/' + Cursor[i] + '.png';
            img.width = 32;
            img.height = 32;
            img.style.position = 'absolute';
            img.style.top = '-10000px';
            img.style.left = '-10000px';
            img.style.zIndex = 10000;
            this.pointers[i] = img;
            document.body.appendChild(img);
        }
    },
    // 身份验证
    login : function()
    {
        var password = $('#password').val();
        if ($.trim(password).length == 0) return $('.x-message').html('请输入密码');
        this._send({
            type : 'command',
            command : 'login',
            password : password
        });
        $('#btn-auth').addClass('disable');
    },
    // 定时器
    _startTimer : function()
    {
        var self = this;
        setTimeout(function()
        {
            self.__decompressAndShow();
        }, 50);
        setTimeout(function()
        {
            self.__keepalive();
        }, 5000);
    },
    // 图像解压并显示
    __decompressAndShow : function()
    {
        var self = this;
        var compressedData = this.frames.shift();
        if (compressedData == undefined) return setTimeout(function(){ self.__decompressAndShow(); }, 50);

        var width = ((compressedData[0] << 8) | compressedData[1]) & 0xffff;
        var height = ((compressedData[2] << 8) | compressedData[3]) & 0xffff;
        var x = '';
        for (var i = 4; i < 12; i++)
        {
            x = x + ('00' + compressedData[i].toString(16)).replace(/^0+(\w{2})$/gi, '$1');
        }
        var captureTime = parseInt(x, 16);
        var sequence = (compressedData[12] << 24 | compressedData[13] << 16 | compressedData[14] << 8 | compressedData[15]) & 0xffffffff;
        this.totalTransfered += (compressedData.length / 1024);

        var tf = this.totalTransfered.toFixed(2) + ' kb';
        if (this.totalTransfered > 1024) tf = (this.totalTransfered / 1024).toFixed(2) + ' mb';
        $('#x-last-frame h1').html((compressedData.length / 1024).toFixed(2) + ' kb');
        $('#x-frames h1').html(sequence);
        $('#x-bytes h1').html(tf);
        if (this.imageData.width != width || this.imageData.height != height)
        {
            var screenElement = document.getElementById('screen');
            screenElement.width = width;
            screenElement.style.width = width;
            screenElement.height = height;
            screenElement.style.height = height;
            this.canvas.width = width;
            this.canvas.height = height;
            screenElement.style.marginLeft = parseInt(0 - width / 2) + 'px';
            screenElement.style.marginTop = parseInt(0 - height / 2) + 'px';
            this.imageData = this.canvas.createImageData(width, height);
            setTimeout(function()
            {
                self.__obtainScreenOffset();
            }, 0);
        }
        decompress('rle', compressedData, this.imageData);
        this.canvas.putImageData(this.imageData, 0, 0);
        setTimeout(function()
        {
            self.__decompressAndShow();
        }, 50);
    },

    // /////////////////////////////////////////////////////////////////////
    // 断开连接
    disconnect : function()
    {
        this.connection.close();
    },
    _send : function(cmd)
    {
        this.connection.send(JSON.stringify(cmd));
    },
    connect : function()
    {
        var self = this;
        if (this.connection && this.connection.readyState == 1) return;
        this.frames = [];
        this.hidCommands = [];
        this._standby();
        this.connection = new WebSocket('ws://' + location.host + '/desktop/wss');
        this.connection.onopen = function()
        {
            self._onopen.apply(self, arguments);
        };
        this.connection.onmessage = function()
        {
            self._onmessage.apply(self, arguments);
        };
        this.connection.onclose = function()
        {
            self._onclose.apply(self, arguments);
        };
        this.connection.onerror = function()
        {
            self._onerror.apply(self, arguments);
        };
        this.connection.binaryType = 'arraybuffer';
    },
    _onopen : function() { this._connected(); },
    _onmessage : function(resp)
    {
        var self = this;
        if (resp.data instanceof ArrayBuffer)
        {
            if (self.totalTransfered == 0) $('.opening').fadeOut();
            var packet = new Uint8Array(resp.data);
            this.frames.push(packet);
        }
        else
        {
            var response = eval('(' + resp.data + ')');
            console.log('action: ' + response.action);
            if ('login' == response.action)
            {
                $('#btn-auth').removeClass('disable');
                if (response.result == 'success')
                {
                    $('.x-message').text('密码校验通过');
                    $('.opening').fadeIn();
                }
                else $('.x-message').text(response.result);
            }
            else if ('request-desktop' == response.action)
            {
                if (response.result != 'success') this.showMessage(response.result);
                else $('.x-auth-dialog').animateCss('bounceOut', function() { $('.x-auth-dialog').hide(); });
                $('#x-session-id h1').html(response.extra);
            }
            else if ('status' == response.action)
            {
                if ('kicked' == response.result)
                {
                    alert('你已经被移出屏幕分享会议');
                }
            }
            else if ('setup' == response.action)
            {
                this._controlling();
            }
            else if ('pointer' == response.action)
            {
                this._showPointer(response.x, response.y, response.style);
            }
        }
    },
    _onclose : function() { this._disconnected(); },
    _onerror : function() { },

    // 状态变更
    _exchanging : function()
    {
        this.state = 'exchanging';
    },
    _controlling : function()
    {
        this.state = 'controlling';
    },
    _connected : function()
    {
        this.state = 'connected';
    },
    _standby : function()
    {
        this.state = 'standby';
    },
    _disconnected : function()
    {
        this.state = 'disconnected';
    },
    _isControlling : function()
    {
        return this.state == 'controlling';
    },

    // /////////////////////////////////////////////////////////////////////
    // 事件绑定
    _bindEvents : function()
    {
        var self = this;
        var screenElement = document.getElementById('screen');
        // 鼠标按下
        screenElement.onmousedown = function(e)
        {
            // TODO: 做一个围观群众的互动
        }
        screenElement.oncontextmenu = function(e)
        {
            if (e.preventDefault) e.preventDefault();
            return false;
        }
        $('#btn-auth').click(function()
        {
            self.login();
        });
        $('.x-dialog .x-close').click(function()
        {
            var dialog = null;
            (dialog = $(this).parents('.x-dialog')).animateCss('bounceOut', function(){ dialog.hide(); });
            self._controlling();
        });
        window.onresize = function()
        {
            self.__obtainScreenOffset();
        }
    },

    lastPointer : null,
    _showPointer : function(x, y, style)
    {
        if (this.lastPointer)
        {
            this.lastPointer.style.left = '-10000px';
            this.lastPointer.style.top = '-10000px';
        }
        if (null == this.screenOffset) return;
        this.pointers[style].style.top = (this.screenOffset.top + y) + 'px';
        this.pointers[style].style.left = (this.screenOffset.left + x) + 'px';
        this.lastPointer = this.pointers[style];
    },
    __obtainScreenOffset : function()
    {
        this.screenOffset = $(this.element).offset();
    },

    // /////////////////////////////////////////////////////////////////////
    // UI相关
    showLoginDialog : function()
    {
        $('.x-auth-dialog').animateCss('bounceIn');
    },
    showMessage : function(text)
    {
        var timeout = 4000;
        var box = $('<div class="x-message-box">' + text + '</div>');
        $(document.body).append(box);
        box.css({ top : (document.body.scrollTop + window.innerHeight - 100) + 'px' }).show().animateCss('bounceIn', function()
        {
            setTimeout(function()
            {
                box.remove();
            }, timeout);
        });
    },

    // /////////////////////////////////////////////////////////////////////
    // 杂项

    // 每5秒发送一个HTTP请求，以保持会话
    __keepalive : function()
    {
        var self = this;
        $.post(ROOT_PATH + '/keepalive', null, null);
        this._send({
            type : 'command',
            command : 'keepalive',
        });
        setTimeout(function()
        {
            self.__keepalive();
        }, 20000);
    }
};