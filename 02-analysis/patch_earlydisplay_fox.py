import os, zipfile, shutil, zlib, struct

jar = r"C:\Users\qqerd\AppData\Roaming\.BakaXL\minecraft\libraries\net\neoforged\fancymodloader\earlydisplay\4.0.45\earlydisplay-4.0.45.jar"

def transparent_png():
    raw = b"\x00\x00\x00\x00\x00"
    def chunk(t, d):
        c = t + d
        return c + struct.pack(">I", zlib.crc32(c) & 0xffffffff)
    ihdr = struct.pack(">IIBBBBB", 1, 1, 8, 6, 0, 0, 0)
    return (b"\x89PNG\r\n\x1a\n"
            + chunk(b"IHDR", ihdr)
            + chunk(b"IDAT", zlib.compress(raw, 9))
            + chunk(b"IEND", b""))

tp = transparent_png()
print("透明 PNG 字节: %d" % len(tp))

targets = {"fox_running.png": tp, "squirrel.png": tp}

tmp = jar + ".new"
with zipfile.ZipFile(jar) as zin, zipfile.ZipFile(tmp, "w", zipfile.ZIP_DEFLATED) as zout:
    for item in zin.infolist():
        buf = zin.read(item.filename)
        if item.filename in targets:
            old = len(buf)
            buf = tp
            print("   %s  %d -> %d B" % (item.filename, old, len(buf)))
        zout.writestr(item, buf)

shutil.move(tmp, jar)
print("\n已重写: %s  %d B" % (jar, os.path.getsize(jar)))

with zipfile.ZipFile(jar) as z:
    for n in targets:
        print("   校验 %-20s = %d B" % (n, z.getinfo(n).file_size))
    print("   校验 ColourScheme.class = %d B" % z.getinfo("net/neoforged/fml/earlydisplay/ColourScheme.class").file_size)
    names = z.namelist()
    print("   条目总数: %d" % len(names))
