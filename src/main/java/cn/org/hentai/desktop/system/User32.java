package cn.org.hentai.desktop.system;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by matrixy on 2019/5/8.
 */
public interface User32 extends com.sun.jna.Library
{
    User32 INSTANCE = Native.loadLibrary("User32.dll", User32.class);
    int GetCursorInfo(CURSORINFO cursorinfo);
    WinNT.HANDLE LoadImageA(
            WinDef.HINSTANCE hinst,
            Pointer lpszName,
            int uType,
            int cxDesired,
            int cyDesired,
            int fuLoad
    );

    public static class CURSORINFO extends Structure {
        public int cbSize;
        public int flags;
        public WinDef.HCURSOR hCursor;
        public WinDef.POINT ptScreenPos;

        public CURSORINFO()
        {
            this.cbSize = Native.getNativeSize(CURSORINFO.class, null);
        }

        @Override
        protected List<String> getFieldOrder()
        {
            return Arrays.asList("cbSize", "flags", "hCursor", "ptScreenPos");
        }
    }

    public enum Cursor {
        APPSTARTING(32650),
        NORMAL(32512),
        CROSS(32515),
        HAND(32649),
        HELP(32651),
        IBEAM(32513),
        NO(32648),
        SIZEALL(32646),
        SIZENESW(32643),
        SIZENS(32645),
        SIZENWSE(32642),
        SIZEWE(32644),
        UP(32516),
        WAIT(32514),
        PEN(32631),

        // UNMAPPED CURSORS (identified by address, manual test here to discover the address)
        ALL_SCROLL(-1, 0xff10323),
        ZOOM_IN(-1, 0xa580327)
        ;

        private final int code;
        private final int address;

        Cursor(final int code) {
            this.code = code;
            this.address = -1;
        }
        Cursor(final int code, final int address) {
            this.code = code;
            this.address = address;
        }

        public int getCode() {
            return code;
        }

        public int getAddress() {
            return address;
        }
    }
}
