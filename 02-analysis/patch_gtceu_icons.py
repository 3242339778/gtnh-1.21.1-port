import zipfile, os, shutil, struct, zlib

g5 = r"C:\Users\qqerd\Desktop\mc启动器\Prism Launcher\instances\GTNH\.minecraft\mods\gregtech-5.09.54.265.jar"
cur = r"C:\Users\qqerd\AppData\Roaming\.BakaXL\minecraft\instances\ceshi\.minecraft\mods\gtceu-1.21.1-7.0.2-patched.jar"
z5 = zipfile.ZipFile(g5); zc = zipfile.ZipFile(cur)

tpl5 = {}
for n in z5.namelist():
    if "textures/items/materialicons/" in n and n.endswith(".png"):
        rel = n.split("/materialicons/",1)[1][:-4]
        seg = rel.split("/")
        if len(seg)==2: tpl5[(seg[0],seg[1])] = n

def base_suf(f):
    for s in ("_secondary","_overlay"):
        if f.endswith(s): return f[:-len(s)], s
    return f, ""

form_map = {"dust":"dust","dust_small":"dustSmall","dust_tiny":"dustTiny",
 "dust_impure":"dustImpure","dust_pure":"dustPure","ingot":"ingot",
 "ingot_hot":"ingotHot","ingot_double":"ingotDouble","plate":"plate",
 "plate_dense":"plateDense","plate_double":"plateDouble","gem":"gem",
 "gem_chipped":"gemChipped","gem_flawed":"gemFlawed","gem_exquisite":"gemExquisite",
 "gem_flawless":"gemFlawless","nugget":"nugget","gear":"gearGt",
 "gear_small":"gearGtSmall","rod":"stick","rod_long":"stickLong","bolt":"bolt",
 "ring":"ring","round":"round","spring":"spring","spring_small":"springSmall",
 "screw":"screw","wire":"wire","wire_fine":"wireFine","foil":"foil","crushed":"crushed",
 "crushed_purified":"crushedPurified","crushed_refined":"crushedCentrifuged",
 "raw_ore":"oreRaw","rotor":"rotor","lens":"lens","turbine_blade":"TURBINE_BLADE",
 "cell":"cell","tool_head_buzz_saw":"toolHeadBuzzSaw","tool_head_chainsaw":"toolHeadChainsaw",
 "tool_head_drill":"toolHeadDrill","tool_head_screwdriver":"toolHeadScrewdriver",
 "tool_head_wirecutter":"toolHeadWireCutter","tool_head_wrench":"toolHeadWrench"}
special_forms = {"gem","gem_chipped","gem_flawed","gem_exquisite","gem_flawless","lens","turbine_blade","rotor"}
iset_map = {"dull":"NONE","shiny":"SHINY","metallic":"METALLIC","bright":"SHINY",
 "radioactive":"NUCLEAR","sand":"SAND","rough":"ROUGH","fine":"FINE","wood":"WOOD",
 "diamond":"DIAMOND","quartz":"QUARTZ","magnetic":"MAGNETIC","certus":"GEM_A",
 "ruby":"RUBY","gem_horizontal":"GEM_HORIZONTAL","gem_vertical":"GEM_VERTICAL",
 "emerald":"EMERALD","glass":"GLASS","lignite":"LIGNITE","lapis":"LAPIS",
 "netherstar":"NETHERSTAR","flint":"FLINT","opal":"OPAL"}
gem_sets = {"diamond","quartz","ruby","certus","emerald","gem_horizontal","gem_vertical","lapis","netherstar","opal","glass","lignite","flint"}

def transparent_png(size=16):
    raw = bytearray()
    for y in range(size):
        raw.append(0); raw.extend(b"\x00\x00\x00\x00" * size)
    def chunk(t, d):
        return struct.pack(">I", len(d)) + t + d + struct.pack(">I", zlib.crc32(t+d) & 0xffffffff)
    ihdr = struct.pack(">IIBBBBB", size, size, 8, 6, 0, 0, 0)
    return b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", ihdr) + chunk(b"IDAT", zlib.compress(bytes(raw), 9)) + chunk(b"IEND", b"")
TRANSP = transparent_png()

pre = "assets/gtceu/textures/item/material_sets/"
repl = {}
n_ok = 0; n_trans = 0; n_keep = 0
for nm in zc.namelist():
    if not (nm.startswith(pre) and nm.endswith(".png")): continue
    rel = nm[len(pre):][:-4]
    seg = rel.split("/")
    if len(seg) != 2: continue
    ic, fc = seg[0], seg[1]
    b, suf = base_suf(fc)
    f5 = form_map.get(b)
    if not f5:
        n_keep += 1; continue
    cands = []
    if b in special_forms or ic in gem_sets:
        i5 = iset_map.get(ic, "NONE")
        cands += [(i5, f5), (i5, f5+"_OVERLAY")]
    cands += [("NONE", f5), ("NONE", f5+"_OVERLAY")]
    i5d = iset_map.get(ic, "NONE")
    if i5d != "NONE":
        cands += [(i5d, f5), (i5d, f5+"_OVERLAY")]
    cands += [("METALLIC", f5), ("SHINY", f5), ("FINE", f5), ("ROUGH", f5), ("MAGNETIC", f5), ("DULL", f5)]
    if suf:
        hit = None
        for c in cands:
            if c[1].endswith("_OVERLAY") and c in tpl5: hit = c; break
        if hit:
            repl[nm] = z5.read(tpl5[hit]); n_ok += 1
        else:
            repl[nm] = TRANSP; n_trans += 1
    else:
        hit = None
        for c in cands:
            if c in tpl5: hit = c; break
        if hit:
            repl[nm] = z5.read(tpl5[hit]); n_ok += 1
        else:
            n_keep += 1

print("待替换 %d 个贴图（实图 %d / 透明层 %d），保留原样 %d" % (len(repl), n_ok, n_trans, n_keep))

out = r"D:\GTNH\gtverse\05-port\1211-mods\gtceu-1.21.1-7.0.2-gtnh-icons.jar"
if os.path.exists(out): os.remove(out)
with zipfile.ZipFile(cur) as zin, zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as zout:
    for it in zin.infolist():
        data = zin.read(it.filename)
        if it.filename in repl:
            data = repl[it.filename]
        zout.writestr(it, data)
print("输出: %s  %d B" % (out, os.path.getsize(out)))

dst = r"C:\Users\qqerd\AppData\Roaming\.BakaXL\minecraft\instances\ceshi\.minecraft\mods\gtceu-1.21.1-7.0.2-gtnh-icons.jar"
shutil.copyfile(out, dst)
print("已投放实例: %d B" % os.path.getsize(dst))

victim = r"C:\Users\qqerd\AppData\Roaming\.BakaXL\minecraft\instances\ceshi\.minecraft\mods\gtceu-1.21.1-7.0.2-patched.jar"
if os.path.exists(victim):
    bak = victim + ".bak2"
    if not os.path.exists(bak): shutil.copyfile(victim, bak)
    os.remove(victim)
    print("已移除旧的 patched.jar（备份为 .bak2）")
print("\nmods 目录:")
for f in sorted(os.listdir(r"C:\Users\qqerd\AppData\Roaming\.BakaXL\minecraft\instances\ceshi\.minecraft\mods")):
    p = os.path.join(r"C:\Users\qqerd\AppData\Roaming\.BakaXL\minecraft\instances\ceshi\.minecraft\mods", f)
    print("   %-52s %d B" % (f, os.path.getsize(p)))
