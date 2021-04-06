/*
 * Tencent is pleased to support the open source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dq.itopic.views.emojitextview;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.SparseIntArray;

import com.dq.itopic.R;

import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * 用于作性能比较的控件。
 */
@SuppressWarnings({"JavaDoc", "UnusedReturnValue", "unused", "WeakerAccess"})
public final class EmojiconHandler {

    public static final HashMap<String, Integer> sQQFaceMap = new LinkedHashMap<>();
    private static final SparseIntArray sEmojisMap = new SparseIntArray(846);
    /**
     * 表情的放大倍数
     */
    private static final float EMOJIICON_SCALE = 1.2f;
    /**
     * 表情的偏移值
     */
    private static final int EMOJIICON_TRANSLATE_Y = 0;
    private static final int QQFACE_TRANSLATE_Y = dpToPx(1);

    /**
     * 屏幕密度,系统源码注释不推荐使用
     */
    public static final float DENSITY = Resources.getSystem()
            .getDisplayMetrics().density;

    /**
     * 把以 dp 为单位的值，转化为以 px 为单位的值
     *
     * @param dpValue
     *            以 dp 为单位的值
     * @return px value
     */
    public static int dpToPx(int dpValue) {
        return (int) (dpValue * DENSITY + 0.5f);
    }

