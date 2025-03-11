package com.exaroton.api;

@SuppressWarnings("unused")
public enum BrandColor {
    DARK(0x0f0f0f),
    LIGHT(0xffffff),
    MAIN(0x19ba19),
    DANGER(0xf91c1c),
    SUCCESS(MAIN.rgb),
    WARN(0xf97f12),
    LOADING(0x4c4c4c),
    ;

    private final int rgb;

    BrandColor(int rgb) {
        this.rgb = rgb;
    }

    public int getRGB() {
        return rgb;
    }
}
