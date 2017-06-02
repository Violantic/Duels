/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.backend.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ethan on 2/26/2017.
 */
public class TimeUtil {

    public static String getTime() {
        long current = System.currentTimeMillis();
        Date date = new Date(current);
        SimpleDateFormat f = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String formatted = f.format(date);
        return formatted;
    }

}