    static {
        long start = System.currentTimeMillis();
        sQQFaceMap.put("[微笑]", R.drawable.expression_1);
        sQQFaceMap.put("[撇嘴]", R.drawable.expression_2);
        sQQFaceMap.put("[色]", R.drawable.expression_3);
        sQQFaceMap.put("[发呆]", R.drawable.expression_4);
        sQQFaceMap.put("[得意]", R.drawable.expression_5);
        sQQFaceMap.put("[流泪]", R.drawable.expression_6);
        sQQFaceMap.put("[害羞]", R.drawable.expression_7);
        sQQFaceMap.put("[闭嘴]", R.drawable.expression_8);
        sQQFaceMap.put("[睡]", R.drawable.expression_9);
        sQQFaceMap.put("[大哭]", R.drawable.expression_10);
        sQQFaceMap.put("[尴尬]", R.drawable.expression_11);
        sQQFaceMap.put("[发怒]", R.drawable.expression_12);
        sQQFaceMap.put("[调皮]", R.drawable.expression_13);
        sQQFaceMap.put("[呲牙]", R.drawable.expression_14);
        sQQFaceMap.put("[惊讶]", R.drawable.expression_15);
        sQQFaceMap.put("[难过]", R.drawable.expression_16);
        sQQFaceMap.put("[酷]", R.drawable.expression_17);
        sQQFaceMap.put("[冷汗]", R.drawable.expression_18);
        sQQFaceMap.put("[抓狂]", R.drawable.expression_19);
        sQQFaceMap.put("[吐]", R.drawable.expression_20);

        sQQFaceMap.put("[嘿哈]", R.drawable.expression_101);
        sQQFaceMap.put("[奸笑]", R.drawable.expression_102);
        sQQFaceMap.put("[捂脸]", R.drawable.expression_103);
        sQQFaceMap.put("[机智]", R.drawable.expression_104);
        sQQFaceMap.put("[皱眉]", R.drawable.expression_105);
        sQQFaceMap.put("[耶]", R.drawable.expression_106);
        sQQFaceMap.put("[红包]", R.drawable.expression_107);
        sQQFaceMap.put("[蜡烛]", R.drawable.expression_108);
        sQQFaceMap.put("[小鸡]", R.drawable.expression_109);
        sQQFaceMap.put("[旺柴]", R.drawable.expression_110);

        sQQFaceMap.put("[吃瓜]", R.drawable.watermelon);
        sQQFaceMap.put("[加油]", R.drawable.addoil);
        sQQFaceMap.put("[汗]", R.drawable.sweat);
        sQQFaceMap.put("[天啊]", R.drawable.shocked);
        sQQFaceMap.put("[Emm]", R.drawable.cold);
        sQQFaceMap.put("[社会]", R.drawable.social);
        sQQFaceMap.put("[好的]", R.drawable.noprob);
        sQQFaceMap.put("[打脸]", R.drawable.slap);
        sQQFaceMap.put("[翻白眼]", R.drawable.boring);
        sQQFaceMap.put("[666]", R.drawable.sixsixsix);
        sQQFaceMap.put("[我看看]", R.drawable.letmesee);
        sQQFaceMap.put("[叹气]", R.drawable.sigh);
        sQQFaceMap.put("[苦涩]", R.drawable.hurt);
        sQQFaceMap.put("[裂开]", R.drawable.broken);

        sQQFaceMap.put("[偷笑]", R.drawable.expression_21);
        sQQFaceMap.put("[白眼]", R.drawable.expression_23);
        sQQFaceMap.put("[傲慢]", R.drawable.expression_24);
        sQQFaceMap.put("[饥饿]", R.drawable.expression_25);
        sQQFaceMap.put("[困]", R.drawable.expression_26);
        sQQFaceMap.put("[惊恐]", R.drawable.expression_27);
        sQQFaceMap.put("[流汗]", R.drawable.expression_28);
        sQQFaceMap.put("[憨笑]", R.drawable.expression_29);
        sQQFaceMap.put("[悠闲]", R.drawable.expression_30);
        sQQFaceMap.put("[奋斗]", R.drawable.expression_31);
        sQQFaceMap.put("[咒骂]", R.drawable.expression_32);
        sQQFaceMap.put("[疑问]", R.drawable.expression_33);
        sQQFaceMap.put("[嘘]", R.drawable.expression_34);
        sQQFaceMap.put("[晕]", R.drawable.expression_35);
        sQQFaceMap.put("[疯了]", R.drawable.expression_36);
        sQQFaceMap.put("[衰]", R.drawable.expression_37);
        sQQFaceMap.put("[骷髅]", R.drawable.expression_38);
        sQQFaceMap.put("[敲打]", R.drawable.expression_39);
        sQQFaceMap.put("[再见]", R.drawable.expression_40);
        sQQFaceMap.put("[擦汗]", R.drawable.expression_41);
        sQQFaceMap.put("[抠鼻]", R.drawable.expression_42);
        sQQFaceMap.put("[鼓掌]", R.drawable.expression_43);
        sQQFaceMap.put("[坏笑]", R.drawable.expression_45);
        sQQFaceMap.put("[糗大了]", R.drawable.expression_44);
        sQQFaceMap.put("[左哼哼]", R.drawable.expression_46);
        sQQFaceMap.put("[右哼哼]", R.drawable.expression_47);
        sQQFaceMap.put("[哈欠]", R.drawable.expression_48);
        sQQFaceMap.put("[鄙视]", R.drawable.expression_49);
        sQQFaceMap.put("[委屈]", R.drawable.expression_50);
        sQQFaceMap.put("[快哭了]", R.drawable.expression_51);
        sQQFaceMap.put("[阴险]", R.drawable.expression_52);
        sQQFaceMap.put("[亲亲]", R.drawable.expression_53);
        sQQFaceMap.put("[吓]", R.drawable.expression_54);
        sQQFaceMap.put("[可怜]", R.drawable.expression_55);
        sQQFaceMap.put("[菜刀]", R.drawable.expression_56);
        sQQFaceMap.put("[西瓜]", R.drawable.expression_57);
        sQQFaceMap.put("[啤酒]", R.drawable.expression_58);
        sQQFaceMap.put("[篮球]", R.drawable.expression_59);
        sQQFaceMap.put("[乒乓]", R.drawable.expression_60);
        sQQFaceMap.put("[咖啡]", R.drawable.expression_61);
        sQQFaceMap.put("[饭]", R.drawable.expression_62);
        sQQFaceMap.put("[猪头]", R.drawable.expression_63);
        sQQFaceMap.put("[玫瑰]", R.drawable.expression_64);
        sQQFaceMap.put("[凋谢]", R.drawable.expression_65);
        sQQFaceMap.put("[嘴唇]", R.drawable.expression_66);
        sQQFaceMap.put("[爱心]", R.drawable.expression_67);
        sQQFaceMap.put("[心碎]", R.drawable.expression_68);
        sQQFaceMap.put("[蛋糕]", R.drawable.expression_69);
        sQQFaceMap.put("[闪电]", R.drawable.expression_70);
        sQQFaceMap.put("[炸弹]", R.drawable.expression_71);
        sQQFaceMap.put("[刀]", R.drawable.expression_72);
        sQQFaceMap.put("[足球]", R.drawable.expression_73);
        sQQFaceMap.put("[瓢虫]", R.drawable.expression_74);
        sQQFaceMap.put("[便便]", R.drawable.expression_75);
        sQQFaceMap.put("[月亮]", R.drawable.expression_76);
        sQQFaceMap.put("[太阳]", R.drawable.expression_77);
        sQQFaceMap.put("[礼物]", R.drawable.expression_78);
        sQQFaceMap.put("[拥抱]", R.drawable.expression_79);
        sQQFaceMap.put("[强]", R.drawable.expression_80);
        sQQFaceMap.put("[弱]", R.drawable.expression_81);
        sQQFaceMap.put("[握手]", R.drawable.expression_82);
        sQQFaceMap.put("[胜利]", R.drawable.expression_83);
        sQQFaceMap.put("[抱拳]", R.drawable.expression_84);
        sQQFaceMap.put("[勾引]", R.drawable.expression_85);
        sQQFaceMap.put("[拳头]", R.drawable.expression_86);
        sQQFaceMap.put("[差劲]", R.drawable.expression_87);
        sQQFaceMap.put("[爱你]", R.drawable.expression_88);
        sQQFaceMap.put("[NO]", R.drawable.expression_89);
        sQQFaceMap.put("[OK]", R.drawable.expression_90);
        sQQFaceMap.put("[爱情]", R.drawable.expression_91);
//        sQQFaceMap.put("[飞吻]", R.drawable.expression_92);
//        sQQFaceMap.put("[跳跳]", R.drawable.expression_93);
//        sQQFaceMap.put("[发抖]", R.drawable.expression_94);
//        sQQFaceMap.put("[怄火]", R.drawable.expression_95);
//        sQQFaceMap.put("[转圈]", R.drawable.expression_96);
//        sQQFaceMap.put("[磕头]", R.drawable.expression_97);
//        sQQFaceMap.put("[回头]", R.drawable.expression_98);
//        sQQFaceMap.put("[跳绳]", R.drawable.expression_99);
//        sQQFaceMap.put("[投降]", R.drawable.expression_100);



        sEmojisMap.append(0x00a9, R.drawable.emoji_00a9);
        sEmojisMap.append(0x00ae, R.drawable.emoji_00ae);
        sEmojisMap.append(0x203c, R.drawable.emoji_203c);
        sEmojisMap.append(0x2049, R.drawable.emoji_2049);
        sEmojisMap.append(0x2122, R.drawable.emoji_2122);
        sEmojisMap.append(0x2139, R.drawable.emoji_2139);
        sEmojisMap.append(0x2194, R.drawable.emoji_2194);
        sEmojisMap.append(0x2195, R.drawable.emoji_2195);
        sEmojisMap.append(0x2196, R.drawable.emoji_2196);
        sEmojisMap.append(0x2197, R.drawable.emoji_2197);
        sEmojisMap.append(0x2198, R.drawable.emoji_2198);
        sEmojisMap.append(0x2199, R.drawable.emoji_2199);
        sEmojisMap.append(0x21a9, R.drawable.emoji_21a9);
        sEmojisMap.append(0x21aa, R.drawable.emoji_21aa);
        sEmojisMap.append(0x231a, R.drawable.emoji_231a);
        sEmojisMap.append(0x231b, R.drawable.emoji_231b);
        sEmojisMap.append(0x23e9, R.drawable.emoji_23e9);
        sEmojisMap.append(0x23ea, R.drawable.emoji_23ea);
        sEmojisMap.append(0x23eb, R.drawable.emoji_23eb);
        sEmojisMap.append(0x23ec, R.drawable.emoji_23ec);
        sEmojisMap.append(0x23f0, R.drawable.emoji_23f0);
        sEmojisMap.append(0x23f3, R.drawable.emoji_23f3);
        sEmojisMap.append(0x24c2, R.drawable.emoji_24c2);
        sEmojisMap.append(0x25aa, R.drawable.emoji_25aa);
        sEmojisMap.append(0x25ab, R.drawable.emoji_25ab);
        sEmojisMap.append(0x25b6, R.drawable.emoji_25b6);
        sEmojisMap.append(0x25c0, R.drawable.emoji_25c0);
        sEmojisMap.append(0x25fb, R.drawable.emoji_25fb);
        sEmojisMap.append(0x25fc, R.drawable.emoji_25fc);
        sEmojisMap.append(0x25fd, R.drawable.emoji_25fd);
        sEmojisMap.append(0x25fe, R.drawable.emoji_25fe);
        sEmojisMap.append(0x2600, R.drawable.emoji_2600);
        sEmojisMap.append(0x2601, R.drawable.emoji_2601);
        sEmojisMap.append(0x260e, R.drawable.emoji_260e);
        sEmojisMap.append(0x2611, R.drawable.emoji_2611);
        sEmojisMap.append(0x2614, R.drawable.emoji_2614);
        sEmojisMap.append(0x2615, R.drawable.emoji_2615);
        sEmojisMap.append(0x261d, R.drawable.emoji_261d);
        sEmojisMap.append(0x263a, R.drawable.emoji_263a);
        sEmojisMap.append(0x2648, R.drawable.emoji_2648);
        sEmojisMap.append(0x2649, R.drawable.emoji_2649);
        sEmojisMap.append(0x264a, R.drawable.emoji_264a);
        sEmojisMap.append(0x264b, R.drawable.emoji_264b);
        sEmojisMap.append(0x264c, R.drawable.emoji_264c);
        sEmojisMap.append(0x264d, R.drawable.emoji_264d);
        sEmojisMap.append(0x264e, R.drawable.emoji_264e);
        sEmojisMap.append(0x264f, R.drawable.emoji_264f);
        sEmojisMap.append(0x2650, R.drawable.emoji_2650);
        sEmojisMap.append(0x2651, R.drawable.emoji_2651);
        sEmojisMap.append(0x2652, R.drawable.emoji_2652);
        sEmojisMap.append(0x2653, R.drawable.emoji_2653);
        sEmojisMap.append(0x2660, R.drawable.emoji_2660);
        sEmojisMap.append(0x2663, R.drawable.emoji_2663);
        sEmojisMap.append(0x2665, R.drawable.emoji_2665);
        sEmojisMap.append(0x2666, R.drawable.emoji_2666);
        sEmojisMap.append(0x2668, R.drawable.emoji_2668);
        sEmojisMap.append(0x267b, R.drawable.emoji_267b);
        sEmojisMap.append(0x267f, R.drawable.emoji_267f);
        sEmojisMap.append(0x2693, R.drawable.emoji_2693);
        sEmojisMap.append(0x26a0, R.drawable.emoji_26a0);
        sEmojisMap.append(0x26a1, R.drawable.emoji_26a1);
        sEmojisMap.append(0x26aa, R.drawable.emoji_26aa);
        sEmojisMap.append(0x26ab, R.drawable.emoji_26ab);
        sEmojisMap.append(0x26bd, R.drawable.emoji_26bd);
        sEmojisMap.append(0x26be, R.drawable.emoji_26be);
        sEmojisMap.append(0x26c4, R.drawable.emoji_26c4);
        sEmojisMap.append(0x26c5, R.drawable.emoji_26c5);
        sEmojisMap.append(0x26ce, R.drawable.emoji_26ce);
        sEmojisMap.append(0x26d4, R.drawable.emoji_26d4);
        sEmojisMap.append(0x26ea, R.drawable.emoji_26ea);
        sEmojisMap.append(0x26f2, R.drawable.emoji_26f2);
        sEmojisMap.append(0x26f3, R.drawable.emoji_26f3);
        sEmojisMap.append(0x26f5, R.drawable.emoji_26f5);
        sEmojisMap.append(0x26fa, R.drawable.emoji_26fa);
        sEmojisMap.append(0x26fd, R.drawable.emoji_26fd);
        sEmojisMap.append(0x2702, R.drawable.emoji_2702);
        sEmojisMap.append(0x2705, R.drawable.emoji_2705);
        sEmojisMap.append(0x2708, R.drawable.emoji_2708);
        sEmojisMap.append(0x2709, R.drawable.emoji_2709);
        sEmojisMap.append(0x270a, R.drawable.emoji_270a);
        sEmojisMap.append(0x270b, R.drawable.emoji_270b);
        sEmojisMap.append(0x270c, R.drawable.emoji_270c);
        sEmojisMap.append(0x270f, R.drawable.emoji_270f);
        sEmojisMap.append(0x2712, R.drawable.emoji_2712);
        sEmojisMap.append(0x2714, R.drawable.emoji_2714);
        sEmojisMap.append(0x2716, R.drawable.emoji_2716);
        sEmojisMap.append(0x2728, R.drawable.emoji_2728);
        sEmojisMap.append(0x2733, R.drawable.emoji_2733);
        sEmojisMap.append(0x2734, R.drawable.emoji_2734);
        sEmojisMap.append(0x2744, R.drawable.emoji_2744);
        sEmojisMap.append(0x2747, R.drawable.emoji_2747);
        sEmojisMap.append(0x274c, R.drawable.emoji_274c);
        sEmojisMap.append(0x274e, R.drawable.emoji_274e);
        sEmojisMap.append(0x2753, R.drawable.emoji_2753);
        sEmojisMap.append(0x2754, R.drawable.emoji_2754);
        sEmojisMap.append(0x2755, R.drawable.emoji_2755);
        sEmojisMap.append(0x2757, R.drawable.emoji_2757);
        sEmojisMap.append(0x2764, R.drawable.emoji_2764);
        sEmojisMap.append(0x2795, R.drawable.emoji_2795);
        sEmojisMap.append(0x2796, R.drawable.emoji_2796);
        sEmojisMap.append(0x2797, R.drawable.emoji_2797);
        sEmojisMap.append(0x27a1, R.drawable.emoji_27a1);
        sEmojisMap.append(0x27b0, R.drawable.emoji_27b0);
        sEmojisMap.append(0x27bf, R.drawable.emoji_27bf);
        sEmojisMap.append(0x2934, R.drawable.emoji_2934);
        sEmojisMap.append(0x2935, R.drawable.emoji_2935);
        sEmojisMap.append(0x2b05, R.drawable.emoji_2b05);
        sEmojisMap.append(0x2b06, R.drawable.emoji_2b06);
        sEmojisMap.append(0x2b07, R.drawable.emoji_2b07);
        sEmojisMap.append(0x2b1b, R.drawable.emoji_2b1b);
        sEmojisMap.append(0x2b1c, R.drawable.emoji_2b1c);
        sEmojisMap.append(0x2b50, R.drawable.emoji_2b50);
        sEmojisMap.append(0x2b55, R.drawable.emoji_2b55);
        sEmojisMap.append(0x3030, R.drawable.emoji_3030);
        sEmojisMap.append(0x303d, R.drawable.emoji_303d);
        sEmojisMap.append(0x3297, R.drawable.emoji_3297);
        sEmojisMap.append(0x3299, R.drawable.emoji_3299);
        sEmojisMap.append(0x1f004, R.drawable.emoji_1f004);
        sEmojisMap.append(0x1f0cf, R.drawable.emoji_1f0cf);
        sEmojisMap.append(0x1f170, R.drawable.emoji_1f170);
        sEmojisMap.append(0x1f171, R.drawable.emoji_1f171);
        sEmojisMap.append(0x1f17e, R.drawable.emoji_1f17e);
        sEmojisMap.append(0x1f17f, R.drawable.emoji_1f17f);
        sEmojisMap.append(0x1f18e, R.drawable.emoji_1f18e);
        sEmojisMap.append(0x1f191, R.drawable.emoji_1f191);
        sEmojisMap.append(0x1f192, R.drawable.emoji_1f192);
        sEmojisMap.append(0x1f193, R.drawable.emoji_1f193);
        sEmojisMap.append(0x1f194, R.drawable.emoji_1f194);
        sEmojisMap.append(0x1f195, R.drawable.emoji_1f195);
        sEmojisMap.append(0x1f196, R.drawable.emoji_1f196);
        sEmojisMap.append(0x1f197, R.drawable.emoji_1f197);
        sEmojisMap.append(0x1f198, R.drawable.emoji_1f198);
        sEmojisMap.append(0x1f199, R.drawable.emoji_1f199);
        sEmojisMap.append(0x1f19a, R.drawable.emoji_1f19a);
        sEmojisMap.append(0x1f201, R.drawable.emoji_1f201);
        sEmojisMap.append(0x1f202, R.drawable.emoji_1f202);
        sEmojisMap.append(0x1f21a, R.drawable.emoji_1f21a);
        sEmojisMap.append(0x1f22f, R.drawable.emoji_1f22f);
        sEmojisMap.append(0x1f232, R.drawable.emoji_1f232);
        sEmojisMap.append(0x1f233, R.drawable.emoji_1f233);
        sEmojisMap.append(0x1f234, R.drawable.emoji_1f234);
        sEmojisMap.append(0x1f235, R.drawable.emoji_1f235);
        sEmojisMap.append(0x1f236, R.drawable.emoji_1f236);
        sEmojisMap.append(0x1f237, R.drawable.emoji_1f237);
        sEmojisMap.append(0x1f238, R.drawable.emoji_1f238);
        sEmojisMap.append(0x1f239, R.drawable.emoji_1f239);
        sEmojisMap.append(0x1f23a, R.drawable.emoji_1f23a);
        sEmojisMap.append(0x1f250, R.drawable.emoji_1f250);
        sEmojisMap.append(0x1f251, R.drawable.emoji_1f251);
        sEmojisMap.append(0x1f300, R.drawable.emoji_1f300);
        sEmojisMap.append(0x1f301, R.drawable.emoji_1f301);
        sEmojisMap.append(0x1f302, R.drawable.emoji_1f302);
        sEmojisMap.append(0x1f303, R.drawable.emoji_1f303);
        sEmojisMap.append(0x1f304, R.drawable.emoji_1f304);
        sEmojisMap.append(0x1f305, R.drawable.emoji_1f305);
        sEmojisMap.append(0x1f306, R.drawable.emoji_1f306);
        sEmojisMap.append(0x1f307, R.drawable.emoji_1f307);
        sEmojisMap.append(0x1f308, R.drawable.emoji_1f308);
        sEmojisMap.append(0x1f309, R.drawable.emoji_1f309);
        sEmojisMap.append(0x1f30a, R.drawable.emoji_1f30a);
        sEmojisMap.append(0x1f30b, R.drawable.emoji_1f30b);
        sEmojisMap.append(0x1f30c, R.drawable.emoji_1f30c);
        sEmojisMap.append(0x1f30d, R.drawable.emoji_1f30d);
        sEmojisMap.append(0x1f30e, R.drawable.emoji_1f30e);
        sEmojisMap.append(0x1f30f, R.drawable.emoji_1f30f);
        sEmojisMap.append(0x1f310, R.drawable.emoji_1f310);
        sEmojisMap.append(0x1f311, R.drawable.emoji_1f311);
        sEmojisMap.append(0x1f312, R.drawable.emoji_1f312);
        sEmojisMap.append(0x1f313, R.drawable.emoji_1f313);
        sEmojisMap.append(0x1f314, R.drawable.emoji_1f314);
        sEmojisMap.append(0x1f315, R.drawable.emoji_1f315);
        sEmojisMap.append(0x1f316, R.drawable.emoji_1f316);
        sEmojisMap.append(0x1f317, R.drawable.emoji_1f317);
        sEmojisMap.append(0x1f318, R.drawable.emoji_1f318);
        sEmojisMap.append(0x1f319, R.drawable.emoji_1f319);
        sEmojisMap.append(0x1f31a, R.drawable.emoji_1f31a);
        sEmojisMap.append(0x1f31b, R.drawable.emoji_1f31b);
        sEmojisMap.append(0x1f31c, R.drawable.emoji_1f31c);
        sEmojisMap.append(0x1f31d, R.drawable.emoji_1f31d);
        sEmojisMap.append(0x1f31e, R.drawable.emoji_1f31e);
        sEmojisMap.append(0x1f31f, R.drawable.emoji_1f31f);
        sEmojisMap.append(0x1f320, R.drawable.emoji_1f303);
        sEmojisMap.append(0x1f330, R.drawable.emoji_1f330);
        sEmojisMap.append(0x1f331, R.drawable.emoji_1f331);
        sEmojisMap.append(0x1f332, R.drawable.emoji_1f332);
        sEmojisMap.append(0x1f333, R.drawable.emoji_1f333);
        sEmojisMap.append(0x1f334, R.drawable.emoji_1f334);
        sEmojisMap.append(0x1f335, R.drawable.emoji_1f335);
        sEmojisMap.append(0x1f337, R.drawable.emoji_1f337);
        sEmojisMap.append(0x1f338, R.drawable.emoji_1f338);
        sEmojisMap.append(0x1f339, R.drawable.emoji_1f339);
        sEmojisMap.append(0x1f33a, R.drawable.emoji_1f33a);
        sEmojisMap.append(0x1f33b, R.drawable.emoji_1f33b);
        sEmojisMap.append(0x1f33c, R.drawable.emoji_1f33c);
        sEmojisMap.append(0x1f33d, R.drawable.emoji_1f33d);
        sEmojisMap.append(0x1f33e, R.drawable.emoji_1f33e);
        sEmojisMap.append(0x1f33f, R.drawable.emoji_1f33f);
        sEmojisMap.append(0x1f340, R.drawable.emoji_1f340);
        sEmojisMap.append(0x1f341, R.drawable.emoji_1f341);
        sEmojisMap.append(0x1f342, R.drawable.emoji_1f342);
        sEmojisMap.append(0x1f343, R.drawable.emoji_1f343);
        sEmojisMap.append(0x1f344, R.drawable.emoji_1f344);
        sEmojisMap.append(0x1f345, R.drawable.emoji_1f345);
        sEmojisMap.append(0x1f346, R.drawable.emoji_1f346);
        sEmojisMap.append(0x1f347, R.drawable.emoji_1f347);
        sEmojisMap.append(0x1f348, R.drawable.emoji_1f348);
        sEmojisMap.append(0x1f349, R.drawable.emoji_1f349);
        sEmojisMap.append(0x1f34a, R.drawable.emoji_1f34a);
        sEmojisMap.append(0x1f34b, R.drawable.emoji_1f34b);
        sEmojisMap.append(0x1f34c, R.drawable.emoji_1f34c);
        sEmojisMap.append(0x1f34d, R.drawable.emoji_1f34d);
        sEmojisMap.append(0x1f34e, R.drawable.emoji_1f34e);
        sEmojisMap.append(0x1f34f, R.drawable.emoji_1f34f);
        sEmojisMap.append(0x1f350, R.drawable.emoji_1f350);
        sEmojisMap.append(0x1f351, R.drawable.emoji_1f351);
        sEmojisMap.append(0x1f352, R.drawable.emoji_1f352);
        sEmojisMap.append(0x1f353, R.drawable.emoji_1f353);
        sEmojisMap.append(0x1f354, R.drawable.emoji_1f354);
        sEmojisMap.append(0x1f355, R.drawable.emoji_1f355);
        sEmojisMap.append(0x1f356, R.drawable.emoji_1f356);
        sEmojisMap.append(0x1f357, R.drawable.emoji_1f357);
        sEmojisMap.append(0x1f358, R.drawable.emoji_1f358);
        sEmojisMap.append(0x1f359, R.drawable.emoji_1f359);
        sEmojisMap.append(0x1f35a, R.drawable.emoji_1f35a);
        sEmojisMap.append(0x1f35b, R.drawable.emoji_1f35b);
        sEmojisMap.append(0x1f35c, R.drawable.emoji_1f35c);
        sEmojisMap.append(0x1f35d, R.drawable.emoji_1f35d);
        sEmojisMap.append(0x1f35e, R.drawable.emoji_1f35e);
        sEmojisMap.append(0x1f35f, R.drawable.emoji_1f35f);
        sEmojisMap.append(0x1f360, R.drawable.emoji_1f360);
        sEmojisMap.append(0x1f361, R.drawable.emoji_1f361);
        sEmojisMap.append(0x1f362, R.drawable.emoji_1f362);
        sEmojisMap.append(0x1f363, R.drawable.emoji_1f363);
        sEmojisMap.append(0x1f364, R.drawable.emoji_1f364);
        sEmojisMap.append(0x1f365, R.drawable.emoji_1f365);
        sEmojisMap.append(0x1f366, R.drawable.emoji_1f366);
        sEmojisMap.append(0x1f367, R.drawable.emoji_1f367);
        sEmojisMap.append(0x1f368, R.drawable.emoji_1f368);
        sEmojisMap.append(0x1f369, R.drawable.emoji_1f369);
        sEmojisMap.append(0x1f36a, R.drawable.emoji_1f36a);
        sEmojisMap.append(0x1f36b, R.drawable.emoji_1f36b);
        sEmojisMap.append(0x1f36c, R.drawable.emoji_1f36c);
        sEmojisMap.append(0x1f36d, R.drawable.emoji_1f36d);
        sEmojisMap.append(0x1f36e, R.drawable.emoji_1f36e);
        sEmojisMap.append(0x1f36f, R.drawable.emoji_1f36f);
        sEmojisMap.append(0x1f370, R.drawable.emoji_1f370);
        sEmojisMap.append(0x1f371, R.drawable.emoji_1f371);
        sEmojisMap.append(0x1f372, R.drawable.emoji_1f372);
        sEmojisMap.append(0x1f373, R.drawable.emoji_1f373);
        sEmojisMap.append(0x1f374, R.drawable.emoji_1f374);
        sEmojisMap.append(0x1f375, R.drawable.emoji_1f375);
        sEmojisMap.append(0x1f376, R.drawable.emoji_1f376);
        sEmojisMap.append(0x1f377, R.drawable.emoji_1f377);
        sEmojisMap.append(0x1f378, R.drawable.emoji_1f378);
        sEmojisMap.append(0x1f379, R.drawable.emoji_1f379);
        sEmojisMap.append(0x1f37a, R.drawable.emoji_1f37a);
        sEmojisMap.append(0x1f37b, R.drawable.emoji_1f37b);
        sEmojisMap.append(0x1f37c, R.drawable.emoji_1f37c);
        sEmojisMap.append(0x1f380, R.drawable.emoji_1f380);
        sEmojisMap.append(0x1f381, R.drawable.emoji_1f381);
        sEmojisMap.append(0x1f382, R.drawable.emoji_1f382);
        sEmojisMap.append(0x1f383, R.drawable.emoji_1f383);
        sEmojisMap.append(0x1f384, R.drawable.emoji_1f384);
        sEmojisMap.append(0x1f385, R.drawable.emoji_1f385);
        sEmojisMap.append(0x1f386, R.drawable.emoji_1f386);
        sEmojisMap.append(0x1f387, R.drawable.emoji_1f387);
        sEmojisMap.append(0x1f388, R.drawable.emoji_1f388);
        sEmojisMap.append(0x1f389, R.drawable.emoji_1f389);
        sEmojisMap.append(0x1f38a, R.drawable.emoji_1f38a);
        sEmojisMap.append(0x1f38b, R.drawable.emoji_1f38b);
        sEmojisMap.append(0x1f38c, R.drawable.emoji_1f38c);
        sEmojisMap.append(0x1f38d, R.drawable.emoji_1f38d);
        sEmojisMap.append(0x1f38e, R.drawable.emoji_1f38e);
        sEmojisMap.append(0x1f38f, R.drawable.emoji_1f38f);
        sEmojisMap.append(0x1f390, R.drawable.emoji_1f390);
        sEmojisMap.append(0x1f391, R.drawable.emoji_1f391);
        sEmojisMap.append(0x1f392, R.drawable.emoji_1f392);
        sEmojisMap.append(0x1f393, R.drawable.emoji_1f393);
        sEmojisMap.append(0x1f3a0, R.drawable.emoji_1f3a0);
        sEmojisMap.append(0x1f3a1, R.drawable.emoji_1f3a1);
        sEmojisMap.append(0x1f3a2, R.drawable.emoji_1f3a2);
        sEmojisMap.append(0x1f3a3, R.drawable.emoji_1f3a3);
        sEmojisMap.append(0x1f3a4, R.drawable.emoji_1f3a4);
        sEmojisMap.append(0x1f3a5, R.drawable.emoji_1f3a5);
        sEmojisMap.append(0x1f3a6, R.drawable.emoji_1f3a6);
        sEmojisMap.append(0x1f3a7, R.drawable.emoji_1f3a7);
        sEmojisMap.append(0x1f3a8, R.drawable.emoji_1f3a8);
        sEmojisMap.append(0x1f3a9, R.drawable.emoji_1f3a9);
        sEmojisMap.append(0x1f3aa, R.drawable.emoji_1f3aa);
        sEmojisMap.append(0x1f3ab, R.drawable.emoji_1f3ab);
        sEmojisMap.append(0x1f3ac, R.drawable.emoji_1f3ac);
        sEmojisMap.append(0x1f3ad, R.drawable.emoji_1f3ad);
        sEmojisMap.append(0x1f3ae, R.drawable.emoji_1f3ae);
        sEmojisMap.append(0x1f3af, R.drawable.emoji_1f3af);
        sEmojisMap.append(0x1f3b0, R.drawable.emoji_1f3b0);
        sEmojisMap.append(0x1f3b1, R.drawable.emoji_1f3b1);
        sEmojisMap.append(0x1f3b2, R.drawable.emoji_1f3b2);
        sEmojisMap.append(0x1f3b3, R.drawable.emoji_1f3b3);
        sEmojisMap.append(0x1f3b4, R.drawable.emoji_1f3b4);
        sEmojisMap.append(0x1f3b5, R.drawable.emoji_1f3b5);
        sEmojisMap.append(0x1f3b6, R.drawable.emoji_1f3b6);
        sEmojisMap.append(0x1f3b7, R.drawable.emoji_1f3b7);
        sEmojisMap.append(0x1f3b8, R.drawable.emoji_1f3b8);
        sEmojisMap.append(0x1f3b9, R.drawable.emoji_1f3b9);
        sEmojisMap.append(0x1f3ba, R.drawable.emoji_1f3ba);
        sEmojisMap.append(0x1f3bb, R.drawable.emoji_1f3bb);
        sEmojisMap.append(0x1f3bc, R.drawable.emoji_1f3bc);
        sEmojisMap.append(0x1f3bd, R.drawable.emoji_1f3bd);
        sEmojisMap.append(0x1f3be, R.drawable.emoji_1f3be);
        sEmojisMap.append(0x1f3bf, R.drawable.emoji_1f3bf);
        sEmojisMap.append(0x1f3c0, R.drawable.emoji_1f3c0);
        sEmojisMap.append(0x1f3c1, R.drawable.emoji_1f3c1);
        sEmojisMap.append(0x1f3c2, R.drawable.emoji_1f3c2);
        sEmojisMap.append(0x1f3c3, R.drawable.emoji_1f3c3);
        sEmojisMap.append(0x1f3c4, R.drawable.emoji_1f3c4);
        sEmojisMap.append(0x1f3c6, R.drawable.emoji_1f3c6);
        sEmojisMap.append(0x1f3c7, R.drawable.emoji_1f3c7);
        sEmojisMap.append(0x1f3c8, R.drawable.emoji_1f3c8);
        sEmojisMap.append(0x1f3c9, R.drawable.emoji_1f3c9);
        sEmojisMap.append(0x1f3ca, R.drawable.emoji_1f3ca);
        sEmojisMap.append(0x1f3e0, R.drawable.emoji_1f3e0);
        sEmojisMap.append(0x1f3e1, R.drawable.emoji_1f3e1);
        sEmojisMap.append(0x1f3e2, R.drawable.emoji_1f3e2);
        sEmojisMap.append(0x1f3e3, R.drawable.emoji_1f3e3);
        sEmojisMap.append(0x1f3e4, R.drawable.emoji_1f3e4);
        sEmojisMap.append(0x1f3e5, R.drawable.emoji_1f3e5);
        sEmojisMap.append(0x1f3e6, R.drawable.emoji_1f3e6);
        sEmojisMap.append(0x1f3e7, R.drawable.emoji_1f3e7);
        sEmojisMap.append(0x1f3e8, R.drawable.emoji_1f3e8);
        sEmojisMap.append(0x1f3e9, R.drawable.emoji_1f3e9);
        sEmojisMap.append(0x1f3ea, R.drawable.emoji_1f3ea);
        sEmojisMap.append(0x1f3eb, R.drawable.emoji_1f3eb);
        sEmojisMap.append(0x1f3ec, R.drawable.emoji_1f3ec);
        sEmojisMap.append(0x1f3ed, R.drawable.emoji_1f3ed);
        sEmojisMap.append(0x1f3ee, R.drawable.emoji_1f3ee);
        sEmojisMap.append(0x1f3ef, R.drawable.emoji_1f3ef);
        sEmojisMap.append(0x1f3f0, R.drawable.emoji_1f3f0);
        sEmojisMap.append(0x1f400, R.drawable.emoji_1f400);
        sEmojisMap.append(0x1f401, R.drawable.emoji_1f401);
        sEmojisMap.append(0x1f402, R.drawable.emoji_1f402);
        sEmojisMap.append(0x1f403, R.drawable.emoji_1f403);
        sEmojisMap.append(0x1f404, R.drawable.emoji_1f404);
        sEmojisMap.append(0x1f405, R.drawable.emoji_1f405);
        sEmojisMap.append(0x1f406, R.drawable.emoji_1f406);
        sEmojisMap.append(0x1f407, R.drawable.emoji_1f407);
        sEmojisMap.append(0x1f408, R.drawable.emoji_1f408);
        sEmojisMap.append(0x1f409, R.drawable.emoji_1f409);
        sEmojisMap.append(0x1f40a, R.drawable.emoji_1f40a);
        sEmojisMap.append(0x1f40b, R.drawable.emoji_1f40b);
        sEmojisMap.append(0x1f40c, R.drawable.emoji_1f40c);
        sEmojisMap.append(0x1f40d, R.drawable.emoji_1f40d);
        sEmojisMap.append(0x1f40e, R.drawable.emoji_1f40e);
        sEmojisMap.append(0x1f40f, R.drawable.emoji_1f40f);
        sEmojisMap.append(0x1f410, R.drawable.emoji_1f410);
        sEmojisMap.append(0x1f411, R.drawable.emoji_1f411);
        sEmojisMap.append(0x1f412, R.drawable.emoji_1f412);
        sEmojisMap.append(0x1f413, R.drawable.emoji_1f413);
        sEmojisMap.append(0x1f414, R.drawable.emoji_1f414);
        sEmojisMap.append(0x1f415, R.drawable.emoji_1f415);
        sEmojisMap.append(0x1f416, R.drawable.emoji_1f416);
        sEmojisMap.append(0x1f417, R.drawable.emoji_1f417);
        sEmojisMap.append(0x1f418, R.drawable.emoji_1f418);
        sEmojisMap.append(0x1f419, R.drawable.emoji_1f419);
        sEmojisMap.append(0x1f41a, R.drawable.emoji_1f41a);
        sEmojisMap.append(0x1f41b, R.drawable.emoji_1f41b);
        sEmojisMap.append(0x1f41c, R.drawable.emoji_1f41c);
        sEmojisMap.append(0x1f41d, R.drawable.emoji_1f41d);
        sEmojisMap.append(0x1f41e, R.drawable.emoji_1f41e);
        sEmojisMap.append(0x1f41f, R.drawable.emoji_1f41f);
        sEmojisMap.append(0x1f420, R.drawable.emoji_1f420);
        sEmojisMap.append(0x1f421, R.drawable.emoji_1f421);
        sEmojisMap.append(0x1f422, R.drawable.emoji_1f422);
        sEmojisMap.append(0x1f423, R.drawable.emoji_1f423);
        sEmojisMap.append(0x1f424, R.drawable.emoji_1f424);
        sEmojisMap.append(0x1f425, R.drawable.emoji_1f425);
        sEmojisMap.append(0x1f426, R.drawable.emoji_1f426);
        sEmojisMap.append(0x1f427, R.drawable.emoji_1f427);
        sEmojisMap.append(0x1f428, R.drawable.emoji_1f428);
        sEmojisMap.append(0x1f429, R.drawable.emoji_1f429);
        sEmojisMap.append(0x1f42a, R.drawable.emoji_1f42a);
        sEmojisMap.append(0x1f42b, R.drawable.emoji_1f42b);
        sEmojisMap.append(0x1f42c, R.drawable.emoji_1f42c);
        sEmojisMap.append(0x1f42d, R.drawable.emoji_1f42d);
        sEmojisMap.append(0x1f42e, R.drawable.emoji_1f42e);
        sEmojisMap.append(0x1f42f, R.drawable.emoji_1f42f);
        sEmojisMap.append(0x1f430, R.drawable.emoji_1f430);
        sEmojisMap.append(0x1f431, R.drawable.emoji_1f431);
        sEmojisMap.append(0x1f432, R.drawable.emoji_1f432);
        sEmojisMap.append(0x1f433, R.drawable.emoji_1f433);
        sEmojisMap.append(0x1f434, R.drawable.emoji_1f434);
        sEmojisMap.append(0x1f435, R.drawable.emoji_1f435);
        sEmojisMap.append(0x1f436, R.drawable.emoji_1f436);
        sEmojisMap.append(0x1f437, R.drawable.emoji_1f437);
        sEmojisMap.append(0x1f438, R.drawable.emoji_1f438);
        sEmojisMap.append(0x1f439, R.drawable.emoji_1f439);
        sEmojisMap.append(0x1f43a, R.drawable.emoji_1f43a);
        sEmojisMap.append(0x1f43b, R.drawable.emoji_1f43b);
        sEmojisMap.append(0x1f43c, R.drawable.emoji_1f43c);
        sEmojisMap.append(0x1f43d, R.drawable.emoji_1f43d);
        sEmojisMap.append(0x1f43e, R.drawable.emoji_1f43e);
        sEmojisMap.append(0x1f440, R.drawable.emoji_1f440);
        sEmojisMap.append(0x1f442, R.drawable.emoji_1f442);
        sEmojisMap.append(0x1f443, R.drawable.emoji_1f443);
        sEmojisMap.append(0x1f444, R.drawable.emoji_1f444);
        sEmojisMap.append(0x1f445, R.drawable.emoji_1f445);
        sEmojisMap.append(0x1f446, R.drawable.emoji_1f446);
        sEmojisMap.append(0x1f447, R.drawable.emoji_1f447);
        sEmojisMap.append(0x1f448, R.drawable.emoji_1f448);
        sEmojisMap.append(0x1f449, R.drawable.emoji_1f449);
        sEmojisMap.append(0x1f44a, R.drawable.emoji_1f44a);
        sEmojisMap.append(0x1f44b, R.drawable.emoji_1f44b);
        sEmojisMap.append(0x1f44c, R.drawable.emoji_1f44c);
        sEmojisMap.append(0x1f44d, R.drawable.emoji_1f44d);
        sEmojisMap.append(0x1f44e, R.drawable.emoji_1f44e);
        sEmojisMap.append(0x1f44f, R.drawable.emoji_1f44f);
        sEmojisMap.append(0x1f450, R.drawable.emoji_1f450);
        sEmojisMap.append(0x1f451, R.drawable.emoji_1f451);
        sEmojisMap.append(0x1f452, R.drawable.emoji_1f452);
        sEmojisMap.append(0x1f453, R.drawable.emoji_1f453);
        sEmojisMap.append(0x1f454, R.drawable.emoji_1f454);
        sEmojisMap.append(0x1f455, R.drawable.emoji_1f455);
        sEmojisMap.append(0x1f456, R.drawable.emoji_1f456);
        sEmojisMap.append(0x1f457, R.drawable.emoji_1f457);
        sEmojisMap.append(0x1f458, R.drawable.emoji_1f458);
        sEmojisMap.append(0x1f459, R.drawable.emoji_1f459);
        sEmojisMap.append(0x1f45a, R.drawable.emoji_1f45a);
        sEmojisMap.append(0x1f45b, R.drawable.emoji_1f45b);
        sEmojisMap.append(0x1f45c, R.drawable.emoji_1f45c);
        sEmojisMap.append(0x1f45d, R.drawable.emoji_1f45d);
        sEmojisMap.append(0x1f45e, R.drawable.emoji_1f45e);
        sEmojisMap.append(0x1f45f, R.drawable.emoji_1f45f);
        sEmojisMap.append(0x1f460, R.drawable.emoji_1f460);
        sEmojisMap.append(0x1f461, R.drawable.emoji_1f461);
        sEmojisMap.append(0x1f462, R.drawable.emoji_1f462);
        sEmojisMap.append(0x1f463, R.drawable.emoji_1f463);
        sEmojisMap.append(0x1f464, R.drawable.emoji_1f464);
        sEmojisMap.append(0x1f465, R.drawable.emoji_1f465);
        sEmojisMap.append(0x1f466, R.drawable.emoji_1f466);
        sEmojisMap.append(0x1f467, R.drawable.emoji_1f467);
        sEmojisMap.append(0x1f468, R.drawable.emoji_1f468);
        sEmojisMap.append(0x1f469, R.drawable.emoji_1f469);
        sEmojisMap.append(0x1f46a, R.drawable.emoji_1f46a);
        sEmojisMap.append(0x1f46b, R.drawable.emoji_1f46b);
        sEmojisMap.append(0x1f46c, R.drawable.emoji_1f46c);
        sEmojisMap.append(0x1f46d, R.drawable.emoji_1f46d);
        sEmojisMap.append(0x1f46e, R.drawable.emoji_1f46e);
        sEmojisMap.append(0x1f46f, R.drawable.emoji_1f46f);
        sEmojisMap.append(0x1f470, R.drawable.emoji_1f470);
        sEmojisMap.append(0x1f471, R.drawable.emoji_1f471);
        sEmojisMap.append(0x1f472, R.drawable.emoji_1f472);
        sEmojisMap.append(0x1f473, R.drawable.emoji_1f473);
        sEmojisMap.append(0x1f474, R.drawable.emoji_1f474);
        sEmojisMap.append(0x1f475, R.drawable.emoji_1f475);
        sEmojisMap.append(0x1f476, R.drawable.emoji_1f476);
        sEmojisMap.append(0x1f477, R.drawable.emoji_1f477);
        sEmojisMap.append(0x1f478, R.drawable.emoji_1f478);
        sEmojisMap.append(0x1f479, R.drawable.emoji_1f479);
        sEmojisMap.append(0x1f47a, R.drawable.emoji_1f47a);
        sEmojisMap.append(0x1f47b, R.drawable.emoji_1f47b);
        sEmojisMap.append(0x1f47c, R.drawable.emoji_1f47c);
        sEmojisMap.append(0x1f47d, R.drawable.emoji_1f47d);
        sEmojisMap.append(0x1f47e, R.drawable.emoji_1f47e);
        sEmojisMap.append(0x1f47f, R.drawable.emoji_1f47f);
        sEmojisMap.append(0x1f480, R.drawable.emoji_1f480);
        sEmojisMap.append(0x1f481, R.drawable.emoji_1f481);
        sEmojisMap.append(0x1f482, R.drawable.emoji_1f482);
        sEmojisMap.append(0x1f483, R.drawable.emoji_1f483);
        sEmojisMap.append(0x1f484, R.drawable.emoji_1f484);
        sEmojisMap.append(0x1f485, R.drawable.emoji_1f485);
        sEmojisMap.append(0x1f486, R.drawable.emoji_1f486);
        sEmojisMap.append(0x1f487, R.drawable.emoji_1f487);
        sEmojisMap.append(0x1f488, R.drawable.emoji_1f488);
        sEmojisMap.append(0x1f489, R.drawable.emoji_1f489);
        sEmojisMap.append(0x1f48a, R.drawable.emoji_1f48a);
        sEmojisMap.append(0x1f48b, R.drawable.emoji_1f48b);
        sEmojisMap.append(0x1f48c, R.drawable.emoji_1f48c);
        sEmojisMap.append(0x1f48d, R.drawable.emoji_1f48d);
        sEmojisMap.append(0x1f48e, R.drawable.emoji_1f48e);
        sEmojisMap.append(0x1f48f, R.drawable.emoji_1f48f);
        sEmojisMap.append(0x1f490, R.drawable.emoji_1f490);
        sEmojisMap.append(0x1f491, R.drawable.emoji_1f491);
        sEmojisMap.append(0x1f492, R.drawable.emoji_1f492);
        sEmojisMap.append(0x1f493, R.drawable.emoji_1f493);
        sEmojisMap.append(0x1f494, R.drawable.emoji_1f494);
        sEmojisMap.append(0x1f495, R.drawable.emoji_1f495);
        sEmojisMap.append(0x1f496, R.drawable.emoji_1f496);
        sEmojisMap.append(0x1f497, R.drawable.emoji_1f497);
        sEmojisMap.append(0x1f498, R.drawable.emoji_1f498);
        sEmojisMap.append(0x1f499, R.drawable.emoji_1f499);
        sEmojisMap.append(0x1f49a, R.drawable.emoji_1f49a);
        sEmojisMap.append(0x1f49b, R.drawable.emoji_1f49b);
        sEmojisMap.append(0x1f49c, R.drawable.emoji_1f49c);
        sEmojisMap.append(0x1f49d, R.drawable.emoji_1f49d);
        sEmojisMap.append(0x1f49e, R.drawable.emoji_1f49e);
        sEmojisMap.append(0x1f49f, R.drawable.emoji_1f49f);
        sEmojisMap.append(0x1f4a0, R.drawable.emoji_1f4a0);
        sEmojisMap.append(0x1f4a1, R.drawable.emoji_1f4a1);
        sEmojisMap.append(0x1f4a2, R.drawable.emoji_1f4a2);
        sEmojisMap.append(0x1f4a3, R.drawable.emoji_1f4a3);
        sEmojisMap.append(0x1f4a4, R.drawable.emoji_1f4a4);
        sEmojisMap.append(0x1f4a5, R.drawable.emoji_1f4a5);
        sEmojisMap.append(0x1f4a6, R.drawable.emoji_1f4a6);
        sEmojisMap.append(0x1f4a7, R.drawable.emoji_1f4a7);
        sEmojisMap.append(0x1f4a8, R.drawable.emoji_1f4a8);
        sEmojisMap.append(0x1f4a9, R.drawable.emoji_1f4a9);
        sEmojisMap.append(0x1f4aa, R.drawable.emoji_1f4aa);
        sEmojisMap.append(0x1f4ab, R.drawable.emoji_1f4ab);
        sEmojisMap.append(0x1f4ac, R.drawable.emoji_1f4ac);
        sEmojisMap.append(0x1f4ad, R.drawable.emoji_1f4ad);
        sEmojisMap.append(0x1f4ae, R.drawable.emoji_1f4ae);
        sEmojisMap.append(0x1f4af, R.drawable.emoji_1f4af);
        sEmojisMap.append(0x1f4b0, R.drawable.emoji_1f4b0);
        sEmojisMap.append(0x1f4b1, R.drawable.emoji_1f4b1);
        sEmojisMap.append(0x1f4b2, R.drawable.emoji_1f4b2);
        sEmojisMap.append(0x1f4b3, R.drawable.emoji_1f4b3);
        sEmojisMap.append(0x1f4b4, R.drawable.emoji_1f4b4);
        sEmojisMap.append(0x1f4b5, R.drawable.emoji_1f4b5);
        sEmojisMap.append(0x1f4b6, R.drawable.emoji_1f4b6);
        sEmojisMap.append(0x1f4b7, R.drawable.emoji_1f4b7);
        sEmojisMap.append(0x1f4b8, R.drawable.emoji_1f4b8);
        sEmojisMap.append(0x1f4b9, R.drawable.emoji_1f4b9);
        sEmojisMap.append(0x1f4ba, R.drawable.emoji_1f4ba);
        sEmojisMap.append(0x1f4bb, R.drawable.emoji_1f4bb);
        sEmojisMap.append(0x1f4bc, R.drawable.emoji_1f4bc);
        sEmojisMap.append(0x1f4bd, R.drawable.emoji_1f4bd);
        sEmojisMap.append(0x1f4be, R.drawable.emoji_1f4be);
        sEmojisMap.append(0x1f4bf, R.drawable.emoji_1f4bf);
        sEmojisMap.append(0x1f4c0, R.drawable.emoji_1f4c0);
        sEmojisMap.append(0x1f4c1, R.drawable.emoji_1f4c1);
        sEmojisMap.append(0x1f4c2, R.drawable.emoji_1f4c2);
        sEmojisMap.append(0x1f4c3, R.drawable.emoji_1f4c3);
        sEmojisMap.append(0x1f4c4, R.drawable.emoji_1f4c4);
        sEmojisMap.append(0x1f4c5, R.drawable.emoji_1f4c5);
        sEmojisMap.append(0x1f4c6, R.drawable.emoji_1f4c6);
        sEmojisMap.append(0x1f4c7, R.drawable.emoji_1f4c7);
        sEmojisMap.append(0x1f4c8, R.drawable.emoji_1f4c8);
        sEmojisMap.append(0x1f4c9, R.drawable.emoji_1f4c9);
        sEmojisMap.append(0x1f4ca, R.drawable.emoji_1f4ca);
        sEmojisMap.append(0x1f4cb, R.drawable.emoji_1f4cb);
        sEmojisMap.append(0x1f4cc, R.drawable.emoji_1f4cc);
        sEmojisMap.append(0x1f4cd, R.drawable.emoji_1f4cd);
        sEmojisMap.append(0x1f4ce, R.drawable.emoji_1f4ce);
        sEmojisMap.append(0x1f4cf, R.drawable.emoji_1f4cf);
        sEmojisMap.append(0x1f4d0, R.drawable.emoji_1f4d0);
        sEmojisMap.append(0x1f4d1, R.drawable.emoji_1f4d1);
        sEmojisMap.append(0x1f4d2, R.drawable.emoji_1f4d2);
        sEmojisMap.append(0x1f4d3, R.drawable.emoji_1f4d3);
        sEmojisMap.append(0x1f4d4, R.drawable.emoji_1f4d4);
        sEmojisMap.append(0x1f4d5, R.drawable.emoji_1f4d5);
        sEmojisMap.append(0x1f4d6, R.drawable.emoji_1f4d6);
        sEmojisMap.append(0x1f4d7, R.drawable.emoji_1f4d7);
        sEmojisMap.append(0x1f4d8, R.drawable.emoji_1f4d8);
        sEmojisMap.append(0x1f4d9, R.drawable.emoji_1f4d9);
        sEmojisMap.append(0x1f4da, R.drawable.emoji_1f4da);
        sEmojisMap.append(0x1f4db, R.drawable.emoji_1f4db);
        sEmojisMap.append(0x1f4dc, R.drawable.emoji_1f4dc);
        sEmojisMap.append(0x1f4dd, R.drawable.emoji_1f4dd);
        sEmojisMap.append(0x1f4de, R.drawable.emoji_1f4de);
        sEmojisMap.append(0x1f4df, R.drawable.emoji_1f4df);
        sEmojisMap.append(0x1f4e0, R.drawable.emoji_1f4e0);
        sEmojisMap.append(0x1f4e1, R.drawable.emoji_1f4e1);
        sEmojisMap.append(0x1f4e2, R.drawable.emoji_1f4e2);
        sEmojisMap.append(0x1f4e3, R.drawable.emoji_1f4e3);
        sEmojisMap.append(0x1f4e4, R.drawable.emoji_1f4e4);
        sEmojisMap.append(0x1f4e5, R.drawable.emoji_1f4e5);
        sEmojisMap.append(0x1f4e6, R.drawable.emoji_1f4e6);
        sEmojisMap.append(0x1f4e7, R.drawable.emoji_1f4e7);
        sEmojisMap.append(0x1f4e8, R.drawable.emoji_1f4e8);
        sEmojisMap.append(0x1f4e9, R.drawable.emoji_1f4e9);
        sEmojisMap.append(0x1f4ea, R.drawable.emoji_1f4ea);
        sEmojisMap.append(0x1f4eb, R.drawable.emoji_1f4eb);
        sEmojisMap.append(0x1f4ec, R.drawable.emoji_1f4ec);
        sEmojisMap.append(0x1f4ed, R.drawable.emoji_1f4ed);
        sEmojisMap.append(0x1f4ee, R.drawable.emoji_1f4ee);
        sEmojisMap.append(0x1f4ef, R.drawable.emoji_1f4ef);
        sEmojisMap.append(0x1f4f0, R.drawable.emoji_1f4f0);
        sEmojisMap.append(0x1f4f1, R.drawable.emoji_1f4f1);
        sEmojisMap.append(0x1f4f2, R.drawable.emoji_1f4f2);
        sEmojisMap.append(0x1f4f3, R.drawable.emoji_1f4f3);
        sEmojisMap.append(0x1f4f4, R.drawable.emoji_1f4f4);
        sEmojisMap.append(0x1f4f5, R.drawable.emoji_1f4f5);
        sEmojisMap.append(0x1f4f6, R.drawable.emoji_1f4f6);
        sEmojisMap.append(0x1f4f7, R.drawable.emoji_1f4f7);
        sEmojisMap.append(0x1f4f9, R.drawable.emoji_1f4f9);
        sEmojisMap.append(0x1f4fa, R.drawable.emoji_1f4fa);
        sEmojisMap.append(0x1f4fb, R.drawable.emoji_1f4fb);
        sEmojisMap.append(0x1f4fc, R.drawable.emoji_1f4fc);
        sEmojisMap.append(0x1f500, R.drawable.emoji_1f500);
        sEmojisMap.append(0x1f501, R.drawable.emoji_1f501);
        sEmojisMap.append(0x1f502, R.drawable.emoji_1f502);
        sEmojisMap.append(0x1f503, R.drawable.emoji_1f503);
        sEmojisMap.append(0x1f504, R.drawable.emoji_1f504);
        sEmojisMap.append(0x1f505, R.drawable.emoji_1f505);
        sEmojisMap.append(0x1f506, R.drawable.emoji_1f506);
        sEmojisMap.append(0x1f507, R.drawable.emoji_1f507);
        sEmojisMap.append(0x1f508, R.drawable.emoji_1f508);
        sEmojisMap.append(0x1f509, R.drawable.emoji_1f509);
        sEmojisMap.append(0x1f50a, R.drawable.emoji_1f50a);
        sEmojisMap.append(0x1f50b, R.drawable.emoji_1f50b);
        sEmojisMap.append(0x1f50c, R.drawable.emoji_1f50c);
        sEmojisMap.append(0x1f50d, R.drawable.emoji_1f50d);
        sEmojisMap.append(0x1f50e, R.drawable.emoji_1f50e);
        sEmojisMap.append(0x1f50f, R.drawable.emoji_1f50f);
        sEmojisMap.append(0x1f510, R.drawable.emoji_1f510);
        sEmojisMap.append(0x1f511, R.drawable.emoji_1f511);
        sEmojisMap.append(0x1f512, R.drawable.emoji_1f512);
        sEmojisMap.append(0x1f513, R.drawable.emoji_1f513);
        sEmojisMap.append(0x1f514, R.drawable.emoji_1f514);
        sEmojisMap.append(0x1f515, R.drawable.emoji_1f515);
        sEmojisMap.append(0x1f516, R.drawable.emoji_1f516);
        sEmojisMap.append(0x1f517, R.drawable.emoji_1f517);
        sEmojisMap.append(0x1f518, R.drawable.emoji_1f518);
        sEmojisMap.append(0x1f519, R.drawable.emoji_1f519);
        sEmojisMap.append(0x1f51a, R.drawable.emoji_1f51a);
        sEmojisMap.append(0x1f51b, R.drawable.emoji_1f51b);
        sEmojisMap.append(0x1f51c, R.drawable.emoji_1f51c);
        sEmojisMap.append(0x1f51d, R.drawable.emoji_1f51d);
        sEmojisMap.append(0x1f51e, R.drawable.emoji_1f51e);
        sEmojisMap.append(0x1f51f, R.drawable.emoji_1f51f);
        sEmojisMap.append(0x1f520, R.drawable.emoji_1f520);
        sEmojisMap.append(0x1f521, R.drawable.emoji_1f521);
        sEmojisMap.append(0x1f522, R.drawable.emoji_1f522);
        sEmojisMap.append(0x1f523, R.drawable.emoji_1f523);
        sEmojisMap.append(0x1f524, R.drawable.emoji_1f524);
        sEmojisMap.append(0x1f525, R.drawable.emoji_1f525);
        sEmojisMap.append(0x1f526, R.drawable.emoji_1f526);
        sEmojisMap.append(0x1f527, R.drawable.emoji_1f527);
        sEmojisMap.append(0x1f528, R.drawable.emoji_1f528);
        sEmojisMap.append(0x1f529, R.drawable.emoji_1f529);
        sEmojisMap.append(0x1f52a, R.drawable.emoji_1f52a);
        sEmojisMap.append(0x1f52b, R.drawable.emoji_1f52b);
        sEmojisMap.append(0x1f52c, R.drawable.emoji_1f52c);
        sEmojisMap.append(0x1f52d, R.drawable.emoji_1f52d);
        sEmojisMap.append(0x1f52e, R.drawable.emoji_1f52e);
        sEmojisMap.append(0x1f52f, R.drawable.emoji_1f52f);
        sEmojisMap.append(0x1f530, R.drawable.emoji_1f530);
        sEmojisMap.append(0x1f531, R.drawable.emoji_1f531);
        sEmojisMap.append(0x1f532, R.drawable.emoji_1f532);
        sEmojisMap.append(0x1f533, R.drawable.emoji_1f533);
        sEmojisMap.append(0x1f534, R.drawable.emoji_1f534);
        sEmojisMap.append(0x1f535, R.drawable.emoji_1f535);
        sEmojisMap.append(0x1f536, R.drawable.emoji_1f536);
        sEmojisMap.append(0x1f537, R.drawable.emoji_1f537);
        sEmojisMap.append(0x1f538, R.drawable.emoji_1f538);
        sEmojisMap.append(0x1f539, R.drawable.emoji_1f539);
        sEmojisMap.append(0x1f53a, R.drawable.emoji_1f53a);
        sEmojisMap.append(0x1f53b, R.drawable.emoji_1f53b);
        sEmojisMap.append(0x1f53c, R.drawable.emoji_1f53c);
        sEmojisMap.append(0x1f53d, R.drawable.emoji_1f53d);
        sEmojisMap.append(0x1f550, R.drawable.emoji_1f550);
        sEmojisMap.append(0x1f551, R.drawable.emoji_1f551);
        sEmojisMap.append(0x1f552, R.drawable.emoji_1f552);
        sEmojisMap.append(0x1f553, R.drawable.emoji_1f553);
        sEmojisMap.append(0x1f554, R.drawable.emoji_1f554);
        sEmojisMap.append(0x1f555, R.drawable.emoji_1f555);
        sEmojisMap.append(0x1f556, R.drawable.emoji_1f556);
        sEmojisMap.append(0x1f557, R.drawable.emoji_1f557);
        sEmojisMap.append(0x1f558, R.drawable.emoji_1f558);
        sEmojisMap.append(0x1f559, R.drawable.emoji_1f559);
        sEmojisMap.append(0x1f55a, R.drawable.emoji_1f55a);
        sEmojisMap.append(0x1f55b, R.drawable.emoji_1f55b);
        sEmojisMap.append(0x1f55c, R.drawable.emoji_1f55c);
        sEmojisMap.append(0x1f55d, R.drawable.emoji_1f55d);
        sEmojisMap.append(0x1f55e, R.drawable.emoji_1f55e);
        sEmojisMap.append(0x1f55f, R.drawable.emoji_1f55f);
        sEmojisMap.append(0x1f560, R.drawable.emoji_1f560);
        sEmojisMap.append(0x1f561, R.drawable.emoji_1f561);
        sEmojisMap.append(0x1f562, R.drawable.emoji_1f562);
        sEmojisMap.append(0x1f563, R.drawable.emoji_1f563);
        sEmojisMap.append(0x1f564, R.drawable.emoji_1f564);
        sEmojisMap.append(0x1f565, R.drawable.emoji_1f565);
        sEmojisMap.append(0x1f566, R.drawable.emoji_1f566);
        sEmojisMap.append(0x1f567, R.drawable.emoji_1f567);
        sEmojisMap.append(0x1f5fb, R.drawable.emoji_1f5fb);
        sEmojisMap.append(0x1f5fc, R.drawable.emoji_1f5fc);
        sEmojisMap.append(0x1f5fd, R.drawable.emoji_1f5fd);
        sEmojisMap.append(0x1f5fe, R.drawable.emoji_1f5fe);
        sEmojisMap.append(0x1f5ff, R.drawable.emoji_1f5ff);
        sEmojisMap.append(0x1f600, R.drawable.emoji_1f600);
        sEmojisMap.append(0x1f601, R.drawable.emoji_1f601);
        sEmojisMap.append(0x1f602, R.drawable.emoji_1f602);
        sEmojisMap.append(0x1f603, R.drawable.emoji_1f603);
        sEmojisMap.append(0x1f604, R.drawable.emoji_1f604);
        sEmojisMap.append(0x1f605, R.drawable.emoji_1f605);
        sEmojisMap.append(0x1f606, R.drawable.emoji_1f606);
        sEmojisMap.append(0x1f607, R.drawable.emoji_1f607);
        sEmojisMap.append(0x1f608, R.drawable.emoji_1f608);
        sEmojisMap.append(0x1f609, R.drawable.emoji_1f609);
        sEmojisMap.append(0x1f60a, R.drawable.emoji_1f60a);
        sEmojisMap.append(0x1f60b, R.drawable.emoji_1f60b);
        sEmojisMap.append(0x1f60c, R.drawable.emoji_1f60c);
        sEmojisMap.append(0x1f60d, R.drawable.emoji_1f60d);
        sEmojisMap.append(0x1f60e, R.drawable.emoji_1f60e);
        sEmojisMap.append(0x1f60f, R.drawable.emoji_1f60f);
        sEmojisMap.append(0x1f610, R.drawable.emoji_1f610);
        sEmojisMap.append(0x1f611, R.drawable.emoji_1f611);
        sEmojisMap.append(0x1f612, R.drawable.emoji_1f612);
        sEmojisMap.append(0x1f613, R.drawable.emoji_1f613);
        sEmojisMap.append(0x1f614, R.drawable.emoji_1f614);
        sEmojisMap.append(0x1f615, R.drawable.emoji_1f615);
        sEmojisMap.append(0x1f616, R.drawable.emoji_1f616);
        sEmojisMap.append(0x1f617, R.drawable.emoji_1f617);
        sEmojisMap.append(0x1f618, R.drawable.emoji_1f618);
        sEmojisMap.append(0x1f619, R.drawable.emoji_1f619);
        sEmojisMap.append(0x1f61a, R.drawable.emoji_1f61a);
        sEmojisMap.append(0x1f61b, R.drawable.emoji_1f61b);
        sEmojisMap.append(0x1f61c, R.drawable.emoji_1f61c);
        sEmojisMap.append(0x1f61d, R.drawable.emoji_1f61d);
        sEmojisMap.append(0x1f61e, R.drawable.emoji_1f61e);
        sEmojisMap.append(0x1f61f, R.drawable.emoji_1f61f);
        sEmojisMap.append(0x1f620, R.drawable.emoji_1f620);
        sEmojisMap.append(0x1f621, R.drawable.emoji_1f621);
        sEmojisMap.append(0x1f622, R.drawable.emoji_1f622);
        sEmojisMap.append(0x1f623, R.drawable.emoji_1f623);
        sEmojisMap.append(0x1f624, R.drawable.emoji_1f624);
        sEmojisMap.append(0x1f625, R.drawable.emoji_1f625);
        sEmojisMap.append(0x1f626, R.drawable.emoji_1f626);
        sEmojisMap.append(0x1f627, R.drawable.emoji_1f627);
        sEmojisMap.append(0x1f628, R.drawable.emoji_1f628);
        sEmojisMap.append(0x1f629, R.drawable.emoji_1f629);
        sEmojisMap.append(0x1f62a, R.drawable.emoji_1f62a);
        sEmojisMap.append(0x1f62b, R.drawable.emoji_1f62b);
        sEmojisMap.append(0x1f62c, R.drawable.emoji_1f62c);
        sEmojisMap.append(0x1f62d, R.drawable.emoji_1f62d);
        sEmojisMap.append(0x1f62e, R.drawable.emoji_1f62e);
        sEmojisMap.append(0x1f62f, R.drawable.emoji_1f62f);
        sEmojisMap.append(0x1f630, R.drawable.emoji_1f630);
        sEmojisMap.append(0x1f631, R.drawable.emoji_1f631);
        sEmojisMap.append(0x1f632, R.drawable.emoji_1f632);
        sEmojisMap.append(0x1f633, R.drawable.emoji_1f633);
        sEmojisMap.append(0x1f634, R.drawable.emoji_1f634);
        sEmojisMap.append(0x1f635, R.drawable.emoji_1f635);
        sEmojisMap.append(0x1f636, R.drawable.emoji_1f636);
        sEmojisMap.append(0x1f637, R.drawable.emoji_1f637);
        sEmojisMap.append(0x1f638, R.drawable.emoji_1f638);
        sEmojisMap.append(0x1f639, R.drawable.emoji_1f639);
        sEmojisMap.append(0x1f63a, R.drawable.emoji_1f63a);
        sEmojisMap.append(0x1f63b, R.drawable.emoji_1f63b);
        sEmojisMap.append(0x1f63c, R.drawable.emoji_1f63c);
        sEmojisMap.append(0x1f63d, R.drawable.emoji_1f63d);
        sEmojisMap.append(0x1f63e, R.drawable.emoji_1f63e);
        sEmojisMap.append(0x1f63f, R.drawable.emoji_1f63f);
        sEmojisMap.append(0x1f640, R.drawable.emoji_1f640);
        sEmojisMap.append(0x1f645, R.drawable.emoji_1f645);
        sEmojisMap.append(0x1f646, R.drawable.emoji_1f646);
        sEmojisMap.append(0x1f647, R.drawable.emoji_1f647);
        sEmojisMap.append(0x1f648, R.drawable.emoji_1f648);
        sEmojisMap.append(0x1f649, R.drawable.emoji_1f649);
        sEmojisMap.append(0x1f64a, R.drawable.emoji_1f64a);
        sEmojisMap.append(0x1f64b, R.drawable.emoji_1f64b);
        sEmojisMap.append(0x1f64c, R.drawable.emoji_1f64c);
        sEmojisMap.append(0x1f64d, R.drawable.emoji_1f64d);
        sEmojisMap.append(0x1f64e, R.drawable.emoji_1f64e);
        sEmojisMap.append(0x1f64f, R.drawable.emoji_1f64f);
        sEmojisMap.append(0x1f680, R.drawable.emoji_1f680);
        sEmojisMap.append(0x1f681, R.drawable.emoji_1f681);
        sEmojisMap.append(0x1f682, R.drawable.emoji_1f682);
        sEmojisMap.append(0x1f683, R.drawable.emoji_1f683);
        sEmojisMap.append(0x1f684, R.drawable.emoji_1f684);
        sEmojisMap.append(0x1f685, R.drawable.emoji_1f685);
        sEmojisMap.append(0x1f686, R.drawable.emoji_1f686);
        sEmojisMap.append(0x1f687, R.drawable.emoji_1f687);
        sEmojisMap.append(0x1f688, R.drawable.emoji_1f688);
        sEmojisMap.append(0x1f689, R.drawable.emoji_1f689);
        sEmojisMap.append(0x1f68a, R.drawable.emoji_1f68a);
        sEmojisMap.append(0x1f68b, R.drawable.emoji_1f68b);
        sEmojisMap.append(0x1f68c, R.drawable.emoji_1f68c);
        sEmojisMap.append(0x1f68d, R.drawable.emoji_1f68d);
        sEmojisMap.append(0x1f68e, R.drawable.emoji_1f68e);
        sEmojisMap.append(0x1f68f, R.drawable.emoji_1f68f);
        sEmojisMap.append(0x1f690, R.drawable.emoji_1f690);
        sEmojisMap.append(0x1f691, R.drawable.emoji_1f691);
        sEmojisMap.append(0x1f692, R.drawable.emoji_1f692);
        sEmojisMap.append(0x1f693, R.drawable.emoji_1f693);
        sEmojisMap.append(0x1f694, R.drawable.emoji_1f694);
        sEmojisMap.append(0x1f695, R.drawable.emoji_1f695);
        sEmojisMap.append(0x1f696, R.drawable.emoji_1f696);
        sEmojisMap.append(0x1f697, R.drawable.emoji_1f697);
        sEmojisMap.append(0x1f698, R.drawable.emoji_1f698);
        sEmojisMap.append(0x1f699, R.drawable.emoji_1f699);
        sEmojisMap.append(0x1f69a, R.drawable.emoji_1f69a);
        sEmojisMap.append(0x1f69b, R.drawable.emoji_1f69b);
        sEmojisMap.append(0x1f69c, R.drawable.emoji_1f69c);
        sEmojisMap.append(0x1f69d, R.drawable.emoji_1f69d);
        sEmojisMap.append(0x1f69e, R.drawable.emoji_1f69e);
        sEmojisMap.append(0x1f69f, R.drawable.emoji_1f69f);
        sEmojisMap.append(0x1f6a0, R.drawable.emoji_1f6a0);
        sEmojisMap.append(0x1f6a1, R.drawable.emoji_1f6a1);
        sEmojisMap.append(0x1f6a2, R.drawable.emoji_1f6a2);
        sEmojisMap.append(0x1f6a3, R.drawable.emoji_1f6a3);
        sEmojisMap.append(0x1f6a4, R.drawable.emoji_1f6a4);
        sEmojisMap.append(0x1f6a5, R.drawable.emoji_1f6a5);
        sEmojisMap.append(0x1f6a6, R.drawable.emoji_1f6a6);
        sEmojisMap.append(0x1f6a7, R.drawable.emoji_1f6a7);
        sEmojisMap.append(0x1f6a8, R.drawable.emoji_1f6a8);
        sEmojisMap.append(0x1f6a9, R.drawable.emoji_1f6a9);
        sEmojisMap.append(0x1f6aa, R.drawable.emoji_1f6aa);
        sEmojisMap.append(0x1f6ab, R.drawable.emoji_1f6ab);
        sEmojisMap.append(0x1f6ac, R.drawable.emoji_1f6ac);
        sEmojisMap.append(0x1f6ad, R.drawable.emoji_1f6ad);
        sEmojisMap.append(0x1f6ae, R.drawable.emoji_1f6ae);
        sEmojisMap.append(0x1f6af, R.drawable.emoji_1f6af);
        sEmojisMap.append(0x1f6b0, R.drawable.emoji_1f6b0);
        sEmojisMap.append(0x1f6b1, R.drawable.emoji_1f6b1);
        sEmojisMap.append(0x1f6b2, R.drawable.emoji_1f6b2);
        sEmojisMap.append(0x1f6b3, R.drawable.emoji_1f6b3);
        sEmojisMap.append(0x1f6b4, R.drawable.emoji_1f6b4);
        sEmojisMap.append(0x1f6b5, R.drawable.emoji_1f6b5);
        sEmojisMap.append(0x1f6b6, R.drawable.emoji_1f6b6);
        sEmojisMap.append(0x1f6b7, R.drawable.emoji_1f6b7);
        sEmojisMap.append(0x1f6b8, R.drawable.emoji_1f6b8);
        sEmojisMap.append(0x1f6b9, R.drawable.emoji_1f6b9);
        sEmojisMap.append(0x1f6ba, R.drawable.emoji_1f6ba);
        sEmojisMap.append(0x1f6bb, R.drawable.emoji_1f6bb);
        sEmojisMap.append(0x1f6bc, R.drawable.emoji_1f6bc);
        sEmojisMap.append(0x1f6bd, R.drawable.emoji_1f6bd);
        sEmojisMap.append(0x1f6be, R.drawable.emoji_1f6be);
        sEmojisMap.append(0x1f6bf, R.drawable.emoji_1f6bf);
        sEmojisMap.append(0x1f6c0, R.drawable.emoji_1f6c0);
        sEmojisMap.append(0x1f6c1, R.drawable.emoji_1f6c1);
        sEmojisMap.append(0x1f6c2, R.drawable.emoji_1f6c2);
        sEmojisMap.append(0x1f6c3, R.drawable.emoji_1f6c3);
        sEmojisMap.append(0x1f6c4, R.drawable.emoji_1f6c4);
        sEmojisMap.append(0x1f6c5, R.drawable.emoji_1f6c5);
    }

