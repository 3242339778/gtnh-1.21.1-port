import os, zipfile, shutil, sys

jar = r"C:\Users\qqerd\AppData\Roaming\.BakaXL\minecraft\libraries\net\neoforged\fancymodloader\earlydisplay\4.0.45\earlydisplay-4.0.45.jar"
orig = jar + ".orig"

if not os.path.exists(orig):
    print("缺原版备份 .orig，先备份当前")
    shutil.copy2(jar, orig)

target = "net/neoforged/fml/earlydisplay/ColourScheme.class"

with zipfile.ZipFile(orig) as z:
    data = z.read(target)

# RED 背景颜色 = sipush 239 (0x11 0x00 0xEF), bipush 50 (0x10 0x32), bipush 61 (0x10 0x3D)
pattern = bytes([0x11, 0x00, 0xEF, 0x10, 0x32, 0x10, 0x3D])
repl    = bytes([0x11, 0x00, 0x00, 0x10, 0x00, 0x10, 0x00])   # (0,0,0) 纯黑

cnt = data.count(pattern)
print("找到 RED 背景颜色模式 %d 处" % cnt)

if cnt != 1:
    print("模式不唯一，放弃自动改，需人工确认")
    sys.exit(1)

new = data.replace(pattern, repl)
print("替换完成，长度 %d -> %d" % (len(data), len(new)))

bak = jar + ".red"
if not os.path.exists(bak):
    shutil.copy2(jar, bak)

tmp = jar + ".new"
with zipfile.ZipFile(orig) as zin, zipfile.ZipFile(tmp, "w", zipfile.ZIP_DEFLATED) as zout:
    for item in zin.infolist():
        buf = zin.read(item.filename)
        if item.filename == target:
            buf = new
        zout.writestr(item, buf)

shutil.move(tmp, jar)
print("已重写: %s  %d B" % (jar, os.path.getsize(jar)))

with zipfile.ZipFile(jar) as z:
    d2 = z.read(target)
print("校验 RED 颜色字节存在黑色: %s" % (bytes([0x11,0x00,0x00,0x10,0x00,0x10,0x00]) in d2))
print("校验原红色已消失: %s" % (pattern not in d2))
