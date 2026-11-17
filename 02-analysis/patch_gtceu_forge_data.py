import zipfile, os, re, shutil

SRC = r"D:\GTNH\gtverse\02-analysis\_gtceu\gtceu.jar"
DST = r"D:\GTNH\gtverse\05-port\1211-mods\gtceu-1.21.1-7.0.2-patched.jar"
BAK = r"D:\GTNH\gtverse\02-analysis\_gtceu\gtceu.orig.jar"

if not os.path.exists(BAK):
    shutil.copy2(SRC, BAK)
    print("已备份原 jar:", BAK)

zin = zipfile.ZipFile(SRC, "r")
repl = re.compile(rb'"forge_data"\s*:')
changed = []
n_entries = 0
with zipfile.ZipFile(DST, "w", zipfile.ZIP_DEFLATED, compresslevel=9) as zout:
    for item in zin.infolist():
        data = zin.read(item.filename)
        n_entries += 1
        if item.filename.startswith("assets/gtceu/models/") and item.filename.endswith(".json") and b"forge_data" in data:
            new = repl.sub(b'"neoforge_data":', data)
            left = new.count(b"forge_data")
            changed.append((item.filename, left))
            data = new
        zi = zipfile.ZipInfo(item.filename, date_time=item.date_time)
        zi.compress_type = zipfile.ZIP_DEFLATED
        zi.external_attr = item.external_attr
        zout.writestr(zi, data)
zin.close()

news = os.path.getsize(DST)
olds = os.path.getsize(SRC)
print("原 jar: %d B / %d 条目" % (olds, n_entries))
print("新 jar: %d B" % news)
print("改写模型: %d 个" % len(changed))
bad = [(f, c) for f, c in changed if c != 0]
print("仍残留 forge_data 的模型:", len(bad))
for f, c in bad[:5]:
    print("   ", f, c)

z = zipfile.ZipFile(DST)
print("zip 完整性:", z.testzip() or "OK")
rest = 0
for n in z.namelist():
    if n.startswith("assets/gtceu/models/") and n.endswith(".json"):
        if b"forge_data" in z.read(n):
            rest += 1
print("全 jar 扫描残留 forge_data 模型:", rest)