    private EmojiconHandler() {
    }


    private static int getEmojiResource(int codePoint) {
        return sEmojisMap.get(codePoint);
    }

    private static boolean isSoftBankEmoji(char c) {
        return ((c >> 12) == 0xe);
    }

    /**
     * @param context   Convert emoji characters of the given Spannable to the according emojicon.
     * @param text
     * @param emojiSize
     */
    public static void addEmojis(Context context, SpannableStringBuilder text, int emojiSize, int textSize) {
        addEmojis(context, text, emojiSize, textSize, 0, -1, false);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param index
     * @param length
     */
    public static void addEmojis(Context context, SpannableStringBuilder text, int emojiSize, int textSize, int index, int length) {
        addEmojis(context, text, emojiSize, textSize, index, length, false);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param useSystemDefault
     */
    public static void addEmojis(Context context, SpannableStringBuilder text, int emojiSize, int textSize, boolean useSystemDefault) {
        addEmojis(context, text, emojiSize, textSize, 0, -1, useSystemDefault);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param index
     * @param length
     * @param useSystemDefault
     */
    public static SpannableStringBuilder addEmojis(Context context, SpannableStringBuilder text, int emojiSize, int textSize, int index, int length, boolean useSystemDefault) {
        if (useSystemDefault) {
            return text;
        }

        int textLengthToProcess = calculateLegalTextLength(text, index, length);

        // remove spans throughout all text
        EmojiconSpan[] oldSpans = text.getSpans(0, text.length(), EmojiconSpan.class);
        for (EmojiconSpan oldSpan : oldSpans) {
            text.removeSpan(oldSpan);
        }

        int[] results = new int[3];
        String textStr = text.toString();

        int processIdx = index;
        while (processIdx < textLengthToProcess) {
//            LogUtil.e("processIdx = "+processIdx+"  textLengthToProcess = "+textLengthToProcess);
            boolean isEmoji = findEmoji(textStr, processIdx, textLengthToProcess, results);
            int skip = results[1];
            if (isEmoji) {
                int icon = results[0];
                boolean isQQFace = results[2] > 0;
                EmojiconSpan span = new EmojiconSpan(context, icon, (int) (emojiSize * EMOJIICON_SCALE),
                        (int) (emojiSize * EMOJIICON_SCALE));
                span.setTranslateY(isQQFace ? QQFACE_TRANSLATE_Y : EMOJIICON_TRANSLATE_Y);
                if (span.getCachedDrawable() == null) {
                    text.replace(processIdx, processIdx + skip, "..");
                    //重新计算字符串的合法长度
                    textLengthToProcess = calculateLegalTextLength(text, index, length);
                } else {
                    text.setSpan(span, processIdx, processIdx + skip, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            processIdx += skip;
        }
        return (SpannableStringBuilder) text.subSequence(index, processIdx);
    }

    /**
     * 判断文本位于start的字节是否为emoji。
     *
     * @param text
     * @param start
     * @param end
     * @param result 长度为3的数据。当第一位表示emoji的资源id，
     *               第二位表示emoji在原文本占位长度，
     *               第三位表示emoji类型是否位qq表情。
     * @return 如果是emoji，返回True。
     */
    public static boolean findEmoji(String text, int start, int end, int[] result) {
        int skip = 0;
        int icon = 0;
        char c = text.charAt(start);

        if (icon == 0) {
            int unicode = Character.codePointAt(text, start);
            skip = Character.charCount(unicode);

            if (unicode > 0xff) {
                icon = getEmojiResource(unicode);
            }

            if (icon == 0 && start + skip < end) {
                int followUnicode = Character.codePointAt(text, start + skip);
                if (followUnicode == 0x20e3) {
                    int followSkip = Character.charCount(followUnicode);
                    switch (unicode) {
                        case 0x0031:
                            icon = R.drawable.emoji_0031;
                            break;
                        case 0x0032:
                            icon = R.drawable.emoji_0032;
                            break;
                        case 0x0033:
                            icon = R.drawable.emoji_0033;
                            break;
                        case 0x0034:
                            icon = R.drawable.emoji_0034;
                            break;
                        case 0x0035:
                            icon = R.drawable.emoji_0035;
                            break;
                        case 0x0036:
                            icon = R.drawable.emoji_0036;
                            break;
                        case 0x0037:
                            icon = R.drawable.emoji_0037;
                            break;
                        case 0x0038:
                            icon = R.drawable.emoji_0038;
                            break;
                        case 0x0039:
                            icon = R.drawable.emoji_0039;
                            break;
                        case 0x0030:
                            icon = R.drawable.emoji_0030;
                            break;
                        case 0x0023:
                            icon = R.drawable.emoji_0023;
                            break;
                        default:
                            followSkip = 0;
                            break;
                    }
                    skip += followSkip;
                } else {
                    int followSkip = Character.charCount(followUnicode);
                    switch (unicode) {
                        case 0x1f1ef:
                            icon = (followUnicode == 0x1f1f5) ? R.drawable.emoji_1f1ef_1f1f5 : 0;
                            break;
                        case 0x1f1fa:
                            icon = (followUnicode == 0x1f1f8) ? R.drawable.emoji_1f1fa_1f1f8 : 0;
                            break;
                        case 0x1f1eb:
                            icon = (followUnicode == 0x1f1f7) ? R.drawable.emoji_1f1eb_1f1f7 : 0;
                            break;
                        case 0x1f1e9:
                            icon = (followUnicode == 0x1f1ea) ? R.drawable.emoji_1f1e9_1f1ea : 0;
                            break;
                        case 0x1f1ee:
                            icon = (followUnicode == 0x1f1f9) ? R.drawable.emoji_1f1ee_1f1f9 : 0;
                            break;
                        case 0x1f1ec:
                            icon = (followUnicode == 0x1f1e7) ? R.drawable.emoji_1f1ec_1f1e7 : 0;
                            break;
                        case 0x1f1ea:
                            icon = (followUnicode == 0x1f1f8) ? R.drawable.emoji_1f1ea_1f1f8 : 0;
                            break;
                        case 0x1f1f7:
                            icon = (followUnicode == 0x1f1fa) ? R.drawable.emoji_1f1f7_1f1fa : 0;
                            break;
                        case 0x1f1e8:
                            icon = (followUnicode == 0x1f1f3) ? R.drawable.emoji_1f1e8_1f1f3 : 0;
                            break;
                        case 0x1f1f0:
                            icon = (followUnicode == 0x1f1f7) ? R.drawable.emoji_1f1f0_1f1f7 : 0;
                            break;
                        default:
                            followSkip = 0;
                            break;
                    }
                    skip += followSkip;
                }
            }
        }

        boolean isQQFace = false;
        if (icon == 0) {
            if (c == '[') {
                int emojiCloseIndex = text.indexOf(']', start);
                if (emojiCloseIndex > 0 && emojiCloseIndex - start <= 4) {
                    CharSequence charSequence = text.subSequence(start, emojiCloseIndex + 1);
                    Integer value = sQQFaceMap.get(charSequence.toString());
                    if (value != null) {
                        icon = value;
                        skip = emojiCloseIndex + 1 - start;
                        isQQFace = true;
                    }
                }
            }
        }

        result[0] = icon;
        result[1] = skip;
        result[2] = isQQFace ? 1 : 0;

        return icon > 0;
    }

//    public static String findQQFaceFileName(String key) {
//        return mQQFaceFileNameList.get(key);
//    }

    private static int calculateLegalTextLength(SpannableStringBuilder text, int index, int length) {
        int textLength = text.length();
        int textLengthToProcessMax = textLength - index;
        return (length < 0 || length >= textLengthToProcessMax ? textLength : (length + index));
    }

//    public static List<QQFace> getQQFaceKeyList() {
//        return mQQFaceList;
//    }
//
//    public static boolean isQQFaceCodeExist(String qqFaceCode) {
//        return sQQFaceMap.get(qqFaceCode) != null;
//    }

    public static class QQFace {
        private String name;
        private int res;

        public QQFace(String name, int res) {
            this.name = name;
            this.res = res;
        }

        public String getName() {
            return name;
        }

        public int getRes() {
            return res;
        }
    }
}
