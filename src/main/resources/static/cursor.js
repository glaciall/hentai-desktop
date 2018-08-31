window.Cursor = [
    'default', 'crosshair', 'text', 'wait', 'sw-resize', 'se-resize',
    'nw-resize', 'ne-resize', 'n-resize', 's-resize', 'w-resize', 'e-resize',
    'hand', 'move'
];

function GetCursor(type)
{
    var cursor = Cursor[type];
    if (typeof(cursor) == 'undefined') cursor = 'default';
    return ROOT_PATH + '/icon/cursor/' + cursor + '.png';
}