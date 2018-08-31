/**
 * 图像解压，返回canvas所需的imageData
 * @param method
 * @param compressedData
 */
function decompress(method, compressedData, imageData)
{
    // 行程编码解码
    var headerLength = 16;
    for (var i = (compressedData[headerLength] & 0xff) * 3 + 1 + headerLength, k = 0; i < compressedData.length; )
    {
        var rl = (((compressedData[i] & 0xff) << 8) | (compressedData[i + 1] & 0xff)) & 0xffff
        var times = rl & 0x7fff;
        var red, green, blue;
        if ((rl & 0x8000) > 0)
        {
            var index = compressedData[i + 2] & 0xff;
            if (index == 0)
            {
                k += times * 4;
                i += 3;
                continue;
            }
            var index = (index - 1) * 3 + 1 + headerLength;
            red = compressedData[index] & 0xff;
            green = compressedData[index + 1] & 0xff;
            blue = compressedData[index + 2] & 0xff;
            i += 3;
        }
        else
        {
            red = compressedData[i + 2] & 0xff;
            green = compressedData[i + 3] & 0xff;
            blue = compressedData[i + 4] & 0xff;
            i += 5;
        }

        for (var s = 0; s < times; s++)
        {
            imageData.data[k++] = red;
            imageData.data[k++] = green;
            imageData.data[k++] = blue;
            imageData.data[k++] = 0xff;
        }
    }
}