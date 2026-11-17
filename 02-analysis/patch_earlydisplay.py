import zipfile, os, struct, zlib, shutil

src = r"C:\Users\qqerd\AppData\Roaming\.BakaXL\minecraft\libraries\net\neoforged\fancymodloader\earlydisplay\4.0.45\earlydisplay-4.0.45.jar"
out = r"D:\GTNH\gtverse\05-port\earlydisplay-4.0.45-gtnh.jar"

def transparent_png(w=1, h=1):
    raw = bytearray()
    for y in range(h):
        raw.append(0)
        raw.extend(b"\x00\x00\x00\x00" * w)
    def chunk(t, d):
        return struct.pack(">I", len(d)) + t + d + struct.pack(">I", zlib.crc32(t+d) & 0xffffffff)
    ihdr = struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0)
    return b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", ihdr) + chunk(b"IDAT", zlib.compress(bytes(raw), 9)) + chunk(b"IEND", b"")

TRANSP = transparent_png(1, 1)

print("=== 原 jar 里的图片素材 ===")
with zipfile.ZipFile(src) as z:
    for n in z.namelist():
        if n.lower().endswith((".png", ".ttf", ".jpg")):
            print("   %-26s %d B" % (n, z.getinfo(n).file_size))

repl = {"fox_running.png": TRANSP, "squirrel.png": TRANSP}
if os.path.exists(out): os.remove(out)
with zipfile.ZipFile(src) as zin, zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as zout:
    for it in zin.infolist():
        data = zin.read(it.filename)
        if it.filename in repl:
            data = repl[it.filename]
            print("   替换 %s -> 透明" % it.filename)
        zout.writestr(it, data)
print("\n输出: %s  %d B" % (out, os.path.getsize(out)))

try:
    f = open(src, "ab"); f.close()
    print("libraries 原 jar 可写 -> 可以尝试直接替换")
    movable = True
except Exception as e:
    print("libraries 原 jar 被占用 ->", e)
    movable = False
