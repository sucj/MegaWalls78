package icu.suc.megawalls78.util;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class Formatters {
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yy");
    public static final DateTimeFormatter MMSS = DateTimeFormatter.ofPattern("mm:ss");
    public static final DecimalFormat NUMBER = new DecimalFormat("0.#");
    public static final DecimalFormat NUMBER_Z = new DecimalFormat("0.0");
    public static final DecimalFormat COMPASS = new DecimalFormat("0");
}
